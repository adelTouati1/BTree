import java.util.LinkedList;

public class cache {
	
	private int hits;
	private int misses;
	private LinkedList<BTreeNode> nodeList;
	private int maximumSize;
	
	   public cache(int maximumSize){
		   nodeList = new LinkedList<BTreeNode>();
	        this.maximumSize = maximumSize;
	       
	    }
	
	private void deleteCache() {
	nodeList.clear();
	}
	
	private int getSize() {
	return nodeList.size();
	}
	
	  public boolean isFull(){
	        if (getSize() == maximumSize) {
	        	return true;
	        }else {
	        	return false;
	        }
	    }
	
	public int getMisses(){
	        return misses;
	    }

    public int getHits(){
        return hits;
    }
    
    public int incrementMisses(){
        return misses++;
    }
    
    public int incrementHits(){
        return hits++;
    }
    
    public int getReference(){
    	int reference = hits + misses;
        return reference;
    }
    
    public BTreeNode add(BTreeNode x,int offset){
    	BTreeNode lNodes = null;
        if (isFull()){
            lNodes = nodeList.removeLast();
        }
        nodeList.addFirst(x);
        return lNodes;
    }
    
    public BTreeNode getNode(long offset) {
		
		for (int i = 0; i <  nodeList.size(); i++) {
			if ( nodeList.get(i).getnodeOffset() == offset) {
				BTreeNode returningNode =  nodeList.remove(i);
				 nodeList.addFirst(returningNode);
				return returningNode;
			}
		}
		return null;
	}
    
    
}
