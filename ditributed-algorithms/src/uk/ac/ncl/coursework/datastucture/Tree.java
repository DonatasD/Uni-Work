package uk.ac.ncl.coursework.datastucture;

import java.util.LinkedList;
import java.util.Random;

import uk.ac.ncl.coursework.system.ElectionServer;

public class Tree extends AbstractTree<ElectionServer> {

	private Tree() {
		super();
	}

	private Tree(int n) {
		super(n);
	}

	/**
	 * 
	 * @return maximum number of nodes in level
	 */
	public int getMaxNodesInLevel() {
		int max = 0;
		for (Integer n : getLevelsPopulation()) {
			if (max < n) {
				max = n;
			}
		}
		return max;
	}
	/**
	 * 
	 * @return LinkedList of the number of TreeNodes in each level
	 */
	public LinkedList<Integer> getLevelsPopulation() {
		LinkedList<Integer> population = new LinkedList<Integer>();
		LinkedList<TreeNode<ElectionServer>> top = new LinkedList<TreeNode<ElectionServer>>();
		top.add((TreeNode<ElectionServer>) this.getRoot());
		while (!top.isEmpty()) {
			population.add(new Integer(top.size()));
			LinkedList<TreeNode<ElectionServer>> temp = new LinkedList<TreeNode<ElectionServer>>();
			for (TreeNode<ElectionServer> s : top) {
				temp.addAll(s.getChildren());
			}
			top = temp;
		}
		return population;
	}
	/**
	 * Depth 0 means root has no children
	 * 
	 * @return Tree depth
	 */
	public int getDepth() {
		LinkedList<TreeNode<ElectionServer>> top = new LinkedList<TreeNode<ElectionServer>>();
		top.add((TreeNode<ElectionServer>) this.getRoot());
		int depth = 0;
		while (!top.isEmpty()) {
			depth++;
			LinkedList<TreeNode<ElectionServer>> temp = new LinkedList<TreeNode<ElectionServer>>();
			for (TreeNode<ElectionServer> s : top) {
				temp.addAll(s.getChildren());
			}
			top = temp;
		}
		return depth;
	}
	/**
	 * level 0 - root
	 * 
	 * @param level
	 *            indicates which level nodes should be returned
	 * @return
	 */
	public LinkedList<TreeNode<ElectionServer>> getNodesByLevel(int level) {
		LinkedList<TreeNode<ElectionServer>> result = new LinkedList<TreeNode<ElectionServer>>();
		result.add((TreeNode<ElectionServer>) this.getRoot());
		if (level == 0) {
			return result;
		} else if (level > getDepth()) {
			throw new IndexOutOfBoundsException();
		}
		boolean finish = false;
		int i = 1;
		while (!finish) {
			LinkedList<TreeNode<ElectionServer>> temp = new LinkedList<TreeNode<ElectionServer>>();
			for (int j = 0; j < result.size(); j++) {
				temp.addAll(result.get(j).getChildren());
			}
			result = temp;
			if (i == level) {
				finish = true;
			}
			i++;
		}
		return result;
	}
	/**
	 * Checks that the index is in the range
	 * 
	 * @param index
	 *            server id
	 */
	private void rangeCheck(int index) {
		if (index >= this.getN() || this.getN() < 0) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}
	}

	/**
	 * Construct outOfBoundsMsg
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + this.getN();
	}

	/**
	 * @param index
	 *            Server id
	 * @return Server with defined server id
	 */
	public ElectionServer getServer(int index) {
		rangeCheck(index);
		boolean found = false;
		LinkedList<TreeNode<ElectionServer>> current = new LinkedList<TreeNode<ElectionServer>>();
		LinkedList<TreeNode<ElectionServer>> temp = new LinkedList<TreeNode<ElectionServer>>();
		current.add((TreeNode<ElectionServer>) this.getRoot());
		while (!found && !current.isEmpty()) {
			for (int i = 0; i < current.size(); i++) {
				if (current.get(i).getItem().getId() == index) {
					return current.get(i).getItem();
				}
				temp.addAll(current.get(i).getChildren());
			}
			current = new LinkedList<TreeNode<ElectionServer>>(temp);
			temp = new LinkedList<TreeNode<ElectionServer>>();
		}
		throw new NullPointerException();
	}

	/**
	 * Overloads @see Tree.buildTree(int n, int maxNodes)
	 * 
	 * @param n
	 *            number of nodes in tree
	 */
	public static Tree buildTree(int n) {
		return buildTree(n, n);
	}

	/**
	 * Overloads @see Tree.buildTree(int n, int maxNodes, int minNodes)
	 * 
	 * @param n
	 *            number of nodes in tree
	 * @param maxNodes
	 *            maximum number of children for each node
	 */
	public static Tree buildTree(int n, int maxNodes) {
		return buildTree(n, maxNodes, 1);
	}

	/**
	 * @param n
	 *            number of nodes in tree
	 * @param maxNodes
	 *            maximum number of children for each node
	 * @param minNodes
	 *            minimum number of children for each node if possible(leaves
	 *            may have less) should be less then maximum number if larger
	 *            then max then is set to maximum number value. Should be larger
	 *            then 0 if no initialised as 1.
	 */
	public static Tree buildTree(int n, int maxNodes, int minNodes) {
		Tree tree = new Tree(n);
		if (maxNodes > n) {
			maxNodes = n;
		} else if (maxNodes <= 0) {
			maxNodes = 1;
		}
		if (minNodes > maxNodes) {
			minNodes = maxNodes;
		} else if (minNodes <= 0) {
			minNodes = 1;
		}
		// Indicates how many servers are currently added
		int idCount = 0;
		/**
		 * @see java.util.Random
		 */
		final Random rand = new Random();
		// Data for nodes are stored in the array list for easier adding
		LinkedList<LinkedList<TreeNode<ElectionServer>>> data = new LinkedList<LinkedList<TreeNode<ElectionServer>>>();
		// Indicates the number of levels created in tree(tree depth)
		int iteration = 0;
		// Indicates current's level node id, which will get new children
		// int parentItem = 0;
		// Create nodes until we reach desired numberidCount
		while (idCount < n) {
			// Current tree level that is being created
			LinkedList<TreeNode<ElectionServer>> currentList = new LinkedList<TreeNode<ElectionServer>>();
			// if no nodes are created root should be initialised
			if (idCount == 0) {
				// initiate root
				tree.setRoot(new TreeNode<ElectionServer>(new ElectionServer(idCount++)));
				// add root to current level
				currentList.add((TreeNode<ElectionServer>) tree.getRoot());
				// add current level to temporal tree structure(should be first
				// element)
				data.add(currentList);
			} else {
				// Indicates parent list index in <code>data</code>
				int parentList = iteration - 1;

				for (int j = 0; j < data.get(parentList).size() && idCount < n; j++) {
					int randNumber;
					/**
					 * decide how many nodes will be created for current parent
					 * 
					 * @see java.util.Random.nextInt(int floor)
					 */
					/*
					 * Using Advanced if(complicated) randNumber = (n - idCount)
					 * < maxNodes ? n - idCount < minNodes ? rand.nextInt(n -
					 * idCount) + 1 : rand.nextInt(n - idCount - minNodes + 1) +
					 * minNodes : rand.nextInt(maxNodes - minNodes + 1) +
					 * minNodes;
					 */
					/**
					 * If minNodes and maximum nodes number is greater than
					 * currently available servers number then get random
					 * integer in a range [1, n-idCount]
					 */
					if ((n - idCount) < maxNodes && (n - idCount) < minNodes) {
						randNumber = rand.nextInt(n - idCount) + 1;
					}
					/**
					 * If maximum nodes number is greater than currently
					 * available servers number and minimum nodes number is less
					 * or equal to available server number then generate random
					 * in a range [minNodes, n-idCount]
					 */
					else if ((n - idCount) < maxNodes
							&& (n - idCount) >= minNodes) {
						randNumber = rand.nextInt(n - idCount - minNodes + 1)
								+ minNodes;
					}
					/**
					 * Generate random number in a range [minNodes,maxNodes]
					 */
					else {
						randNumber = rand.nextInt(maxNodes - minNodes + 1)
								+ minNodes;
					}
					for (int i = 0; i < randNumber; i++) {
						rand.nextInt(data.get(parentList).size());
						// Parent node reference
						TreeNode<ElectionServer> parent = data.get(parentList).get(j);
						// Create child's item
						ElectionServer server = new ElectionServer(idCount++);
						// Create new child
						TreeNode<ElectionServer> child = new TreeNode<ElectionServer>(parent,
								server);
						// add child to current level
						currentList.add(child);
						// Assign parent with a new child
						parent.addChild(child);
						// Assign neighbors for items
						server.addNeigh(parent.getItem());
						parent.getItem().addNeigh(server);
					}
				}
				data.add(currentList);
			}
			iteration++;
		}
		return tree;
	}

	public static Tree binaryTree(int n) {
		return buildTree(n, 2, 2);
	}

	/**
	 * Returns string representation of the desired tree.
	 */
	public String toString() {
		return "Tree Size " + this.getN() + "\n" + this.printTree();
	}

	/**
	 * @param current
	 *            indicates current tree level(list of children)
	 * @param count
	 *            indicates the index of current level(depth)
	 * @return String representation of the current level and all levels below
	 *         using recursion
	 */
	private String printTree(LinkedList<TreeNode<ElectionServer>> current, int count) {
		String result = "";
		String tab = new String(new char[count]).replace("\0", "*");
		for (int i = 0; i < current.size(); i++) {
			result += current.get(i).getChildren().isEmpty() ? tab
					+ current.get(i).getItem().toString() + "\n" : tab
					+ current.get(i).getItem().toString() + "\n"
					+ printTree(current.get(i).getChildren(), count + 1);

		}
		return result;

	}

	/**
	 * @return tree representation in string
	 */
	private String printTree() {
		String result = "";
		result += this.getRoot().getItem().toString() + "\n";
		return result += printTree(
				((TreeNode<ElectionServer>) this.getRoot()).getChildren(), 1);
	}

	@Override
	public void insert(AbstractNode<ElectionServer> node, AbstractNode<ElectionServer> root) {
		// TODO Auto-generated method stub

	}
}
