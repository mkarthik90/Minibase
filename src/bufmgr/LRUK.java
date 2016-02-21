package bufmgr;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LRUK extends Replacer {

	int frames[];
	int correlated_reference_period = 0;
	// by default
	int lastRef = 3;
	// Hist stores the history which will be in a map. The pagenumber will be
	// hashed and stored.
	// For each.
	Map<Integer, LinkedList> HIST = new LinkedHistMap<Integer, LinkedList>();
	Map<Integer, Long> last = new LinkedLongHashMap<Integer, Long>();
	int nframes = 0;

	protected LRUK(BufMgr javamgr, int lastRef) {
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

		update(frameNo);
	}

	public void setBufferManager(BufMgr mgr) {
		super.setBufferManager(mgr);
		frames = new int[mgr.getNumBuffers()];
		nframes = 0;
	}

	private void update(int frameNo) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		long t = System.currentTimeMillis();
		long correl_period_of_refd_page = 0;
		if (t - last.get(frameNo) >= correlated_reference_period) {
			List historyDetailsList = HIST.get(frameNo);
			long histFirstValueOfPage = (Long) historyDetailsList.get(1);
			correl_period_of_refd_page = last.get(frameNo)
					- histFirstValueOfPage;

			LinkedList historyDetailsOfFrame = HIST.get(frameNo);
			// TODO change 2 to 1
			for (int i = 2; i < lastRef; i++) {
				historyDetailsOfFrame.add(i,
						(Long) historyDetailsOfFrame.get(i - 1)
								+ correl_period_of_refd_page);
			}
			historyDetailsOfFrame.add(1, t);
			HIST.put(frameNo, historyDetailsOfFrame);
			last.put(frameNo, t);
		} else {
			LinkedList historyDetailsOfFrame = HIST.get(frameNo);
			if (historyDetailsOfFrame == null)
				historyDetailsOfFrame = new LinkedList();
			historyDetailsOfFrame.add(t);
			HIST.put(frameNo, historyDetailsOfFrame);
			last.put(frameNo, t);
		}
	}

	public int pick_victim() throws BufferPoolExceededException {

		int numBuffers = mgr.getNumBuffers();
		int frame = 0;
		int victim = -1;

		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		if (nframes < numBuffers) {

			// INitialize HIST and LAST
			frame = nframes++;
			frames[frame] = frame;
			state_bit[frame].state = Pinned;
			(mgr.frameTable())[frame].pin();
			return frame;
		} else {
			long t = System.currentTimeMillis();
			long min = t;
			for (int i = 0; i < numBuffers; ++i) {
				frame = frames[i];

				long lastOfPage = (long) last.get(frame);
				List historyOfReference = (List) HIST.get(frame);

				// Finding K backward distance for the frame
				// Last Reference is the K value
				// CHECK K -1
				long lastRefOfPage = (Long) historyOfReference.get(lastRef);
				if (t - lastOfPage > correlated_reference_period
						&& lastRefOfPage < min
						&& state_bit[frame].state != Pinned) {

					victim = frame;
					min = lastRefOfPage;
				}

			}
		}
		// If victim is not -1 then we found an empty slot and update the state
		// bits and frame in buffer manager respectively
		if (victim != -1) {
			state_bit[victim].state = Pinned;
			(mgr.frameTable())[victim].pin();
			return victim;
		}

		throw new BufferPoolExceededException(null,
				"bufmgr.BufferPoolExceededException");
	}

	public String name() {
		return "LRUK";
	}

	public int[] getFrames() {
		return frames;
	}

	public long back(int pagenumber, int i) {
		return last.get(pagenumber);
	}

	public long HIST(int pagenumber, int i) {
		LinkedList list = (LinkedList) HIST.get(pagenumber);
		return (Long) list.get(i);
	}

}

class LinkedLongHashMap<Integer, Long> extends LinkedHashMap<Integer, Long> {
	protected long defaultValue = 0;

	@Override
	public Long get(Object k) {
		return (Long) (containsKey(k) ? super.get(k) : defaultValue);
	}
}

class LinkedHistMap<Integer, LinkedList> extends
		LinkedHashMap<Integer, LinkedList> {

	protected List list = new java.util.LinkedList();

	public LinkedHistMap() {
		for (int i = 0; i < 10; i++)
			list.add((long) 0);
	}

	@Override
	public LinkedList get(Object k) {
		return (LinkedList) (containsKey(k) ? super.get(k) : list);
	}
}