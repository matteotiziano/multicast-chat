package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;

import model.Payload;
import model.Message;
import model.Request;
import model.VectorClock;
import view.Config;
import view.Logger;

public class CausalHandler extends Thread {

  private MulticastSocket multicastSocket;
  private InetAddress group;
  private String groupIp;
  private int port;
  private int repPortClient;
  private VectorClock vectorClock;
  private String ip;
  private String processId;

  private volatile boolean running = true;

  private RepeaterHandler repeaterHandler;
  private CommandListener commandListener = null;

  public CausalHandler(String groupIp, int port, int ttl, int repPortServer,
                       int repPortClient) throws IOException {
    this.groupIp = groupIp;
    this.port = port;
    this.group = InetAddress.getByName(groupIp);
    this.multicastSocket = new MulticastSocket(port);
    this.multicastSocket.setTimeToLive(ttl);
    this.repPortClient = repPortClient;
    this.vectorClock = new VectorClock();
    this.ip = InetAddress.getLocalHost().getHostAddress();

    this.repeaterHandler = new RepeaterHandler(repPortServer);
  }

  public void addCommandListener(CommandListener commandListener) {
    this.commandListener = commandListener;
  }

  /**
   *
   * @param name
   * @throws IOException
   */
  public void logon(String name) {
    if (commandListener != null) {
      processId = commandListener.createProcesId(ip, name);
      try {
        multicastSocket.joinGroup(group);
        Logger.println("Joined group " + groupIp + ":" + port + " with id " +
                       processId);
        repeaterHandler.start();
        this.start();
      } catch (IOException e) {
        Logger.println("IOException in CausalHandler logon: " + e.getMessage());
      }
    }
  }

  /**
   *
   * @throws IOException
   */
  public void logoff() {
    running = false;
    try {
      if (multicastSocket.isConnected()) {
        multicastSocket.leaveGroup(group);
      }
      repeaterHandler.logoff();
    } catch (IOException e) {
      Logger.println("IOException in CausalHandler logoff: " + e.getMessage());
    }
    if (!multicastSocket.isClosed()) {
      multicastSocket.close();
    }
    Logger.println("Left group " + groupIp + ":" + port);
  }

  /**
   * Broadcast message
   *
   * @param name
   * @param text
   * @throws IOException
   */
  public void broadcast(String name, String text) {
    Payload payload = new Payload(name, text);
    vectorClock.addOneTo(processId);
    int messageId = vectorClock.get(processId);
    VectorClock piggybackedVectorClock = (VectorClock)vectorClock.Clone();
    try {
      Message message =
          new Message(processId, messageId, piggybackedVectorClock, payload);
      byte[] buffer = MessageHandler.getByteFrom(message);
      DatagramPacket datagramPacket =
          new DatagramPacket(buffer, buffer.length, group, port);
      multicastSocket.send(datagramPacket);
      Logger.println("Broadcasted message " + message.toString());
      commandListener.receivedMessage(payload);
      repeaterHandler.addToHistory(message);
    } catch (IOException e) {
      Logger.println("IOException in CausalHandler broadcast: " +
                     e.getMessage());
    }
  }

  /**
   * Check if a received message is corrupted
   *
   * @param message
   * @return
   * @throws IOException
   */
  private boolean isCorrupted(Message message) throws IOException {
    byte[] receivedDigest = message.getDigest();
    byte[] computedDigest = IntegrityHandler.getDigest(message);

    return !(IntegrityHandler.compareDigests(receivedDigest, computedDigest));
  }

  /**
   * Request retransmission of message from its ID
   *
   * @param ip
   * @param messageId
   * @param length
   * @return
   * @throws IOException
   */
  private Message requestMessage(String ip, int messageId, int length)
      throws IOException {
    Request request = new Request(messageId);
    byte[] buffer = new byte[length];
    Message message;
    boolean corrupted;
    Socket socket = new Socket(ip, repPortClient);
    InputStream in = socket.getInputStream();
    OutputStream out = socket.getOutputStream();

    do {
      out.write(MessageHandler.getByteFrom(request));
      out.flush();
      in.read(buffer);
      message = MessageHandler.getMessageFrom(buffer);
      message.setReceiveDate(new Date());
      socket.close();
      in.close();
      out.close();

      corrupted = isCorrupted(message);
      if (corrupted) {
        Logger.println("Corrupted message " + message.toString());
      }
    } while (corrupted);
    return message;
  }

  /**
   * Thread that receives messages and manages the correct causal ordering
   */
  public void run() {
    Message receivedMessage, requestedMessage;
    int seen, sent;
    String process;

    while (running) {
      try {
        byte[] buffer = new byte[Config.MAX_BUFFER_DIM];
        DatagramPacket datagramPacket =
            new DatagramPacket(buffer, buffer.length);
        // blocking method
        multicastSocket.receive(datagramPacket);
        receivedMessage =
            MessageHandler.getMessageFrom(datagramPacket.getData());
        // check if the sender is different from current process
        if (!receivedMessage.getProcessId().equals(processId)) {
          // for testing we miss messages with a certain probability
          if (Math.random() > Config.MISSING_PROB) {
            Logger.println("Received message " + receivedMessage.toString());

            VectorClock piggybackedVectorClock =
                receivedMessage.getPiggybackedVectorClock();
            Iterator<String> it =
                piggybackedVectorClock.getProcessIds().iterator();

            // compare local and received vector clock
            while (it.hasNext()) {
              process = it.next();
              if (!process.equals(processId)) {
                seen = vectorClock.get(process);
                sent = piggybackedVectorClock.get(process);
                if (seen <= sent - 1) {
                  // if vector clocks do not agree, then pull messages
                  Logger.println("Lost messages " + (seen + 1) + " to " +
                                 (sent - 1) + " from " + process);
                  for (int messageId = seen + 1; messageId < sent;
                       messageId++) {
                    requestedMessage = requestMessage(
                        commandListener.getIpFromProcessId(process), messageId,
                        Config.MAX_BUFFER_DIM);
                    commandListener.receivedMessage(
                        requestedMessage.getPayload());
                    Logger.println("Pulled message " +
                                   requestedMessage.toString());
                  }
                  // push first received message
                  if (process.equals(receivedMessage.getProcessId())) {
                    commandListener.receivedMessage(
                        receivedMessage.getPayload());
                  }
                }
              }
            }
            // merge local and received vector clocks
            vectorClock =
                VectorClock.mergeClocks(piggybackedVectorClock, vectorClock);
          }
        }
      } catch (UnknownHostException e) {
        Logger.println("UnknownHostException in CausalHandler Thread: " +
                       e.getMessage());
      } catch (IOException e) {
        Logger.println("IOException in CausalHandler Thread " + e.getMessage());
      }
    }
  }
}
