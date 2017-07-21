package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.Payload;
import view.Config;
import controller.CommandListener;

public class ChatGUI implements ActionListener, KeyListener, WindowListener {

  private JFrame frame;
  private JPanel top;
  private JPanel bottom;

  private JLabel labelNickName;
  private JTextField inputNickName;
  private JButton buttonNickName;
  private JButton buttonQuit;
  private JButton buttonSend;
  private FlowLayout layout;
  private JScrollPane scroll;
  private JTextArea chat;
  private JTextField inputMessage;

  private static SimpleDateFormat dateFormat =
      new SimpleDateFormat(Config.GUI_DATE_FORMAT);
  private String name;

  private CommandListener commandListener = null;

  public ChatGUI() {
    frame = new JFrame(Config.GUI_TITLE);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);

    layout = new FlowLayout();
    layout.setAlignment(FlowLayout.LEFT);
    bottom = new JPanel(layout);
    top = new JPanel(layout);

    inputNickName = new JTextField(15);
    inputNickName.addKeyListener(this);
    buttonNickName = new JButton(Config.GUI_BUTTON_OK);
    buttonNickName.addActionListener(this);
    inputMessage = new JTextField(38);
    inputMessage.addKeyListener(this);
    labelNickName = new JLabel(Config.GUI_CHOOSE_NICK);
    buttonSend = new JButton(Config.GUI_BUTTON_FIRE);
    buttonSend.addActionListener(this);
    chat = new JTextArea();
    chat.setEditable(false);
    chat.setLineWrap(true);
    chat.setWrapStyleWord(true);
    chat.setFont(
        new Font(Config.GUI_CHAT_FONT, Font.PLAIN, Config.GUI_CHAT_FONT_SIZE));
    chat.setMargin(new Insets(Config.GUI_CHAT_MARGIN, Config.GUI_CHAT_MARGIN,
                              Config.GUI_CHAT_MARGIN, Config.GUI_CHAT_MARGIN));
    scroll = new JScrollPane(chat);
    buttonQuit = new JButton(Config.GUI_QUIT);
    buttonQuit.addActionListener(this);

    top.add(labelNickName);
    top.add(inputNickName);
    top.add(buttonNickName);
    frame.getContentPane().add(top, BorderLayout.NORTH);
    frame.getContentPane().add(scroll, BorderLayout.CENTER);

    bottom.add(inputMessage);
    bottom.add(buttonSend);
    bottom.add(buttonQuit);
    frame.getContentPane().add(bottom, BorderLayout.SOUTH);
    frame.addWindowListener(this);

    bottom.setVisible(false);
    frame.setVisible(false);
  }

  public void addCommandListener(CommandListener commandListener) {
    this.commandListener = commandListener;
  }

  public void show() {
    if (commandListener != null) {
      frame.setVisible(true);
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == buttonSend) {
      if (!inputMessage.getText().equals("")) {
        commandListener.sentMessage(name, inputMessage.getText());
        inputMessage.setText("");
        inputMessage.requestFocus();
      }

    } else if (e.getSource() == buttonNickName) {
      if (inputNickName.getText().equals("")) {
        error(inputNickName);
      } else {
        name = inputNickName.getText();
        labelNickName.setText(Config.GUI_WELCOME + " " + name);
        top.remove(inputNickName);
        top.remove(buttonNickName);

        commandListener.logon(name);
        bottom.setVisible(true);
        inputMessage.requestFocus();
      }
    } else if (e.getSource() == buttonQuit) {
      windowClosing(null);
    }
  }

  public void error(JTextField input) {
    JOptionPane.showMessageDialog(frame, Config.GUI_ERROR_MESSAGE,
                                  Config.GUI_ERROR, JOptionPane.ERROR_MESSAGE);
    input.requestFocus();
  }

  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      ActionEvent e1 = null;
      if (e.getSource() == inputMessage) {
        e1 = new ActionEvent(buttonSend, 0, "");
      } else if (e.getSource() == inputNickName) {
        e1 = new ActionEvent(buttonNickName, 0, "");
      }
      actionPerformed(e1);
    }
  }

  public void appendMessage(Payload payload) {
    chat.append(payload.getAuthor() + Config.MESSAGE_AT +
                dateFormat.format(payload.getDate()) +
                Config.MESSAGE_OUTCOMING + payload.getText() +
                Config.GUI_NEW_LINE);
    chat.setCaretPosition(chat.getText().length());
  }

  public void windowClosing(WindowEvent e) {
    String[] options = {"Yes", "No"};
    int i = JOptionPane.showOptionDialog(
        frame, Config.GUI_CLOSE_MESSAGE, Config.GUI_CLOSE,
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
        options[0]);
    if (i == 0) {
      commandListener.logoff();
      System.exit(0);
    }
  }

  // unimplemented interface(s) methods
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}
  public void windowActivated(WindowEvent e) {}
  public void windowDeactivated(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}
  public void windowClosed(WindowEvent e) {}
}
