package ext.sim.agents;

import java.util.Vector;

public class Order {

	private Vector<Pair> mOrder;
	private int mSize;

	public Order(int size) {

		mSize = size;

		mOrder = new Vector<Pair>(mSize);

		for (int i = 0; i < size; i++)
			mOrder.add(new Pair(i));
	}

	public void replace(int i, int j) {

		Pair tmp = mOrder.set(j, mOrder.get(i));
		mOrder.set(i, tmp);
	}
	
	public int getSize(){
		return mSize;
	}

	public int getAgent(int i) {
		return mOrder.get(i).getAgent();
	}

	public int getCounter(int i) {
		return mOrder.get(i).getCounter();
	}

	public void incCounter(int i) {
		mOrder.get(i).incCounter();
	}

	public void resetCounter(int i) {
		mOrder.get(i).resetCounter();
	}
}
