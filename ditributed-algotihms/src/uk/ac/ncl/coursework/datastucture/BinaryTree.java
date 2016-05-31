package uk.ac.ncl.coursework.datastucture;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.ncl.coursework.system.ElectionServer;

public class BinaryTree extends AbstractTree<ElectionServer> {

	private BinaryTree() {
		super();
	}
	private BinaryTree(int n) {
		super(n);
	}

	public static BinaryTree unbalancedTree(int n) {
		BinaryTree tree = new BinaryTree(n);
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			temp.add(i);
		}
		Collections.shuffle(temp);
		tree.setRoot((AbstractNode<ElectionServer>) (new BinaryNode<ElectionServer>(
				new ElectionServer(temp.remove(0)))));
		for (Integer id : temp) {
			tree.insert((AbstractNode<ElectionServer>) (new BinaryNode<ElectionServer>(
					new ElectionServer(id))), tree.getRoot());
			
		}
		return tree;
	}
	public static BinaryTree balancedTree(int n) {
		BinaryTree tree = unbalancedTree(n);
		tree.balance();
		return tree;
	}

	public void insert(AbstractNode<ElectionServer> node,
			AbstractNode<ElectionServer> root) {

		// If root is empty
		if (root == null) {
			root = node;
			return;
		}

		if (node.getItem().getId() <= root.getItem().getId()) {
			// if current root left child is null
			if (((BinaryNode<ElectionServer>) root).getL() == null) {
				((BinaryNode<ElectionServer>) root)
						.setL((BinaryNode<ElectionServer>) node);
				node.setParent(root);
				node.getItem().addNeigh(root.getItem());
				root.getItem().addNeigh(node.getItem());
			} else {
				insert(node, ((BinaryNode<ElectionServer>) root).getL());
			}
		} else {
			// if current root right child is null
			if (((BinaryNode<ElectionServer>) root).getR() == null) {
				((BinaryNode<ElectionServer>) root)
						.setR((BinaryNode<ElectionServer>) node);
				node.setParent(root);
				node.getItem().addNeigh(root.getItem());
				root.getItem().addNeigh(node.getItem());
			} else {
				insert(node, ((BinaryNode<ElectionServer>) root).getR());
			}
		}
	}
	public ArrayList<BinaryNode<ElectionServer>> makeList(
			BinaryNode<ElectionServer> root) {
		if (root == null) {
			return new ArrayList<BinaryNode<ElectionServer>>();
		}
		ArrayList<BinaryNode<ElectionServer>> result = new ArrayList<BinaryNode<ElectionServer>>();
		result.addAll(this.makeList(root.getL()));
		result.add(root);
		result.addAll(this.makeList(root.getR()));
		return result;
	}

	public void balance(ArrayList<BinaryNode<ElectionServer>> list) {
		if (list.isEmpty()) {
			return;
		}

		ArrayList<BinaryNode<ElectionServer>> left = new ArrayList<BinaryNode<ElectionServer>>();
		ArrayList<BinaryNode<ElectionServer>> right = new ArrayList<BinaryNode<ElectionServer>>();
		BinaryNode<ElectionServer> mid;
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() / 2) {
				left.add(list.get(i));
			} else if (i == list.size() / 2) {
				mid = list.get(i);
				this.insert(mid, this.getRoot());
			} else {
				right.add(list.get(i));
			}
		}
		this.balance(left);
		if (!right.isEmpty()) {
			this.balance(right);
		}
	}
	public void balance() {
		ArrayList<BinaryNode<ElectionServer>> list = makeList((BinaryNode<ElectionServer>) this
				.getRoot());

		ArrayList<BinaryNode<ElectionServer>> left = new ArrayList<BinaryNode<ElectionServer>>();
		ArrayList<BinaryNode<ElectionServer>> right = new ArrayList<BinaryNode<ElectionServer>>();
		BinaryNode<ElectionServer> mid;
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setL(null);
			list.get(i).setR(null);
			list.get(i).setParent(null);
			list.get(i).getItem().resetNeigh();
			if (i < list.size() / 2) {
				left.add(list.get(i));
			} else if (i == list.size() / 2 && i != 0) {
				mid = list.get(i);
				this.setRoot(mid);
			} else {
				right.add(list.get(i));
			}
		}
		this.balance(left);
		this.balance(right);
	}

	public BinaryNode<ElectionServer> findById(int id,
			BinaryNode<ElectionServer> current) {
		if (current == null) {
			throw new NullPointerException("Not found");
		}
		if (current.getItem().getId() == id) {
			return current;
		} else if (current.getItem().getId() > id) {
			return findById(id, current.getL());
		} else {
			return findById(id, current.getR());
		}
	}

	public String toString() {
		return "Tree Size " + this.getN() + "\n" + printTree((BinaryNode<ElectionServer>) this.getRoot(), 0);
	}
	private String printTree(BinaryNode<ElectionServer> current, int count) {
		if (current == null) {
			return "";
		}
		String tab = new String(new char[count]).replace("\0", "*");
		return tab + current + "\n" + printTree(current.getL(), count + 1)
				+ printTree(current.getR(), count + 1);
	}

	public static void main(String[] args) {

		BinaryTree tree = new BinaryTree();
		BinaryNode<ElectionServer> n1 = new BinaryNode<ElectionServer>(
				new ElectionServer(6));
		BinaryNode<ElectionServer> n2 = new BinaryNode<ElectionServer>(
				new ElectionServer(2));
		BinaryNode<ElectionServer> n3 = new BinaryNode<ElectionServer>(
				new ElectionServer(5));
		BinaryNode<ElectionServer> n4 = new BinaryNode<ElectionServer>(
				new ElectionServer(7));
		BinaryNode<ElectionServer> n5 = new BinaryNode<ElectionServer>(
				new ElectionServer(1));
		BinaryNode<ElectionServer> n6 = new BinaryNode<ElectionServer>(
				new ElectionServer(3));
		BinaryNode<ElectionServer> n7 = new BinaryNode<ElectionServer>(
				new ElectionServer(4));
		BinaryNode<ElectionServer> n8 = new BinaryNode<ElectionServer>(
				new ElectionServer(9));
		BinaryNode<ElectionServer> n9 = new BinaryNode<ElectionServer>(
				new ElectionServer(8));
		tree.setRoot(n1);
		tree.insert(n2, tree.getRoot());
		tree.insert(n3, tree.getRoot());
		tree.insert(n4, tree.getRoot());
		tree.insert(n5, tree.getRoot());
		tree.insert(n6, tree.getRoot());
		tree.insert(n7, tree.getRoot());
		tree.insert(n8, tree.getRoot());
		tree.insert(n9, tree.getRoot());
		System.out.println(tree);

		// BinaryTree tree = BinaryTree.balancedTree(20);

		/*
		 * System.out.println(tree.getRoot().getItem());
		 * System.out.println("---" + tree.getRoot().getL().getItem());
		 * System.out.println("------" + tree.root.getL().getL().getItem());
		 * System.out.println("------" +
		 * tree.getRoot().getL().getR().getItem());
		 * System.out.println("-----------" +
		 * tree.root.getL().getR().getL().getItem());
		 * System.out.println("---------------" +
		 * tree.root.getL().getR().getL().getR().getItem());
		 * System.out.println("---" + tree.root.getR().getItem());
		 * System.out.println("-------" + tree.root.getR().getR().getItem());
		 * System.out.println("------------" +
		 * tree.root.getR().getR().getL().getItem());
		 */
		// tree.balance();

		/*
		 * System.out.println(tree.root.getItem()); System.out.println("---" +
		 * tree.root.getL().getItem()); System.out.println("------" +
		 * tree.root.getL().getL().getItem()); System.out.println("---------" +
		 * tree.root.getL().getL().getL().getItem());
		 * System.out.println("------" + tree.root.getL().getR().getItem());
		 * System.out.println("---" + tree.root.getR().getItem());
		 * System.out.println("------" + tree.root.getR().getL().getItem());
		 * System.out.println("---------" +
		 * tree.root.getR().getL().getL().getItem());
		 * System.out.println("------" + tree.root.getR().getR().getItem());
		 */
		System.out.println(tree);

	}

}
