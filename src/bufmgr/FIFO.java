package bufmgr;

public class FIFO extends Replacer {

	/**
	 * private field An array to hold number of frames in the buffer pool
	 */

	private int frames[];

	/**
	 * private field number of frames used
	 */
	private int nframes;

	protected FIFO(BufMgr javamgr) {
		super(javamgr);
		frames = null;
	}

	public int pick_victim() throws BufferPoolExceededException,
			PagePinnedException {

		int numBuffers = mgr.getNumBuffers();
		int frame;

		if (nframes < numBuffers) {
			frame = nframes++;
			frames[frame] = frame;
			state_bit[frame].state = Pinned;
			(mgr.frameTable())[frame].pin();
			return frame;
		}

		for (int i = 0; i < numBuffers; ++i) {
			frame = frames[i];
			if (state_bit[frame].state != Pinned) {
				state_bit[frame].state = Pinned;
				(mgr.frameTable())[frame].pin();
				update(frame);
				return frame;
			}
		}

		throw new BufferPoolExceededException(null, "BUFMGR: BUFFER_EXCEEDED.");

	}

	public String name() {
		// TODO Auto-generated method stub
		return "FIFO";
	}

	/**
	 *This method does nothing 
	 * @param frameNo
	 *            the frame number
	 */
	private void update(int frameNo) {

		//This frame needs to be pushed to the end
		//If it is the last frame no need to move it to the end
		int index;
	     for ( index=0; index < nframes; ++index )
	        if ( frames[index] == frameNo )
	            break;

	    while ( ++index < nframes )
	        frames[index-1] = frames[index];
	        frames[nframes-1] = frameNo;

	}

	/**
	 * Calling super class the same method Initializing the frames[] with number
	 * of buffer allocated by buffer manager set number of frame used to zero
	 * 
	 * @param mgr
	 *            a BufMgr object
	 * @see BufMgr
	 * @see Replacer
	 */
	public void setBufferManager(BufMgr mgr) {
		super.setBufferManager(mgr);
		//Assuming all frames are free while initializign
		frames = new int[mgr.getNumBuffers()];
		nframes = 0;
	}

	/**
	 * calll super class the same method pin the page in the given frame number
	 * move the page to the end of list
	 * 
	 * @param frameNo
	 *            the frame number to pin
	 * @exception InvalidFrameNumberException
	 */
	public void pin(int frameNo) throws InvalidFrameNumberException {
		super.pin(frameNo);
	}

	/**
	 * print out the information of frame usage
	 */
	public void info() {
		super.info();

		System.out.print("FIFO REPLACEMENT");

		for (int i = 0; i < nframes; i++) {
			if (i % 5 == 0)
				System.out.println();
			System.out.print("\t" + frames[i]);

		}
		System.out.println();
	}

}
