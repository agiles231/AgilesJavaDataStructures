package java.datastructures.tree.balanced;

import java.util.Comparator;

public class RedBlackTree<T> {
	
	// this is used so that T can be any data, even it does not implement compareable interface
	Comparator<T> comparator;
	Node root;
	public RedBlackTree(Comparator<T> comparator) {
		this.comparator = comparator;
		root = null;
	}
	
	public void insert(T item) {
		Node n = new Node(null, null, null, item, Node.RED, false); // always red by default. Assume not left
		insert(n);
	}
	
	private void insert(Node node) {
		if (node != null) { // if node is null, then do not do anything
			T item = node.item;
			if (root == null) {
				root = node;
				root.color = Node.BLACK;
			} else {
				Node cur = root;
				Node uncle = null;
				boolean insertOccurred = false;
				while (!insertOccurred) {
					Node grandParent = cur.parent;
					if (comparator.compare(item, cur.item) < 0) { // less than cur.item
						if (cur.left != null) { // go down another level, reset the uncle before reassigning current node
							uncle = cur.right;
							cur = cur.left;
						} else {
							cur.setLeft(node);
							if (cur.color == Node.RED) {
								rebalance(node, cur, grandParent, uncle);
							}
							insertOccurred = true;
						}
					} else {
						if (cur.right != null) { // go down another level, reset the uncle before reassigning current node
							uncle = cur.left;
							cur = cur.right;
						} else { // this is where the node will be inserted, so append to cur
							cur.setRight(node);
							if (cur.color == Node.RED) {
								rebalance(node, cur, grandParent, uncle);
							}
							insertOccurred = true;
						}
					}
				}
			}
		}
	}
	
	private void rebalance(Node current, Node parent, Node grandParent, Node uncle) {
		if (current == root) { // root is always colored black
			current.color = Node.BLACK;
			return;
		}
		if (parent.color != Node.BLACK) {
			if (uncle != null && uncle.color == Node.RED) { // uncle is red
				// recolor
				parent.color = Node.BLACK;
				uncle.color = Node.BLACK;
				grandParent.color = Node.RED;
				
				// continue up the tree recoloring
				current = grandParent;
				parent = current.parent;
				if (parent != null) {
					grandParent = parent.parent;
					if (grandParent != null) {
						if (parent.isLeft) {
							uncle = grandParent.right;
						} else {
							uncle = grandParent.left;
						}
					} else {
						uncle = null;
					}
				} else {
					grandParent = null;
				}
				rebalance(current, parent, grandParent, uncle);
			} else { // uncle is black
				// 4 cases: left-left, left-right, right-right, right-left
				// case 1: left-left
				if (parent.isLeft && current.isLeft) {
					rightRotate(grandParent);
					swapColor(grandParent, parent);
				}
				// case 2: left-right
				else if (parent.isLeft && !current.isLeft) {
					leftRotate(parent);
					rightRotate(grandParent);
					swapColor(grandParent, current);
				}
				// case 3: right-right
				else if (!parent.isLeft && !current.isLeft) {
					leftRotate(grandParent);
					swapColor(grandParent, parent);
				}
				// case 4: right-left
				else {
					rightRotate(parent);
					leftRotate(grandParent);
					swapColor(grandParent, current);
				}
			}
		}
	}
	
	private void swapColor(Node node, Node otherNode) {
		int color = node.color;
		node.color = otherNode.color;
		otherNode.color = color;
	}
	
	private void leftRotate(Node node) {
		Node right = node.right;
		Node rightLeft = right.left;
		node.setRight(rightLeft);
		Node parent = node.parent;
		if (parent == null) { // node is root
			root = right;
		} else {
			parent.setLeft(right);
		}
		right.setLeft(node);
	}

	private void rightRotate(Node node) {
		Node left = node.left;
		Node leftRight = left.right;
		node.setLeft(leftRight);
		Node parent = node.parent;
		if (parent == null) { // node is root
			root = left;
		} else {
			parent.setRight(left);
		}
		left.setRight(node);
	}
	
	public boolean remove(T item) {
		Node cur = root;
		boolean removed = false;
		while (cur != null && !removed) {
			int comparisonResult = comparator.compare(item, cur.item);
			if (comparisonResult == 0) { // found it
				Node parent = cur.parent;
				Node right = cur.right;
				Node left = cur.left;
				if (parent == null) { // was root
					if (cur.right != null) { // had a right, which will now become root
						root = cur.right;
						insert(left); // insert old left to new tree with new root (may be null)
					} else {
						root = cur.left; // left will become root (may be null)
					}
				} else if (cur.isLeft) { // is left
					parent.setLeft(right);
					insert(left);
				} else { // is right
					parent.setRight(right);
					insert(left);
				}
				removed = true;
			} else if (comparisonResult < 0) {
				cur = cur.left;
			} else if (comparisonResult > 0) {
				cur = cur.right;
			}
		}
		return removed;
	}
	
	public boolean find(T item) {
		Node cur = root;
		boolean found = false;
		while (cur != null && !found) {
			int comparisonResult = comparator.compare(item, cur.item);
			if (comparisonResult == 0) {
				found = true;
			} else if (comparisonResult < 0 && cur.left != null) {
				cur = cur.left;
			} else if (comparisonResult > 0 && cur.right != null) {
				cur = cur.right;
			} else {
				cur = null; // nowhere to go (will cause loop to stop and return false)
			}
		}
		return found;
	}
	
	private void display(Node cur) {
		if (cur != null) {
			display(cur.left);
			System.out.print(" " + cur.item + (cur.color == Node.RED ? "r" : "b"));
			display(cur.right);
		}
	}
	
	public static void main(String[] args) {
		RedBlackTree<Integer> t = new RedBlackTree<Integer>(new Comparator<Integer>() {
			public int compare(Integer i, Integer i1) {
				return i.compareTo(i1);
			}
		});
		t.insert(10);
		t.insert(5);
		t.insert(12);
		t.insert(11);
		t.insert(11);
		t.insert(12);
		t.insert(4);
		t.insert(7);
		t.insert(6);
		t.insert(9);
		t.display(t.root);
		System.out.println();
		t.remove(11);
		System.out.println(t.find(11));
		System.out.println(t.find(7));
		System.out.println(t.find(1));
		t.display(t.root);
	}

	
	private class Node {
		private static final int RED = 0;
		private static final int BLACK = 1;
		private Node parent;
		private Node left;
		private Node right;
		private T item;
		private int color;
		private boolean isLeft;
		
		Node(Node parent, Node left, Node right, T item, int color, boolean isLeft) {
			this.parent = parent;
			this.left = left;
			this.right = right;
			this.item = item;
			this.color = color;
			this.isLeft = isLeft;
		}
		
		void setParent(Node newParent) {
			this.parent = newParent;
		}

		void setLeft(Node newLeft) {
			this.left = newLeft;
			if (newLeft != null) {
				newLeft.setParent(this);
				newLeft.setIsLeft(true);
			}
		}

		void setRight(Node newRight) {
			this.right = newRight;
			if (newRight != null) {
				newRight.setParent(this);
				newRight.setIsLeft(false);
			}
		}

		void setItem(T item) {
			this.item = item;
		}
		
		void setColor(int color) {
			this.color = color;
		}
		
		void setIsLeft(boolean isLeft) {
			this.isLeft = isLeft;
		}
	}
}
