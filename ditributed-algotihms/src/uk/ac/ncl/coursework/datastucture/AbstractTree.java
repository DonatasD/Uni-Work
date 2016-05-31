package uk.ac.ncl.coursework.datastucture;

public abstract class AbstractTree<E> {
	/**
	 * root indicates the root of the tree n number of nodes in the tree(leaves,
	 * branches and root)
	 */
	private AbstractNode<E> root;
	private int n;

	public AbstractTree() {
		this.root = null;
		this.n = 0;
	}

	public AbstractTree(int n) {
		this.root = null;
		this.n = n;
	}
	/**
	 * @return the root
	 */
	public AbstractNode<E> getRoot() {
		return this.root;
	}
	/**
	 * @return tree node number
	 */
	public int getN() {
		return n;
	}

	public void setRoot(AbstractNode<E> root) {
		this.root = root;
	}

	public void setN(int n) {
		this.n = n;
	}

	public abstract void insert(AbstractNode<E> node, AbstractNode<E> root);
}
