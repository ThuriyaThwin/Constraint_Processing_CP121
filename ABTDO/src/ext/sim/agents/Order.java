package ext.sim.agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Order implements Comparable<Order>{

	private int mSize;
	private Vector<Pair> mOrder;
	private Map<Integer, Integer> mPositions;	// agentId, position

	public Order(int size) {

		mSize = size;

		mOrder = new Vector<Pair>(mSize);

		for (int i = 0; i < size; i++)
			mOrder.add(new Pair(i));
		
		mPositions = new HashMap<Integer, Integer>(mSize);
		
		for (int i = 0; i < size; i++)
			mPositions.put(i, i);
	}

	public void replace(int i, int j) {

		Pair tmp = mOrder.set(j, mOrder.get(i));
		mOrder.set(i, tmp);
		
		mPositions.put(i, j);
		mPositions.put(j, i);
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
	
	public int getPosition(int agent){
		return mPositions.get(agent);
	}
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		
		for (Pair pair : mOrder)
			sb.append(pair);
		
		return sb.toString();
	}

	@Override
	public int compareTo(Order other) {

		int size = Math.min(mSize, other.mSize);
		
		int ci = -1;
		int cj = -1;
		
		int ans = 0;
		
		for (int i = 0; i < size; i++){
			
			if (getAgent(i) == other.getAgent(i)){
				
				ci = getCounter(i);
				cj = other.getCounter(i);

				if (ci > cj) ans = 1;
				else if (ci < cj) ans = -1;
				else continue;
			}
			
			break;
		}

		return ans;
	}
}
