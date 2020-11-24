
public class cacheNode {
   
	private int offset;
	private BTreeNode data;
	
	
	public cacheNode (BTreeNode data, int offset) {
		this.offset = offset;
		this.data = data;
	}
	
	public int getOffset() {
	return offset;	
	}
	
	public BTreeNode getData() {
		return data;
		
	}
	
	
}
