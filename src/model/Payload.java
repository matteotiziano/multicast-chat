package model;

import java.io.Serializable;
import java.util.Date;

public class Payload implements Serializable {

	private static final long serialVersionUID = 8554705838734687076L;
	private Date date;
	private String author;
	private String text;
	
	public Payload(String author, String text) {
		this.date = new Date();
		this.author = author;
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public String getAuthor() {
		return author;
	}

	public String getText() {
		return text;
	}
	
}
