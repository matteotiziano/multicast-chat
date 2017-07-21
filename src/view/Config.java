package view;

public class Config {

  public static final boolean DEBUG = true;

  public static final boolean LOG_TO_FILE = false;
  public static final String LOG_FILE_PATH = "bin/log.txt";
  public static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String LOG_TOKEN = ": ";

  public static final String PROCESS_ID_TOKEN = "/";

  public static final double MISSING_PROB = 0.3; // set it >0 for testing

  public static final int TTL = 1; // max routers that can be traversed by the packet
  public static final String GROUP_IP = "230.0.0.1";
  public static final int CHAT_PORT = 4000;

  public static final int REPEATER_PORT_SERVER = 4001; // SERVER and CLIENT port should be the
  public static final int REPEATER_PORT_CLIENT = 4001; // different only for testing in localhost

  public static final int MAX_REQUEST_DIM = 512;
  public static final int MESSAGE_ID_REQ = 0;
  public static final int MESSAGE_DIGEST_REQ = 1;
  public static final String DIGEST_ALGORITHM = "SHA-1";

  public static final int MAX_BUFFER_DIM = 1024; // if too small messages won't even appear
  public static final int MAX_HISTORY_SIZE = 20; // max messages saved locally

  public static final int LESS = -2;
  public static final int LESS_EQ = -1;
  public static final int EQUAL = 0;
  public static final int GREATER_EQ = 1;
  public static final int GREATER = 2;
  public static final int CONCURRENT = 5;

  public static final String MESSAGE_AT = "@";
  public static final String MESSAGE_INCOMING = " < ";
  public static final String MESSAGE_OUTCOMING = " > ";

  public static final String GUI_DATE_FORMAT = "HH:mm:ss";
  public static final String GUI_CHOOSE = "Choose your nickname:";
  public static final String GUI_WELCOME = "Welcome to the group " + GROUP_IP + ":" + CHAT_PORT;
  public static final String GUI_TITLE = "Multicast Chat";
  public static final String GUI_ERROR = "Error";
  public static final String GUI_ERROR_MESSAGE = "Nick name can't be empty.";
  public static final int GUI_CHAT_FONT_SIZE = 14;
  public static final int GUI_CHAT_MARGIN = 10;
  public static final String GUI_CHAT_FONT = "Monospaced";
  public static final String GUI_CLOSE = "Exit";
  public static final String GUI_CLOSE_MESSAGE = "Are you sure you want to leave?";
  public static final String GUI_CHOOSE_NICK = "Choose your nick:";
  public static final int GUI_WIDTH = 640;
  public static final int GUI_HEIGHT = 480;
  public static final String GUI_BUTTON_FIRE = "Send";
  public static final String GUI_BUTTON_OK = "OK";
  public static final String GUI_NEW_LINE = "\n";
  public static final String GUI_QUIT = "Quit";
}
