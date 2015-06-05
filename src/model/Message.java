package model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.Vector;

import view.Config;
import controller.IntegrityHandler;

public class Message implements Serializable, Comparable<Message> {
	
	private static final long serialVersionUID = -1041013198570731751L;
	private String processId;
	private int messageId;
	private Payload payload;
	private VectorClock vectorClock;
	private byte[] digest;
	private Date receiveDate;
	
	public Message(String processId, int messageId, VectorClock vectorClock, Payload payload) throws IOException {
		this.processId = processId;
		this.messageId = messageId;
		this.payload = payload;
		this.vectorClock = vectorClock;
		this.digest = IntegrityHandler.getDigest(payload);
	}
	
	public String getProcessId() {
		return processId;
	}
	
	public Payload getPayload() {
		return payload;
	}
	
	public int getMessageId() {
		return messageId;
	}

	public VectorClock getPiggybackedVectorClock() {
		return vectorClock;
	}
	
	public byte[] getDigest() {
		return digest;
	}
	
	public Date getReceiveDate() {
		return receiveDate;
	}
	
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	
	public String toString() {
		return messageId + " by " + processId + " " + getPiggybackedVectorClock().toString();
	}

	public int compareTo(Message message) {
		Set<String> s1 = vectorClock.getVector().keySet();
		Set<String> s2 = message.getPiggybackedVectorClock().getVector().keySet();
		Vector<String> keys = unionSet(s1, s2);

		int v1, v2;
		int result = Config.CONCURRENT;
		
		int i = 0;
		for(String id : keys) {
			v1 = vectorClock.get(id);
			v2 = message.getPiggybackedVectorClock().get(id);
			
			if(v1<v2) {
				if(i==0 || result==Config.LESS) {
					result = Config.LESS;
				} else {
					if(result==Config.LESS_EQ || result==Config.EQUAL) {
						result = Config.LESS_EQ;
					} else {
						result = Config.CONCURRENT;
						break;
					}
				}
			} else if(v1>v2) {
				if(i==0 || result==Config.GREATER) {
					result = Config.GREATER;
				} else {
					if(result==Config.GREATER_EQ || result==Config.EQUAL) {
						result = Config.GREATER_EQ;
					} else {
						result = Config.CONCURRENT;
						break;
					}
				}
			} else {
				if(i==0 || result==Config.EQUAL) {
					result = Config.EQUAL;
				} else {
					if(result==Config.LESS_EQ || result==Config.LESS) {
						result = Config.LESS_EQ;
					} else if(result==Config.GREATER_EQ || result==Config.GREATER) {
						result = Config.GREATER_EQ;
					}
				}
			}
			i++;
		}
		
		if(result==Config.CONCURRENT) {
			result = receiveDate.compareTo(message.getReceiveDate());
		}
		
		return result;
	}
	
	private Vector<String> unionSet(Set<String> s1, Set<String> s2) {
		Vector<String> union = new Vector<String>();
		union.addAll(s1);
		for(String s : s2) {
			if(!union.contains(s)) {
				union.add(s);
			}
		}
		return union;
	}
	
}
