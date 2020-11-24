
import java.util.Arrays;

public class BTreeNode {
	private int nodeOffset;
	private int numObjects;
	private boolean leaf;
	private TreeObject[] objects;
	private int[] childrenOffsets;
	
	public BTreeNode(int nodeOffset,int numObjects, Boolean leaf, int degree) {
		this.nodeOffset = nodeOffset;
		this.numObjects = numObjects;
		this.leaf = leaf;
		objects = new TreeObject[2*degree];
		childrenOffsets = new int[2*degree+1];
		Arrays.fill(childrenOffsets, -1);
	}

	/**
	 * @return the nodeOffset
	 */
	public int getnodeOffset() {
		return nodeOffset;
	}

	/**
	 * @param nodeOffset the nodeOffset to set
	 */
	public void setnodeOffset(int nodeOffset) {
		this.nodeOffset = nodeOffset;
	}

	/**
	 * @return the numObjects
	 */
	public int getNumObjects() {
		return numObjects;
	}

	/**
	 * @param numObjects the numObjects to set
	 */
	public void setNumObjects(int numObjects) {
		this.numObjects = numObjects;
	}

	/**
	 * @return the leaf
	 */
	public boolean isLeaf() {
		return leaf;
	}

	/**
	 * @param leaf the leaf to set
	 */
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	public TreeObject[] getObjects() {
		return objects;
	}
	
	public int[] getChildren() {
		return childrenOffsets;
	}

	/**
	 * @return the object[index]
	 */
	public TreeObject getObjectAt(int index) {
		return objects[index];
	}

	/**
	 * @param index, the obj to set
	 */
	public void setObjectAt(int index, TreeObject obj) {
		 objects[index] = obj;
	}
	
	/**
	 * @return the offsetOfChild
	 */
	public int getChildOffsetAt(int index) {
		return childrenOffsets[index];
	}

	/**
	 * @param index, the obj to set
	 */
	public void setChildrenOffsetAt(int index, int offset) {
		 childrenOffsets[index] = offset;
	}
}