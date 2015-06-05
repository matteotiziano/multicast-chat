package model;

import java.io.Serializable;

public class Request implements Serializable {

	private static final long serialVersionUID = -9040479273965431406L;
	private int messageId;
	
	public Request(int messageId) {
		this.messageId = messageId;
	}

	public int getMessageId() {
		return messageId;
	}
	
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
}
