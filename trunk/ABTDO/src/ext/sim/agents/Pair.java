package ext.sim.agents;

public class Pair {

	private int mAgent;
	private int mCounter;

	public Pair(int pAgent) {
		mAgent = pAgent;
		mCounter = 0;
	}

	public int getAgent() {
		return mAgent;
	}

	public int getCounter() {
		return mCounter;
	}

	public void incCounter() {
		mCounter++;
	}

	public void resetCounter() {
		mCounter = 0;
	}
	
	@Override
	public String toString() {
		return "(" + mAgent + "," + mCounter +")"; 
	}
}
