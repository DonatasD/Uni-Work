package uk.ac.ncl.coursework.system;

import java.util.HashSet;
import java.util.Random;

import uk.ac.ncl.coursework.datastucture.BinaryNode;
import uk.ac.ncl.coursework.datastucture.BinaryTree;
import uk.ac.ncl.coursework.datastucture.Tree;

/**
 * This class is meant to create a distributed system, which would emulate the
 * tree algorithm, which goes into the group of wave algorithms. The pseudo code
 * for this algorithm can be found
 * 
 * @see Gerard Tel (2000). Introduction to Distributed Algorithms (2nd edition).
 *      Cambridge: Cambridge University Press. p188.
 * @author Donatas Daubaras
 * @since 1.7
 */
public class WaveAlgorithm {
	private final static int ITERATION = 100;

	public WaveAlgorithm() {

	}
	
	public void emulateBinary(BinaryTree tree, boolean fullLog) {
		Random rand = new Random();
		boolean noAction = true;
		boolean globalNoAction = true;
		for (int i = 0; i < tree.getN() * ITERATION; i++) {
			int noOfNodes = rand.nextInt(tree.getN() - 1) + 1;
			HashSet<Integer> activeNodes = new HashSet<Integer>();
			while (activeNodes.size() != noOfNodes) {
				int id = rand.nextInt(tree.getN());
				activeNodes.add(new Integer(id));
			}
			if (fullLog) {
				System.out.println("CHOSEN SERVERS: " + activeNodes);
			}
			for (Integer id : activeNodes) {
				// Make a pointer to this server object
				WaveServer chosen = tree.findById(id, (BinaryNode<ElectionServer>) tree.getRoot()).getItem();
				// Receive messages till silent member is not found and all
				// messages
				// are not received ,and no messages are to receive
				while (!chosen.hasSilent() && !chosen.hasReceivedAll()
						&& chosen.hasMessages()) {
					chosen.receiveMessage();
				}
				// Check if silent is found
				if (chosen.hasSilent()) {
					// Check if message was not already sent to silent
					if (!chosen.isSentToSilent()) {
						chosen.sendMessage();
						noAction = false;
						globalNoAction = false;
					}
					// Check if any messages can be received
					if (chosen.hasMessages()) {
						// Receive message from silent neighbour and decide
						WaveServer sender = chosen.getIncomingMsg().poll();
						chosen.getRec().put(sender, Boolean.TRUE);
						System.out.println(recMsgString(chosen, sender));
						System.out.println(decideMsgString(chosen));
						noAction = false;
						globalNoAction = false;
					}
				}
				if (fullLog) {
					if (noAction) { 
						if (!chosen.hasSilent()) {
							System.out.println(waitingString(chosen));
						}
						else {
							System.out.println(chosen.noActionMsgString());
						}
					}
				}
				noAction = true;
			}
			if (fullLog) {
				if (globalNoAction) {
					System.out.println("-----------------");
				}
			}
			if (!globalNoAction) {
				if (!fullLog) {
					System.out.println("CHOSEN SERVERS: " + activeNodes);
				}
				System.out.println("-----------------");
				noAction = true;
			}
			globalNoAction = true;

		}
	}
	
	/**
	 * Run the emulation for arbitrary tree
	 */
	public void emulateArbitrary(Tree tree, boolean fullLog) {
		Random rand = new Random();
		boolean noAction = true;
		boolean globalNoAction = true;
		for (int i = 0; i < tree.getN() * ITERATION; i++) {
			int noOfNodes = rand.nextInt(tree.getN() - 1) + 1;
			HashSet<Integer> activeNodes = new HashSet<Integer>();
			while (activeNodes.size() != noOfNodes) {
				int id = rand.nextInt(tree.getN());
				activeNodes.add(new Integer(id));
			}
			if (fullLog) {
				System.out.println("CHOSEN SERVERS: " + activeNodes);
			}
			for (Integer id : activeNodes) {
				// Make a pointer to this server object
				WaveServer chosen = tree.getServer(id);
				// Receive messages till silent member is not found and all
				// messages
				// are not received ,and no messages are to receive
				while (!chosen.hasSilent() && !chosen.hasReceivedAll()
						&& chosen.hasMessages()) {
					chosen.receiveMessage();
				}
				// Check if silent is found
				if (chosen.hasSilent()) {
					// Check if message was not already sent to silent
					if (!chosen.isSentToSilent()) {
						chosen.sendMessage();
						noAction = false;
						globalNoAction = false;
					}
					// Check if any messages can be received
					if (chosen.hasMessages()) {
						// Receive message from silent neighbour and decide
						WaveServer sender = chosen.getIncomingMsg().poll();
						chosen.getRec().put(sender, Boolean.TRUE);
						System.out.println(recMsgString(chosen, sender));
						System.out.println(chosen.decideMsgString());
						noAction = false;
						globalNoAction = false;
					}
				}
				if (fullLog) {
					if (noAction) { 
						if (!chosen.hasSilent()) {
							System.out.println(waitingString(chosen));
						}
						else {
							System.out.println(chosen.noActionMsgString());
						}
					}
				}
				noAction = true;
			}
			if (fullLog) {
				if (globalNoAction) {
					System.out.println("-----------------");
				}
			}
			if (!globalNoAction) {
				if (!fullLog) {
					System.out.println("CHOSEN SERVERS: " + activeNodes);
				}
				System.out.println("-----------------");
				noAction = true;
			}
			globalNoAction = true;

		}
	}

	/**
	 * Generate a message for server that decided
	 * 
	 * @param server
	 * @return
	 */
	public String decideMsgString(WaveServer server) {
		return "Server[" + server.getId() + "] DECIDE";
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

	public String waitingString(WaveServer server) {
		return "Server [" + server.getId() + "]" + " is WAITING to find SILENT NEIGHBOUR";
	}
}
