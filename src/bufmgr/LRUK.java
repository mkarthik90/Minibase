package bufmgr;

public class LRUK extends Replacer {
	
	int frames[];

	protected LRUK(BufMgr javamgr) {
		super(javamgr);
		// TODO Auto-generated constructor stub
	}

	public int pick_victim() throws BufferPoolExceededException,
			PagePinnedException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getFrames() {
		// TODO Auto-generated method stub
		return frames;
	}

	public int back(int pagenumber, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long HIST(int pagenumber, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

}
