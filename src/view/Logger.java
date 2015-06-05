package view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(Config.LOG_DATE_FORMAT);
	private static BufferedWriter out;

	public static void println(String line) {
		if(Config.DEBUG) {
			System.out.println(dateFormat.format(new Date()) + Config.LOG_TOKEN + line);
		}
		if(Config.LOG_TO_FILE) {
			write(line + "\n");
		}
	}

	public static void write(String line) {
		try {
			out = new BufferedWriter(new FileWriter(new File(Config.LOG_FILE_PATH), true));
			out.write(line);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
