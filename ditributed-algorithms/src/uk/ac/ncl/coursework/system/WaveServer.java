package uk.ac.ncl.coursework.system;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class WaveServer {
	private Map<WaveServer, Boolean> rec;
	private int neighNo;
	private int id;
	private LinkedList<WaveServer> incomingMsg;
	private boolean sentToSilent;
	/**
	 * @param id
	 *            indicates server id
	 */
	public WaveServer(int id) {
		this.id = id;
		this.neighNo = 0;
		this.sentToSilent = false;
		this.rec = new HashMap<WaveServer, Boolean>();
		this.incomingMsg = new LinkedList<WaveServer>();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the number of neighbors
	 */
	public int getNeighNo() {
		return neighNo;
	}

	/**
	 * @return true is message was sent to silent neighbor
	 */
	public boolean isSentToSilent() {
		return sentToSilent;
	}

	/**
	 * Sets <code>sentToSilent</code> to true
	 */
	public void sentToSilent() {
		this.sentToSilent = Boolean.TRUE;
	}

	/**
	 * @return incoming channel object
	 */
	public LinkedList<WaveServer> getIncomingMsg() {
		return incomingMsg;
	}

	/**
	 * @return The map of neighbors linking to the boolean value whether or not
	 *         the message was received from them.
	 */
	public Map<WaveServer, Boolean> getRec() {
		return rec;
	}

	/**
	 * Check if server received token from all neighbors.
	 * 
	 * @return false if at least one server has not sent yet else true
	 */
	public boolean hasReceivedAll() {
		for (Boolean received : rec.values()) {
			if (received == Boolean.FALSE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return if has silent neighbor then return true
	 */
	public boolean hasSilent() {
		int i = 0;
		/*
		 * if(this.neighNo == 1) { return Boolean.TRUE; }
		 */
		for (Boolean received : rec.values()) {
			if (received == Boolean.FALSE) {
				i++;
			}
			if (i > 1) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * @return Silent neighbor if has one else throws exception
	 */
	public WaveServer getSilent() {
		if (hasSilent()) {
			for (WaveServer server : rec.keySet()) {
				if (rec.get(server) == Boolean.FALSE) {
					return server;
				}
			}
		}
		throw new NullPointerException();
	}

	/**
	 * Checks if server is a neighbor
	 * 
	 * @param server
	 *            the server that is being checked
	 * @return true if neighbor
	 */
	public boolean isNeighbor(WaveServer server) {
		return this.rec.containsKey(server);
	}

	/**
	 * @param server
	 *            new neighbor
	 * @return true for success false if neighbor is already added.
	 */
	public boolean addNeigh(WaveServer server) {
		if (!this.rec.containsKey(server)) {
			this.rec.put(server, Boolean.FALSE);
			this.neighNo++;
			return true;
		}
		return false;
	}
	public void resetNeigh() {
		this.neighNo = 0;
		this.rec = new HashMap<WaveServer, Boolean>();
	}

	/**
	 * @param server
	 *            Server to which message is going to be sent
	 * @param msg
	 *            Message that is sent to neighbor server
	 * @return
	 */
	public void sendMessage() {
		WaveServer receiver = this.getSilent();
		if (this.isNeighbor(receiver)) {
			receiver.incomingMsg.add(this);
			this.sentToSilent();
			System.out.println(sendMsgString(this, receiver));
		}
	}
	
	public void receiveMessage() {
		if (hasMessages()) {
			WaveServer sender = getIncomingMsg().poll();
			getRec().put(sender, Boolean.TRUE);
			System.out.println(recMsgString(this, sender));
		}
	}

	/**
	 * Check if were are messages to be received
	 * 
	 * @return true if <code>messages</code> is not empty
	 */
	public boolean hasMessages() {
		return !this.incomingMsg.isEmpty();
	}

	public String toString() {
		return "" + this.getId();
	}
	

	/**
	 * Generate a message for server that received a message from send
	 * 
	 * @param receiver
	 *            Server that received
	 * @param sender
	 *            Server that sent
	 * @return
	 */
	public String recMsgString(WaveServer receiver, WaveServer sender) {
		return "Server [" + receiver.getId()
				+ "] RECEIVED a message from Server[" + sender.getId() + "]";
	}

	/**
	 * Generate a message that indicates that sender sent a message to receiver
	 * 
	 * @param sender
	 *            Server that sent a message
	 * @param receiver
	 *            Server that received a message
	 * @return
	 */
	public String sendMsgString(WaveServer sender, WaveServer receiver) {
		return "Server [" + sender.getId() + "] SENT a message to Server["
				+ receiver.getId() + "]";
	}
	/**
	 * Generate message for server that did no action
	 * 
	 * @param server
	 *            Server that did no action on his turn
	 * @return
	 */
	public String noActionMsgString() {
		return "Server [" + this.getId() + "] NO ACTION";
	}
	
	/**
	 * Generate a message for server that decided
	 * 
	 * @param server
	 * @return
	 */
	public String decideMsgString() {
		return "Server [" + this.getId() + "] DECIDE";
	}
}
