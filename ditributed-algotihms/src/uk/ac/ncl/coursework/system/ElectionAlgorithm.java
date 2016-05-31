package uk.ac.ncl.coursework.system;

import java.util.HashSet;
import java.util.Random;

import uk.ac.ncl.coursework.datastucture.BinaryNode;
import uk.ac.ncl.coursework.datastucture.BinaryTree;
import uk.ac.ncl.coursework.datastucture.Tree;


public class ElectionAlgorithm extends WaveAlgorithm{

	private final static int ITERATION = 100;

	public ElectionAlgorithm() {
		super();
	}

	public void emulateArbitrary(Tree tree, boolean fullLog) {
		//Set up initators
		Random rand = new Random();
		int noOfInitiators = rand.nextInt(tree.getN() - 1) + 1;
		HashSet<Integer> initiators = new HashSet<Integer>();
		while (initiators.size() != noOfInitiators ) {
			int id = rand.nextInt(tree.getN());

			if (initiators.add(id)) {
				tree.getServer(id).makeInitiator();
			}
		}

		System.out.println("[INITIATORS] " + initiators + "\n");
		boolean noAction = true;
		boolean globalNoAction = true;
		for (int i = 0; i < tree.getN() * ITERATION; i++) {
			int noOfNodes = rand.nextInt(tree.getN() - 1) + 1;
			HashSet<Integer> activeNodes = new HashSet<Integer>();
			while (activeNodes.size() != noOfNodes) {
				int id = rand.nextInt(tree.getN());
				activeNodes.add(new Integer(id));
			}
			//Print chosen nodes
			if (fullLog) {
				System.out.println("CHOSEN SERVERS: " + activeNodes);
			}
			for (Integer id : activeNodes) {
				ElectionServer chosen = tree.getServer(id);

				//WAKE UP START
				if (chosen.isInitiator() && !chosen.isWakeUpSent()) {
					chosen.sendWakeUps();
					noAction = false;
					globalNoAction = false;
				} 
				while (chosen.getWakeUpRec() < chosen.getNeighNo() && !chosen.getIncomingWakeUp().isEmpty()) {
					chosen.receiveWakeUps();
					noAction = false;
					globalNoAction = false;
					if (!chosen.isWakeUpSent()) {
						chosen.sendWakeUps();
					}
				}
				//WAKE UP END
				//TREE ALGORITHM START
				if (chosen.getWakeUpRec() == chosen.getNeighNo()) {
					while (!chosen.hasSilent() && !chosen.hasReceivedAll()
							&& chosen.hasMessages()) {
						chosen.receiveMessage();
						noAction = false;
						globalNoAction = false;
					}

					if (chosen.hasSilent()) {
						// Check if message was not already sent to silent
						if (!chosen.isSentToSilent()) {
							// Send to silent neighbor
							chosen.sendMessage();
							noAction = false;
							globalNoAction = false;
						}
						// Check if any messages can be received
						if (chosen.hasMessages()) {
							// Receive message from silent neighbour and decide
							ElectionServer last = chosen.receiveMessageFromLast();
							chosen.sendMessageToEveryoneExpectOne(last);
							noAction = false;
							globalNoAction = false;
						}
					}
				}
				//TREE ALGORITHM FINISH
				if (fullLog) {
					if (noAction) { 
						if (chosen.getWakeUpRec() == 0 && !chosen.isInitiator()) {
							System.out.println(chosen.sleepString());
						} else if (!chosen.hasSilent()) {
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

	public void emulateBinary(BinaryTree tree, boolean fullLog) {
		//Set up initators
		Random rand = new Random();
		int noOfInitiators = rand.nextInt(tree.getN() - 1) + 1;
		HashSet<Integer> initiators = new HashSet<Integer>();
		while (initiators.size() != noOfInitiators ) {
			int id = rand.nextInt(tree.getN());

			if (initiators.add(id)) {
				tree.findById(id, (BinaryNode<ElectionServer>) tree.getRoot()).getItem().makeInitiator();
			}
		}

		System.out.println("[INITIATORS] " + initiators + "\n");
		boolean noAction = true;
		boolean globalNoAction = true;
		for (int i = 0; i < tree.getN() * ITERATION; i++) {
			int noOfNodes = rand.nextInt(tree.getN() - 1) + 1;
			HashSet<Integer> activeNodes = new HashSet<Integer>();
			while (activeNodes.size() != noOfNodes) {
				int id = rand.nextInt(tree.getN());
				activeNodes.add(new Integer(id));
			}
			//Print chosen nodes
			if (fullLog) {
				System.out.println("CHOSEN SERVERS: " + activeNodes);
			}
			for (Integer id : activeNodes) {
				ElectionServer chosen = tree.findById(id, (BinaryNode<ElectionServer>) tree.getRoot()).getItem();

				//WAKE UP START
				if (chosen.isInitiator() && !chosen.isWakeUpSent()) {
					chosen.sendWakeUps();
					noAction = false;
					globalNoAction = false;
				} 
				while (chosen.getWakeUpRec() < chosen.getNeighNo() && !chosen.getIncomingWakeUp().isEmpty()) {
					chosen.receiveWakeUps();
					noAction = false;
					globalNoAction = false;
					if (!chosen.isWakeUpSent()) {
						chosen.sendWakeUps();
					}
				}
				//WAKE UP END
				//TREE ALGORITHM START
				if (chosen.getWakeUpRec() == chosen.getNeighNo()) {
					while (!chosen.hasSilent() && !chosen.hasReceivedAll()
							&& chosen.hasMessages()) {
						chosen.receiveMessage();
						noAction = false;
						globalNoAction = false;
					}

					if (chosen.hasSilent()) {
						// Check if message was not already sent to silent
						if (!chosen.isSentToSilent()) {
							// Send to silent neighbor
							chosen.sendMessage();
							noAction = false;
							globalNoAction = false;
						}
						// Check if any messages can be received
						if (chosen.hasMessages()) {
							// Receive message from silent neighbour and decide
							ElectionServer last = chosen.receiveMessageFromLast();
							chosen.sendMessageToEveryoneExpectOne(last);
							noAction = false;
							globalNoAction = false;
						}
					}
				}
				//TREE ALGORITHM FINISH
				if (fullLog) {
					if (noAction) { 
						if (chosen.getWakeUpRec() == 0 && !chosen.isInitiator()) {
							System.out.println(chosen.sleepString());
						} else if (!chosen.hasSilent()) {
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

	public static void main(String[] args) {
		ElectionAlgorithm a = new ElectionAlgorithm();
		int n = 7;
		
		Tree arbitraryTree = Tree.buildTree(n);
		BinaryTree balancedTree = BinaryTree.balancedTree(n);
		BinaryTree unbalancedTree = BinaryTree.unbalancedTree(n);

		System.out.println(arbitraryTree);
		a.emulateArbitrary(arbitraryTree, true);

		System.out.println(unbalancedTree);
		a.emulateBinary(unbalancedTree, true);

		System.out.println(balancedTree);
		a.emulateBinary(balancedTree, true);
	}
}
