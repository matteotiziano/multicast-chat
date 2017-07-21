package view;

import java.io.IOException;

import model.Payload;
import controller.CausalHandler;
import controller.CommandListener;

public class Application implements CommandListener {

  private CausalHandler causalHandler;
  private ChatGUI chatGUI;

  public Application(String groupIp, int port, int ttl, int repPortServer,
                     int repPortClient) throws IOException {
    causalHandler =
        new CausalHandler(groupIp, port, ttl, repPortServer, repPortClient);
    chatGUI = new ChatGUI();
  }

  /**
   * Create process id by concat ip and name
   */
  public String createProcesId(String ip, String name) {
    return ip + Config.PROCESS_ID_TOKEN + name;
  }

  /**
   * Extract ip from process id
   */
  public String getIpFromProcessId(String processId) {
    return processId.split(Config.PROCESS_ID_TOKEN)[0];
  }

  /**
   * Add listeners and start application
   */
  public void start() {
    causalHandler.addCommandListener(this);
    chatGUI.addCommandListener(this);
    chatGUI.show();
  }

  public void receivedMessage(Payload payload) {
    chatGUI.appendMessage(payload);
  }

  public void logon(String name) { causalHandler.logon(name); }

  public void logoff() { causalHandler.logoff(); }

  public void sentMessage(String name, String text) {
    causalHandler.broadcast(name, text);
  }

  public static void main(String[] args) {
    String groupIp = args.length >= 1 ? args[0] : Config.GROUP_IP;
    int port = args.length >= 2 ? Integer.parseInt(args[1]) : Config.CHAT_PORT;
    int ttl = args.length >= 3 ? Integer.parseInt(args[2]) : Config.TTL;
    int repPortServer = args.length >= 4 ? Integer.parseInt(args[3])
                                         : Config.REPEATER_PORT_SERVER;
    int repPortClient = args.length >= 5 ? Integer.parseInt(args[4])
                                         : Config.REPEATER_PORT_CLIENT;

    try {
      Logger.println("Group " + groupIp + ":" + port + " with TTL " + ttl +
                     " and repeaters " + repPortServer + " " + repPortClient);
      new Application(groupIp, port, ttl, repPortServer, repPortClient).start();
    } catch (IOException e) {
      Logger.println("IOException in Application: " + e.getMessage());
    }
  }
}
