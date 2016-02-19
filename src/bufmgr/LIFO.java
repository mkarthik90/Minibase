package bufmgr;

public class LIFO extends Replacer {

	/**
	 * private field An array to hold number of frames in the buffer pool
	 */

	private int frames[];

	/**
	 * private field number of frames used
	 */
	private int nframes;

	protected LIFO(BufMgr javamgr) {
		super(javamgr);
		frames = null;
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
	 * Finding a free frame in the buffer pool or choosing a page to replace
	 * using LIFO policy Page replacement happens by scanning the frames from
	 * beginning to the end to see if there is an unpinned page. If an unpinned
	 * page is found it is pinned and moved to initial position of the frame, so
	 * that from next scan if it is unpinned it can be pinned again
	 * 
	 * @return return the frame number return -1 if failed
	 */

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

		return -1;
	}

	public String name() {
		return "LIFO";
	}

	/**
	 * This method just pushes the frame to the beginning
	 * 
	 * @param frameNo
	 *            the frame number
	 */
	private void update(int frameNo) {

		// This frame needs to be pushed to the beginning
		// If it is the first frame no need to move it to the initial position
		int index;
		for (index = 0; index < nframes; ++index)
			if (frames[index] == frameNo)
				break;

		if (index != 0) {
			int frameToBeMoved = frames[index];
			while (index > 0) {
				frames[index - 1] = frames[index];
				index--;
			}
			frames[0] = frameToBeMoved;
		}
	}

}
