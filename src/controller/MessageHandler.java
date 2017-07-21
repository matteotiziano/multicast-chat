package controller;

import java.io.*;

import model.Message;
import model.Request;

public class MessageHandler {

  /**
   * Converts an object in a stream of bytes
   *
   * @param message
   * @return
   * @throws IOException
   */
  public static byte[] getByteFrom(Object obj) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = new ObjectOutputStream(bos);

    out.writeObject(obj);
    out.close();
    return bos.toByteArray();
  }

  /**
   * Converts a stream of bytes in a Message object
   *
   * @param buffer
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static Message getMessageFrom(byte[] buffer) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
    ObjectInputStream in = new ObjectInputStream(bis);

    Message message = null;
    try {
      message = (Message)in.readObject();
    } catch (ClassNotFoundException e) {
      // e.printStackTrace();
    }
    in.close();
    return message;
  }

  /**
   *
   * @param buffer
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static Request getRequestMessageFrom(byte[] buffer)
      throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
    ObjectInputStream in = new ObjectInputStream(bis);

    Request request = null;
    try {
      request = (Request)in.readObject();
    } catch (ClassNotFoundException e) {
      // e.printStackTrace();
    }
    in.close();
    return request;
  }
}
