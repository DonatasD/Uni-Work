package uk.ac.ncl.coursework.main;

import uk.ac.ncl.coursework.datastucture.BinaryTree;
import uk.ac.ncl.coursework.datastucture.Tree;
import uk.ac.ncl.coursework.system.WaveAlgorithm;

public class MainWave {

	public static void main(String[] args) {
		WaveAlgorithm a = new WaveAlgorithm();
		/*
		 * You can run this from eclipse by going to Run -> run configuration ->
		 * Arguments -> and pass n in there. Make sure to select the class
		 * MainWave or MainElection class.
		 */
		// Number of nodes in tree
		int n = 0;
		boolean log = false;
		try {
			n = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException();
		}
		/*
		 * For full log pass true as second parameter
		 */
		if (args.length > 1 && args[1].equals("true")) {
			log = true;
		}

		// Create Trees
		Tree arbitraryTree = Tree.buildTree(n);
		BinaryTree balancedTree = BinaryTree.balancedTree(n);
		BinaryTree unbalancedTree = BinaryTree.unbalancedTree(n);

		// Prints the tree
		System.out.println(arbitraryTree);
		// Second parameter defines if you want to see full(true) or
		// minimal(false) output
		a.emulateArbitrary(arbitraryTree, log);

		System.out.println(unbalancedTree);
		a.emulateBinary(unbalancedTree, log);

		System.out.println(balancedTree);
		a.emulateBinary(balancedTree, log);
	}

}
