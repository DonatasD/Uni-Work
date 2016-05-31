package uk.ac.ncl.coursework.datastucture;

public abstract class AbstractNode<E> {
	private AbstractNode<E> parent;
	private E item;

	public AbstractNode() {
		this.parent = null;
		this.item = null;
	}

	public AbstractNode(E item) {
		this.item = item;
		this.parent = null;
	}
	public AbstractNode(AbstractNode<E> parent, E item) {
		this.parent = parent;
		this.item = item;
	}
	/**
	 * Checks if node has parent. Should return false for root only.
	 * 
	 * @return
	 */
	public boolean hasParent() {
		return this.parent == null ? false : true;
	}
	/**
	 * @return parent of this node
	 */
	public AbstractNode<E> getParent() {
		return parent;
	}
	/**
	 * @param parent
	 *            of this node
	 */
	public void setParent(AbstractNode<E> parent) {
		this.parent = parent;
	}
	/**
	 * @return Object reference for this node
	 */
	public E getItem() {
		return item;
	}
	/**
	 * set Object reference for this node
	 * 
	 * @param item
	 */
	public void setItem(E item) {
		this.item = item;
	}
	public String toString() {
		return item.toString();
	}

}
