package uk.ac.ncl.coursework.datastucture;

import java.util.LinkedList;

public class TreeNode<E> extends AbstractNode<E> {

	/**
	 * <code>parent</code> indicates the parent of the tree node. If the node
	 * parent value is null the node is a root. <code>children</code> is an
	 * ArrayList containing TreeNode references that indicate the children of
	 * this node. <code>allowsChildren</code> boolean variable to indicate
	 * whether or not adding a new children is allowed.
	 */
	private LinkedList<TreeNode<E>> children;

	/**
	 * Default constructor to create empty node with no references.
	 */
	public TreeNode() {
		super();
		this.children = new LinkedList<TreeNode<E>>();
	}

	/**
	 * This constructor should be used to initialise the root of the tree.
	 * 
	 * @param item
	 *            root object
	 */
	public TreeNode(E item) {
		super(item);
		this.children = new LinkedList<TreeNode<E>>();
	}

	/**
	 * This constructor should be used to initialise the branch or the leaf.
	 * 
	 * @param parent
	 *            indicates nodes parent
	 * @param item
	 *            indicates nodes object reference
	 */
	public TreeNode(TreeNode<E> parent, E item) {
		super(parent, item);
		this.children = new LinkedList<TreeNode<E>>();
	}

	/**
	 * @param child
	 *            new item to be added to tree as children of <code>item</code>
	 * @return true if children contains child that was tried to be added.
	 */

	public boolean addChild(TreeNode<E> child) {
		this.children.add(child);
		return this.children.contains(child) ? true : false;
	}

	/**
	 * @param children
	 *            new items to be added to tree as children of <code>item</code>
	 * @return returns true if all children were added; false if at <b>least<b>
	 *         one wasn't.
	 */
	public boolean addChildren(LinkedList<TreeNode<E>> children) {
		boolean result = true;
		for (TreeNode<E> child : children) {
			result = result && children.add(child);
		}
		return result;
	}

	/**
	 * @return nodes children
	 */
	public LinkedList<TreeNode<E>> getChildren() {
		return children;
	}

	/**
	 * @param sets
	 *            node's children
	 */
	public void setChildren(LinkedList<TreeNode<E>> children) {
		this.children = children;
	}

	/**
	 * Gets the index of the child in children using
	 * 
	 * @see java.util.LinkedList.get(int index);
	 * @param childIndex
	 *            index of the child inside children variable
	 * @return
	 */
	public TreeNode<E> getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	/**
	 * @see java.util.LinkedList.size()
	 * @return number of the children
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * @return true if node is a leaf(has no children)
	 */
	public boolean isLeaf() {
		return this.children.isEmpty();
	}

}
