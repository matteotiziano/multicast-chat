package controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import model.Message;
import model.Payload;
import view.Config;

public class IntegrityHandler {

  /**
   * Automatically extracts the payload from the Message and compute
   * its digest.
   *
   * @param message
   * @return
   * @throws IOException
   */
  public static byte[] getDigest(Message message) throws IOException {
    return IntegrityHandler.getDigest(message.getPayload());
  }

  /**
   * Compute digest from the payload of a Message.
   *
   * @param payload
   * @return
   * @throws IOException
   */
  public static byte[] getDigest(Payload payload) throws IOException {
    MessageDigest md;
    byte[] digest = {};
    try {
      md = MessageDigest.getInstance(Config.DIGEST_ALGORITHM);
      md.update(MessageHandler.getByteFrom(payload));
      digest = md.digest();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return digest;
  }

  /**
   * Returns true if the streams (digests) are equal.
   *
   * @param digest1
   * @param digest2
   * @return
   */
  public static boolean compareDigests(byte[] digest1, byte[] digest2) {
    return Arrays.equals(digest1, digest2);
  }

  /**
   * Returns the hexadecimal string corresponding the provided buffer
   *
   * @param buffer
   * @return
   */
  public static String byteToHexString(byte[] buffer) {
    byte singleChar = 0;
    if (buffer == null || buffer.length <= 0)
      return null;

    String entries[] = {"0", "1", "2", "3", "4", "5", "6", "7",
                        "8", "9", "a", "b", "c", "d", "e", "f"};
    StringBuffer out = new StringBuffer(buffer.length * 2);

    for (int i = 0; i < buffer.length; i++) {
      singleChar = (byte)(buffer[i] & 0xF0);
      singleChar = (byte)(singleChar >>> 4);
      singleChar = (byte)(singleChar & 0x0F);
      out.append(entries[(int)singleChar]);
      singleChar = (byte)(buffer[i] & 0x0F);
      out.append(entries[(int)singleChar]);
    }

    return new String(out);
  }

  /**
   * Converts a byte array to an integer
   *
   * @param buffer
   * @return
   */
  public static int byteToInt(byte[] buffer) {
    int n = (buffer[0] << 24) + ((buffer[1] & 0xFF) << 16) +
            ((buffer[2] & 0xFF) << 8) + (buffer[3] & 0xFF);
    return n;
  }

  /**
   * Converts an integer to a byte array
   *
   * @param n
   * @return
   */
  public static byte[] intToByte(int n) {
    byte[] buffer = new byte[] {(byte)(n >>> 24), (byte)(n >>> 16),
                                (byte)(n >>> 8), (byte)(n)};

    return buffer;
  }
}
