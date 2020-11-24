import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class GeneBankSearch {

	public static void main(String[] args) throws IOException {
		File file = new File(args[2]);
		try {
						
			RandomAccessFile bTreeFile = new RandomAccessFile(args[1], "rw");
			bTreeFile.seek(0);
			int rootOffset = bTreeFile.readInt();
			int degree = bTreeFile.readInt();
			BTree tree = new BTree(degree, args[1]);
			
			BTreeNode root = BTree.ReadNodeFromFile(rootOffset);
			
			Scanner fileScan = new Scanner(file);			
			while (fileScan.hasNextLine()) {
				String query = fileScan.nextLine();
				
				String binNum = "";
				query = query.toLowerCase();
				for (int j = 0; j < query.length(); j++) {
					String bin = "";
					
					switch (query.charAt(j)) {
					case 'a':
						bin = "00";
						break;
					case 't':
						bin = "11";
						break;
					case 'c':
						bin = "01";
						break;
					case 'g':
						bin = "10";
						break;
					}
					binNum = binNum + bin;
				}
				long keyVal = Long.parseLong(binNum, 2);
				
				TreeObject retVal = tree.search(root, keyVal);
				if (retVal != null) {
					System.out.println(query + ": " + retVal.getFrequency());
				}
			}
			fileScan.close();
			bTreeFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("The gbk file does not exist.");
		}
	}
}