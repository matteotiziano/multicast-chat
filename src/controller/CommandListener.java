package controller;

import model.Payload;

public interface CommandListener {
  public String createProcesId(String ip, String name);
  public String getIpFromProcessId(String processId);

  public void logon(String nickName);
  public void logoff();
  public void sentMessage(String nickName, String text);
  public void receivedMessage(Payload payload);
}
