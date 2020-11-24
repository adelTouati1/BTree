
public class TreeObject {
	private long key;
	private int frequency;
	
	public TreeObject(long key) {
		this.key = key;
		this.frequency = 1;
	}
	
	public TreeObject(long key, int frequency) {
		this.key = key;
		this.frequency = frequency;
	}
	
	public void incrementFrequency() {
		++frequency;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public long getKey() {
		return key;
	}
}