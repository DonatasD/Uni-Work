package uk.ac.ncl.coursework.datastucture;

public class BinaryNode<E> extends AbstractNode<E> {

	private BinaryNode<E> l;
	private BinaryNode<E> r;

	/**
	 * Default constructor to create empty node with no references.
	 */

	public BinaryNode() {
		super();
		this.l = null;
		this.r = null;
	}

	/**
	 * This constructor should be used to initialise the root of the tree.
	 * 
	 * @param item
	 *            root object
	 */
	public BinaryNode(E item) {
		super(item);
		this.l = null;
		this.r = null;
	}

	public BinaryNode<E> getL() {
		return l;
	}

	public BinaryNode<E> getR() {
		return r;
	}

	public void setL(BinaryNode<E> l) {
		this.l = l;
	}

	public void setR(BinaryNode<E> r) {
		this.r = r;
	}

}
