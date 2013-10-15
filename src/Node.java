import java.util.ArrayList;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
public class Node {
	protected boolean isLeafNode;
	protected ArrayList<Integer> keys;

	public boolean isOverflowed() {
		return keys.size() > 2 * BPlusTree.D;
	}

	public boolean isUnderflowed() {
		return keys.size() < BPlusTree.D;
	}
	public String toString(){
		String result=null;
		for (int i = 0; i < keys.size(); i++) {
			result += "I'm just a node with key" + keys.get(i) + "/";
		}
		
		return result;
	}
}
