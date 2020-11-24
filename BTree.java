import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/*
 *  BTree file Layout
 *  | BOF | int location of root | int degree | BTreeNode #1 | ... | BTreeNode #(n-1) | root | EOF |
 *  
 *  BTreeNode Layout
 *  int nodeOffset | int numObject | Boolean leaf | TreeObject #1 | ... | TreeObject #(2*degree-1) | int childOffset #1 | ... | int childOffet #(2*degree)
 *  
 *  TreeObject Layout
 *  long key | int frequency
 *  
 *  
 */
public class BTree {
	static int degree;
	int sizeOfMetaData = 8;
	static BTreeNode root;

	static RandomAccessFile file;
	static FileOutputStream fos;

	public BTree(int t, String BTreeFile, String dumpfile) {
		try {
			file = new RandomAccessFile(BTreeFile, "rwd");
			fos = new FileOutputStream(dumpfile);
			degree = t;
			file.write(new byte[8]);
			root = new BTreeNode((int) file.length(), 0, true, degree);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BTree(int t, String BTreeFile) {
		try {
			file = new RandomAccessFile(BTreeFile, "rwd");
			degree = t;
			file.write(new byte[8]);
			root = new BTreeNode((int) file.length(), 0, true, degree);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeTree() throws IOException {
		// write meta data
		file.seek(0);

		// locaton of root
		file.writeInt(root.getnodeOffset());

		// the degree of the BTree
		file.writeInt(degree);
	}

	private static int optimalDegree() { 

		double optimal = 4096;
		int Pointer = 4;
 		int Object = 12;
 		int Metadata = 12;
 
 		optimal += Object;
 		optimal -= Pointer;
 		optimal -= Metadata;
 		optimal /= (2 * (Object + Pointer));
 		
		return (int) Math.floor(optimal);
	}

	public BTree(String bTreeFile, String dumpfile) {
		this(optimalDegree(), bTreeFile, dumpfile);
	}
	
	public BTree(String bTreeFile) {
		this(optimalDegree(), bTreeFile);
	}

	private void WriteNodeToFile(BTreeNode node, int location) throws IOException {
		file.seek(location);

		// int nodeOffset
		file.writeInt(node.getnodeOffset());

		// int num Objects
		file.writeInt(node.getNumObjects());

		// Boolean leaf
		file.writeBoolean(node.isLeaf());

		if (node.getNumObjects() == 0) {
			file.write(new byte[12 * (2 * degree - 1) + 4 * (2 * degree)]);
			return;
		} else {
			// Write Tree Objects to file
			for (int i = 1; i <= 2 * degree - 1; i++) {
				if (node.getObjectAt(i) != null) {
					file.writeLong(node.getObjectAt(i).getKey());
					file.writeInt(node.getObjectAt(i).getFrequency());
				} else {
					file.write(new byte[12]);
				}
			}
		}

		if (node.isLeaf()) {
			file.write(new byte[4 * 2 * degree]);
		} else {
			// write all children pointers to file
			for (int i = 1; i <= 2 * degree; i++) {
				if (node.getChildOffsetAt(i) > 0) {
					file.writeInt(node.getChildOffsetAt(i));
				} else {
					file.writeInt(-1);
				}
			}
		}
	}

	public static BTreeNode ReadNodeFromFile(int location) throws IOException {
		file.seek(location);

		int nodeOffset = file.readInt();
		int numObjects = file.readInt();
		boolean bleaf = file.readBoolean();

		BTreeNode bNode = new BTreeNode(nodeOffset, numObjects, bleaf, degree);

		if (numObjects != 0) {
			// read Tree Objects from file
			for (int i = 1; i <= numObjects; i++) {
				long key = file.readLong();
				int frequency = file.readInt();
				bNode.setObjectAt(i, new TreeObject(key, frequency));
			}
		}

		file.skipBytes(((2 * degree - 1) - numObjects) * 12);

		if (!bleaf) {
			// read childOffsets from file
			for (int i = 1; i <= numObjects + 1; i++) {
				int childOffset = file.readInt();
				bNode.setChildrenOffsetAt(i, childOffset);
			}
		}

		file.skipBytes(((2 * degree) - (numObjects + 1)) * 4);

		return bNode;
	}

	private void SplitChild(BTreeNode x, int indexToSplitOn) throws IOException {

		// 1
		BTreeNode z = new BTreeNode((int) file.length(), 0, true, degree);

		// 2
		BTreeNode y = ReadNodeFromFile(x.getChildOffsetAt(indexToSplitOn));

		// 3
		z.setLeaf(y.isLeaf());

		// 4
		z.setNumObjects(degree - 1);

		// 5
		for (int j = 1; j <= degree - 1; j++) {
			// 6
			z.setObjectAt(j, y.getObjectAt(j + degree));
		}

		// 7
		if (!y.isLeaf()) {
			// 8
			for (int j = 1; j <= degree; j++) {
				// 9
				z.setChildrenOffsetAt(j, y.getChildOffsetAt(j + degree));
			}
		}

		// 10
		y.setNumObjects(degree - 1);

		// 11
		for (int j = x.getNumObjects() + 1; j >= indexToSplitOn + 1; j--) {
			// 12
			x.setChildrenOffsetAt(j + 1, x.getChildOffsetAt(j));
		}

		// 13
		x.setChildrenOffsetAt(indexToSplitOn + 1, z.getnodeOffset());

		// 14
		for (int j = x.getNumObjects(); j >= indexToSplitOn; j--) {
			// 15
			x.setObjectAt(j + 1, x.getObjectAt(j));
		}

		// 16
		x.setObjectAt(indexToSplitOn, y.getObjectAt(degree));

		// 17
		x.setNumObjects(x.getNumObjects() + 1);

		// 18
		WriteNodeToFile(y, y.getnodeOffset());

		// 19
		WriteNodeToFile(z, z.getnodeOffset());

		// 20
		WriteNodeToFile(x, x.getnodeOffset());
	}

	public void insert(long k) throws IOException {
		// increment frequency if k exists in BTree then exit
		if (incFreq(this.root, k)) {
			return;
		} else {
			// 1
			BTreeNode r = this.root;
			// 2
			if (r.getNumObjects() == (2 * degree - 1)) {
				// 3
				BTreeNode s = new BTreeNode((int) file.length(), 0, true, degree);
				// 4
				this.root = s;
				// 5
				s.setLeaf(false);
				// 6
				s.setNumObjects(0);
				// 7
				s.setChildrenOffsetAt(1, r.getnodeOffset());
				// write s to file
				WriteNodeToFile(s, s.getnodeOffset());
				// 8
				SplitChild(s, 1);
				// 9
				insertNonFull(s, k);
			} else {
				// 10
				insertNonFull(r, k);
			}
		}
	}

	private void insertNonFull(BTreeNode x, long k) throws IOException {

		BTreeNode ch = null;

		// 1
		int i = x.getNumObjects();
		// 2
		if (x.isLeaf()) {
			// 3
			while (i >= 1 && k < x.getObjectAt(i).getKey()) {
				// 4
				x.setObjectAt(i + 1, x.getObjectAt(i));
				// 5
				i--;
			}

			// 6
			x.setObjectAt(i + 1, new TreeObject(k));

			// 7
			x.setNumObjects(x.getNumObjects() + 1);

			// 8
			WriteNodeToFile(x, x.getnodeOffset());

			// 9

		} else {
			while (i >= 1 && k < x.getObjectAt(i).getKey()) {
				// 10
				i = i - 1;
			}
			// 11
			i = i + 1;
			// 12
			ch = ReadNodeFromFile(x.getChildOffsetAt(i));
			// 13
			if (ch.getNumObjects() == 2 * degree - 1) {
				// 14
				SplitChild(x, i);
				// 15
				if (k > x.getObjectAt(i).getKey()) {
					// 16
					i = i + 1;
				}
			}
			// 17
			ch = ReadNodeFromFile(x.getChildOffsetAt(i));
			insertNonFull(ch, k);
		}
	}

	public TreeObject search(BTreeNode x, long key) throws IOException {
		int i = 1;
		while (i <= x.getNumObjects() && key > x.getObjectAt(i).getKey()) {
			i++;
		}
		if (i <= x.getNumObjects() && key == x.getObjectAt(i).getKey()) {
			return x.getObjectAt(i);
		} else if (x.isLeaf()) {
			return null;
		} else {
			return search(ReadNodeFromFile(x.getChildOffsetAt(i)), key);
		}
	}

	private Boolean incFreq(BTreeNode x, long key) throws IOException {
		int i = 1;
		while (i <= x.getNumObjects() && key > x.getObjectAt(i).getKey()) {
			i++;
		}
		if (i <= x.getNumObjects() && key == x.getObjectAt(i).getKey()) {
			x.getObjectAt(i).incrementFrequency();
			WriteNodeToFile(x, x.getnodeOffset());
			return true;
		} else if (x.isLeaf()) {
			return false;
		} else {
			return incFreq(ReadNodeFromFile(x.getChildOffsetAt(i)), key);
		}
	}

	public static void inorderPrint(BTreeNode x) throws IOException {
		if (x.isLeaf()) {
			for (int i = 1; i <= x.getNumObjects(); i++) {
				TreeObject obj = x.getObjectAt(i);
				byte[] sequence = (GeneBankCreateBTree.decodeLongValue(obj.getKey()) + ": " + obj.getFrequency() + "\n").getBytes();
				fos.write(sequence);
			}
			return;
		}

		for (int i = 1; i <= x.getNumObjects(); i++) {
			inorderPrint(ReadNodeFromFile(x.getChildOffsetAt(i)));
			TreeObject obj = x.getObjectAt(i);
			byte[] sequence = (GeneBankCreateBTree.decodeLongValue(obj.getKey()) + ": " + obj.getFrequency() + "\n").getBytes();
			fos.write(sequence);
		}

		inorderPrint(ReadNodeFromFile(x.getChildOffsetAt(x.getNumObjects() + 1)));
	}

	public void DumpFile() throws IOException {
		inorderPrint(root);
	}
}