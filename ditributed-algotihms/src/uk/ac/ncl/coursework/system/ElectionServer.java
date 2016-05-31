package uk.ac.ncl.coursework.system;

import java.util.LinkedList;

public class ElectionServer extends WaveServer {

	private boolean wakeUpSent;
	private int wakeUpRec;
	private LinkedList<ElectionServer> incomingWakeUp;
	private State state;
	private int value;

	public ElectionServer(int id) {
		super(id);
		this.wakeUpRec = 0;
		this.wakeUpSent = false;
		this.state = State.SLEEP;
		this.incomingWakeUp = new LinkedList<ElectionServer>();
		this.value = Integer.MAX_VALUE;
	}

	public boolean isWakeUpSent() {
		return wakeUpSent;
	}

	public int getWakeUpRec() {
		return wakeUpRec;
	}

	public LinkedList<ElectionServer> getIncomingWakeUp() {
		return incomingWakeUp;
	}

	public State getState() {
		return state;
	}

	public int getValue() {
		return value;
	}

	public boolean isInitiator() {
		return this.value == this.getId();
	}
	public void makeInitiator() {
		this.value = this.getId();
	}
	public void sendWakeUps() {
		if (!wakeUpSent)
			for (WaveServer s : this.getRec().keySet()) {
				((ElectionServer) s).incomingWakeUp.add(this);
				System.out.println(sendWakeUpString(this, (ElectionServer) s));
			}
		this.wakeUpSent = true;
	}

	public void receiveWakeUps() {
		if (!incomingWakeUp.isEmpty()) {
			this.wakeUpRec++;
			System.out.println(recWakeUpString(incomingWakeUp.poll(), this));
		}
	}
	public void sendMessageToEveryoneExpectOne(ElectionServer last) {
		for (WaveServer s: this.getRec().keySet()) {
			if (last.getId() != s.getId()) {
				s.getIncomingMsg().add(this);
				System.out.println(sendMsgString(this, s) + " TO DECIDE");
			}
		}
	}
	private String sendWakeUpString(ElectionServer sender,
			ElectionServer receiver) {
		return "Server [" + sender.getId() + "] SENT a WAKEUP to Server["
				+ receiver.getId() + "]";
	}

	private String recWakeUpString(ElectionServer sender,
			ElectionServer receiver) {
		return "Server [" + receiver.getId()
				+ "] RECEIVED a WAKEUP from Server[" + sender.getId() + "]";
	}

	public void receiveMessage() {
		if (hasMessages()) {
			ElectionServer sender = (ElectionServer) getIncomingMsg().poll();
			getRec().put(sender, Boolean.TRUE);
			if (this.value > sender.getValue()) {
				this.value = sender.getValue();
			}
			System.out.println(recMsgString(this, sender));
		}
	}
	public ElectionServer receiveMessageFromLast() {
		if (hasMessages()) {
			ElectionServer sender = (ElectionServer) getIncomingMsg().poll();
			getRec().put(sender, Boolean.TRUE);
			if (this.value > sender.getValue()) {
				this.value = sender.getValue();
			}
			System.out.println(recMsgString(this, sender));
			decide();
			return sender;
		}
		throw new IllegalAccessError("No message can be received");
	}
	
	public String sleepString() {
		return "Server [" + this.getId() + "] is SLEEPING";
	}
	
	public void decide() {
		if (value == getId()) {
			this.state = State.LEADER;
		} else {
			this.state = State.LOST;
		}
		System.out.println(decideMsgString() + " " + this.state);
	}
	

}
