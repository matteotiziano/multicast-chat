package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import model.Message;
import model.Request;
import view.Config;
import view.Logger;

public class RepeaterHandler extends Thread {
	
	private volatile boolean running = true;
	
	private Vector<Message> history;
	private ServerSocket serverSocket;
	
	public RepeaterHandler(int port) throws IOException {
		this.history = new Vector<Message>();
		this.serverSocket = new ServerSocket(port);
	}
	
	/**
	 * Update history of locally sent messages
	 * 
	 * @param message
	 */
	public synchronized void addToHistory(Message message) {
		if(history.size()>Config.MAX_HISTORY_SIZE) {
			// if history hits the limit then remove first message
			history.remove(0);
		}
		history.add(message);
	}
	
	/**
	 * Find message in history from its ID
	 * 
	 * @param messageId
	 * @return
	 */
	public synchronized Message findMessageInHistory(int messageId) {
		for(Message message : history) {
			if(message.getMessageId()==messageId) {
				return message;
			}
		}
		return null;
	}
	
	/**
	 * Stop service
	 * @throws IOException 
	 */
	public void logoff() throws IOException {
		running = false;
		if(!serverSocket.isClosed()) {
			serverSocket.close();
		}
	}
	
	/**
	 * Thread that listens for retransmission requests
	 */
	public void run() {
		Socket client;
		byte[] buffer = new byte[Config.MAX_REQUEST_DIM];
		Message message = null;
		
		while(running) {
			try {
				// blocking method, listen for requests
				client = serverSocket.accept();
				InputStream in = client.getInputStream();
				OutputStream out = client.getOutputStream();
				in.read(buffer);
				// get requested message from history and send it
				Request request = MessageHandler.getRequestMessageFrom(buffer);
				message = findMessageInHistory(request.getMessageId());
				
				Logger.println("Received request from " + client.getInetAddress().getHostAddress() + 
						" to retransmit message " + request.getMessageId() + " | found: " + (message!=null));
				
				out.write(MessageHandler.getByteFrom(message));
				out.flush();
				client.close();
				in.close();
				out.close();
			} catch (IOException e) {
				Logger.println("IOException in RepeaterHandler Thread " + e.getMessage());
			}
		}
	}

}
