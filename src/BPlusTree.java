import java.util.*;
import java.util.Map.Entry;
import java.util.*;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 */
public class BPlusTree {

	public Node root;
	public static final int D = 2;
	public static final int CAPACITY = 2 * D;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public String search(int key) {
		LeafNode n = treeSearch(root, key);
		if (n == null)
			return null;

		int index = n.keys.indexOf(new Integer(key));
		String result = n.values.get(index);
		System.out.println("search for" + key + ",node key:" + index + ","
				+ "value:" + result);
		return result;

	}

	public LeafNode treeSearch(Node n, int key) {
		if (n == null)
			return null;
		if (n.isLeafNode) {
			System.out.println("LeafNode we find is "
					+ ((LeafNode) n).toString());
			return ((LeafNode) n);

		} else {

			if (key < n.keys.get(0).intValue()) {
				System.out.println("touching node"
						+ ((IndexNode) n).children.get(0));
				return treeSearch(((IndexNode) n).children.get(0), key);
			}
			int a = n.keys.size() - 1;
			if (key > n.keys.get(a)) {
				System.out.println("touching node"
						+ ((IndexNode) n).children.get(a + 1));
				return treeSearch(((IndexNode) n).children.get(a + 1), key);
			} else {
				int m = CAPACITY - 1;
				int i = 0;

				while (i <= m) {
					if (!((((IndexNode) n).keys.get(i).intValue() <= key) && (((IndexNode) n).keys
							.get(i + 1).intValue() > key)))
						i++;
				}
				System.out.println("touching node"
						+ ((IndexNode) n).children.get(i + 1));
				return treeSearch(((IndexNode) n).children.get(i + 1), key);

			}

		}
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(int key, String value) {
		System.out.println("root in insert is" + root);
		Map.Entry<Integer, Node> ety = insert_Tree(root, key, value);

		if (root == null) {
			root = new LeafNode(key, value);
			System.out.println("now root is" + key + "," + value);
		} else {
			if (ety == null) {
				System.out.println("Done with insertion");
				System.out.println("now root 1 is" + root);
			}
			if (ety != null) {
				System.out.println("Return entry isn't not null");
				System.out.println("We have an entry:" + ety.getKey() + ":"
						+ ety.getValue());
				root = new IndexNode(ety.getKey(), root, ety.getValue());
				System.out.println("root is now" + root + "with children");
				for (int i = 0; i < ((IndexNode) root).children.size(); i++) {
					System.out.println(((IndexNode) root).children.get(i));
				}

			}
		}
	}

	public Entry<Integer, Node> insert_Tree(Node n, int key, String value) {

		if (n == null)
			return null;
		if (n.isLeafNode) {
			LeafNode ln = (LeafNode) n;
			System.out.println(key + "," + value
					+ " is being inserting to LeafNode" + ln);
			ln.insertSorted(key, value);
			if (ln.isOverflowed()) {
				System.out.println("spliting Leafnode:" + ln);
				return splitLeafNode(ln);
			}
		} else {
			IndexNode in = (IndexNode) n;
			System.out.println("Dealing with indexNode:" + in);
			// int m = CAPACITY - 1;
			Map.Entry<Integer, Node> ety = null;
			// which way to insert
			int a = in.keys.size() - 1;
			if (key < in.keys.get(0).intValue()) {
				System.out.println("1st if in insert_Tree is executing");
				System.out.println("insert key" + key + " to node"
						+ in.children.get(0));
				ety = insert_Tree(in.children.get(0), key, value);
			}

			else if (key > in.keys.get(a)) {
				System.out.println("2nd if in insert_Tree is executing");
				System.out.println("now " + key + "is bigger than "
						+ in.keys.get(a));
				System.out.println("insert key" + key + "to node"
						+ in.children.get(a + 1));
				ety = insert_Tree(in.children.get(a + 1), key, value);

			} else {
				System.out.println("else in insert_Tree is executing");
				int i = 0;
				while (i < in.keys.size() - 1) {
					int nkv = in.keys.get(i).intValue();
					int nkvnext = in.keys.get(i + 1).intValue();
					if (!(nkv <= key) && (key < nkvnext))
						i++;
				}
				System.out.println("insert key" + key + "to node"
						+ in.children.get(i + 1));
				ety = insert_Tree(in.children.get(i + 1), key, value);
			}

			if (ety != null) {
				System.out.println("I know I'm an ety " + ety.getKey() + ":"
						+ ety.getValue());
				System.out.println("Now in is" + in);
				int index = 0;
				int counter = in.keys.size();
				System.out.println("counter is " + counter);
				while (index < counter
						&& in.keys.get(index).intValue() < ety.getKey()
								.intValue()) {
					index++;
					System.out.println("Index:" + index);

				}

				in.insertSorted(ety, index);
				System.out.println("after insertsorted in is" + in);
				System.out.println("lalalalalla");
				if (in.isOverflowed())

					return splitIndexNode(in);
			}

		}

		return null;

	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	public Entry<Integer, Node> splitLeafNode(LeafNode leaf) {
		System.out.println("Now in splitLeafNode method");
		if (leaf == null)
			return null;
		if (leaf.isOverflowed()) {
			System.out.println(leaf + "is overflowed");
			List<Integer> newKeys = new ArrayList<Integer>();
			List<String> newValues = new ArrayList<String>();

			for (int i = 0; i <= D; i++) {
				Integer a = leaf.keys.get(i + D);
				String b = leaf.values.get(i + D);
				newKeys.add(a);
				newValues.add(b);
			}
			LeafNode m = new LeafNode(newKeys, newValues);// create new leaf
															// keys/values
			System.out.println("m leaf is" + m); // node:m with D+1

			for (int i = CAPACITY; i >= D; i--) {
				System.out.println("now removing index " + i);
				System.out.println("Removing" + leaf.keys.get(i) + " from "
						+ leaf);
				leaf.keys.remove(i);
				leaf.values.remove(i);// n now has D children
			}
			Map.Entry<Integer, Node> ety = new AbstractMap.SimpleEntry<Integer, Node>(
					((Integer) m.keys.get(0)), m);// splitkey/newnode
			System.out.println("in splitnode entry we get:" + ety.getKey()
					+ ":" + ety.getValue());
			System.out.println("Root in split is" + root);
			return ety;

		}

		return null;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index
	 * @return new key/node pair as an Entry
	 */
	public Entry<Integer, Node> splitIndexNode(IndexNode index) {

		if (index == null)
			return null;
		if (index.isOverflowed()) {
			List<Integer> newKeys = new ArrayList<Integer>();
			List<Node> newChildren = new ArrayList<Node>();

			for (int i = 1; i <= D; i++) {
				Integer a = index.keys.get(i + D);
				newKeys.add(a);

			}
			for (int j = 1; j <= D + 1; j++) {
				Node b = index.children.get(j + D);
				newChildren.add(b);
			}

			IndexNode m = new IndexNode(newKeys, newChildren);// create new leaf
																// node:m with
																// D+1
			Integer entryKey = index.keys.get(D); // keys/values
			for (int i = CAPACITY; i >= D; i--) {
				System.out.println(i);
				System.out.println("Removing" + index.keys.get(i) + " from "
						+ index);
				index.keys.remove(i);
				index.children.remove(i + 1);// n now has D children
			}
			Map.Entry<Integer, Node> ety = new AbstractMap.SimpleEntry<Integer, Node>(
					entryKey, m);// splitkey/newnode
			System.out.println("in splitnode entry we get:" + ety.getKey()
					+ ":" + ety.getValue());
			return ety;

		}

		return null;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(int key) {
		if (root == null)
			return;
		else {
			int result = delete_Tree(null, root, key);
			System.out.println("delete get result from delete_Tree" + result);
		}
		// parent is null handled here
	}

	public int delete_Tree(Node parent, Node curNode, int key) {
		// handle LeafNode
		System.out.println("Delete_Tree:parent:" + parent + "current:"
				+ curNode + " key " + key);
		if (curNode == null)
			return -1;
		if (curNode.isLeafNode) {

			LeafNode lf = (LeafNode) curNode;
			System.out.println("Current is leafnode: " + lf);
			// delete key in leafnode
			int kIndex = lf.keys.indexOf(new Integer(key));
			System.out.println("key Index in leafNode" + lf + " is: " + kIndex);
			lf.keys.remove(kIndex);
			lf.values.remove(kIndex);
			System.out.println("After deletion cur leafnode is:" + lf);
			if (lf.isUnderflowed() && lf.keys.size() != 0) {
				System.out.println("underflow leafnode");
				if (parent == null) {
					return -1;
				}

				// find sibling
				LeafNode sibling;

				int curIndex = ((IndexNode) parent).children.indexOf(lf);
				System.out.println("LeafNode " + lf
						+ " 's index in children is" + curIndex);
				if (curIndex == 0) {
					int sIndex = curIndex + 1;
					sibling = (LeafNode) ((IndexNode) parent).children
							.get(sIndex);
					System.out.println("cur node" + lf + "'s sibling is"
							+ sibling);
				} else {
					int sIndex = curIndex - 1;
					sibling = (LeafNode) ((IndexNode) parent).children
							.get(sIndex);
					System.out.println("cur node" + lf + "'s sibling is"
							+ sibling);
				}

				boolean flag = sibling.keys.size() < lf.keys.size();
				LeafNode left = flag ? sibling : lf;
				LeafNode right = (!flag) ? sibling : lf;
				// invoke to handle underflow
				System.out.println("underflow between left:" + left + " and"
						+ "right:" + right);
				int leafResult = handleLeafNodeUnderflow(left, right,
						(IndexNode) parent);
				System.out.println(" returns" + leafResult);

				return leafResult;

			} else {
				// leaf not underflowed
				return -1;
			}

		}

		// handle indexNode
		else {
			IndexNode in = (IndexNode) curNode;
			System.out.println("Current is indexNode: " + in);
			// find child
			Node child;
			int a = in.keys.size() - 1;
			if (key < in.keys.get(0).intValue()) {
				child = in.children.get(0);
				System.out.println("1st if" + " key: " + key + "child: "
						+ child);
			}

			else if (key > in.keys.get(a)) {
				child = in.children.get(a + 1);
				System.out
						.println("2nd if" + "key: " + key + "child: " + child);
			} else {
				int i = 0;
				while (i < in.keys.size() - 1) {
					int nkv = in.keys.get(i).intValue();
					int nkvnext = in.keys.get(i + 1).intValue();
					if (!(nkv <= key) && (key < nkvnext)) {
						System.out.println("if " + nkv + " <=" + key + " and "
								+ key + "<" + nkvnext);
						i++;
						System.out.println("i in while now" + i);
					}
				}
				System.out.println("children position:" + i + "+1");
				child = in.children.get(i + 1);
				System.out
						.println("3rd if" + "key: " + key + "child: " + child);
			}

			int result = delete_Tree(in, child, key);

			{
				if (result == -1)
					return -1;

				else {
					System.out.println("remove " + result + "'th element from "
							+ in);
					// need to handle current node to delete element
					in.keys.remove(result);
					if (in.keys.size() == 0) {
						root = in.children.get(0);
						return -1;
					}
					if (in.isUnderflowed() && in.keys.size() != 0) {

						System.out.println(in + " is now underflowed");
						IndexNode sibling;

						int curIndex = ((IndexNode) parent).children
								.indexOf(in);

						if (curIndex == 0) {
							// current node is leftmost
							sibling = (IndexNode) ((IndexNode) parent).children
									.get(curIndex + 1);
							// define whether node is small or big
							boolean flag = sibling.keys.size() < in.keys.size();
							IndexNode leftIndex = flag ? sibling : in;
							IndexNode rightIndex = (!flag) ? sibling : in;
							// invoke to handle underflow
							int indexResult = handleIndexNodeUnderflow(
									leftIndex, rightIndex, (IndexNode) parent);

							return indexResult;
						} else {
							// index of current node not leftmost

							int lIndex = curIndex - 1;
							// find left sibling
							sibling = (IndexNode) ((IndexNode) parent).children
									.get(lIndex);
							// define whether node is small or big
							boolean flag = sibling.keys.size() < in.keys.size();
							IndexNode leftIndex = flag ? sibling : in;
							IndexNode rightIndex = (!flag) ? sibling : in;
							// invoke to handle underflow
							int indexResult = handleIndexNodeUnderflow(
									leftIndex, rightIndex, (IndexNode) parent);
							return indexResult;
						}

					}

					else {
						return -1;

					}

				}

			}
		}

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode left, LeafNode right,
			IndexNode parent) {
		System.out.println("enter handleNodeUnderflow method");
		int bigSize = right.keys.size();
		int smallSize = left.keys.size();

		if (bigSize > D) {

			// big on the right
			if (parent.children.indexOf(right) > parent.children.indexOf(left)) {
				System.out.println("big size>D and big on the right");
				int disK = right.keys.get(0);// this key value going to be
												// redistributed
				String disV = right.values.get(0);// this value going to be
													// redistributed
				int pushK = right.keys.get(1);// key to be pushed

				// replace parent
				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() <= right.keys
								.get(0).intValue()) {
					parK++;
				}
				parK--;// find parent position

				parent.keys.set(parK, pushK);// replace parent with new key

				// add new key/value to left
				left.insertSorted(disK, disV);

				// remove disNode from right
				int childIndex = parK + 1;
				LeafNode cNode = (LeafNode) (parent.children.get(childIndex));
				cNode.keys.remove(0);
				cNode.values.remove(0);

			}

			else {
				System.out.println("big size>D and big on the left");
				// big on the left
				int disK = right.keys.get(bigSize - 1);// this key value needs
				System.out.println("key value going to move is " + disK); // to
																			// be
																			// redistributed
				String disV = right.values.get(bigSize - 1);// this value going
															// to be
															// redistributed
				System.out.println("dis V executed");
				int pushK = disK;// key to be pushed

				// replace parent
				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() <= left.keys.get(0)
								.intValue()) {
					parK++;
				}
				parK--;// find parent position
				System.out.println("parent key position is " + parK);
				parent.keys.set(parK, pushK);// replace parent with new key
				System.out.println("after set parent at index " + parK + "is "
						+ parent.keys.get(parK));
				// add new key/value to left
				left.insertSorted(disK, disV);

				// remove disNode from right
				int childIndex = parK;
				LeafNode cNode = (LeafNode) (parent.children.get(childIndex));
				cNode.keys.remove(bigSize - 1);
				cNode.values.remove(bigSize - 1);

			}

			return -1;
		} else {

			if (parent.children.indexOf(right) > parent.children.indexOf(left)) {
				System.out.println("big size<=D and big on the right");
				// big on the right
				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() <= right.keys
								.get(0).intValue()) {
					parK++;
				}
				parK--;// find parent position

				// merge

				int counter = smallSize;
				while (counter > 0) {
					int merK = left.keys.get(counter - 1);
					String merV = left.values.get(counter - 1);
					right.insertSorted(merK, merV);
					counter--;
				}

				// remove left node
				parent.children.remove(left);

				return parK;
			}

			else {
				System.out.println("big size<=D and big on the left");
				// big on the left
				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() <= left.keys.get(0)
								.intValue()) {
					parK++;
				}
				parK--;// find parent position

				// merge

				int counter = smallSize;
				while (counter > 0) {
					int merK = left.keys.get(counter - 1);
					String merV = left.values.get(counter - 1);
					right.insertSorted(merK, merV);
					counter--;
				}

				// remove left node
				parent.children.remove(left);

				return parK;
			}

		}

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode leftIndex,
			IndexNode rightIndex, IndexNode parent) {
		System.out.println("Now in IndexNodeUnderflow method");
		int bigSize = rightIndex.keys.size();

		if (bigSize > D) {
			System.out.println("bigSize>D and big on right small on left");
			if (parent.children.indexOf(rightIndex) > parent.children
					.indexOf(leftIndex)) {
				int disK = rightIndex.keys.get(0);// this key value going to be
													// redistributed

				// find parent key position
				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() < rightIndex.keys
								.get(0).intValue()) {
					parK++;
				}
				parK--;// find parent position
				System.out
						.println("parent's position in" + parent + ":" + parK);

				leftIndex.keys.add(parent.keys.get(parK));// redistribute parent
				System.out.println("add" + parent.keys.get(parK) + " to"
						+ leftIndex); // key to left node

				parent.keys.set(parK, disK);// replace parent with new key
				System.out.println("replace parent with" + disK);
				// move grand child
				Node disC = rightIndex.children.get(0);
				leftIndex.children.add(disC);
				System.out.println("add" + disC + " to " + leftIndex
						+ "'s children arraylist");

				// move item from right
				System.out.println("remove" + rightIndex.keys.get(0));
				rightIndex.keys.remove(0);
				System.out.println("remove" + rightIndex.children.get(0));
				rightIndex.children.remove(0);
				System.out.println("remove done");
				return -1;
			} else {
				System.out.println("bigSize>D and big on left small on right");
				int disK = rightIndex.keys.get(bigSize - 1);// this key value
															// going to be
															// redistributed

				// find parent key position
				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() < leftIndex.keys
								.get(0).intValue()) {
					parK++;
				}
				parK--;// find parent position
				System.out
						.println("parent's position in" + parent + ":" + parK);
				leftIndex.keys.add(0, parent.keys.get(parK));// redistribute
																// parent key to
																// left node
				System.out.println("add" + parent.keys.get(parK) + " to"
						+ leftIndex);
				parent.keys.set(parK, disK);// replace parent with new key
				System.out.println("replace parent with" + disK);
				// move grand child
				Node disC = rightIndex.children.get(bigSize);
				leftIndex.children.add(0, disC);
				System.out.println("add" + disC + " to " + leftIndex
						+ "'s children arraylist");

				// move item from right
				System.out.println("remove key"
						+ rightIndex.keys.get(bigSize - 1));
				rightIndex.keys.remove(bigSize - 1);
				System.out.println("remove child"
						+ rightIndex.children.get(bigSize - 1));
				rightIndex.children.remove(bigSize);

				return -1;
			}
		} else {

			int smallSize = leftIndex.keys.size();
			if (parent.children.indexOf(rightIndex) > parent.children
					.indexOf(leftIndex)) {
				System.out.println("bigSize<=D and big on right small on left");

				// find parent key position
				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() < rightIndex.keys
								.get(0).intValue()) {
					parK++;
				}
				parK--;// find parent position
				System.out
						.println("parent's position in" + parent + ":" + parK);

				// merge leftIndex with parent

				rightIndex.keys.add(0, parent.keys.get(parK));
				int counter = smallSize;
				// merge leftIndex with rightIndex, and their children
				while (counter > 0) {
					int merK = leftIndex.keys.get(counter - 1);
					Node merC = leftIndex.children.get(counter);
					rightIndex.keys.add(0, merK);
					rightIndex.children.add(0, merC);
					System.out.println("in while loop" + counter
							+ " now right index is" + rightIndex);
					counter--;
				}
				Node merC = leftIndex.children.get(counter);
				rightIndex.children.add(0, merC);
				System.out.println("after adding everything rightIndex is "
						+ rightIndex);

				// remove children
				System.out.println("remove leftIndex" + leftIndex);
				parent.children.remove(leftIndex);

				return parK;

			} else {
				System.out
						.println("bigSize<=D and big on left and small on right");
				// find parent key position

				int parK = 0;
				while (parK < parent.keys.size()
						&& parent.keys.get(parK).intValue() < leftIndex.keys
								.get(0).intValue()) {
					parK++;
				}
				parK--;// find parent position
				System.out
						.println("parent's position in" + parent + ":" + parK);
				// merge leftIndex with parent
				rightIndex.keys.add(parent.keys.get(parK));
				// merge leftIndex with rightIndex, and their children

				int counter = 0;
				while (counter < smallSize) {
					int merK = leftIndex.keys.get(counter);
					Node merC = leftIndex.children.get(counter);
					rightIndex.keys.add(merK);
					rightIndex.children.add(merC);
					System.out.println("in while loop" + counter
							+ " now right index is" + rightIndex);
					counter++;
				}
				Node merC = leftIndex.children.get(counter);
				rightIndex.children.add(merC);
				System.out.println("after adding everything rightIndex is "
						+ rightIndex);

				// remove children
				System.out.println("remove leftIndex" + leftIndex);
				parent.children.remove(leftIndex);

				return parK;
			}

		}

	}

}
