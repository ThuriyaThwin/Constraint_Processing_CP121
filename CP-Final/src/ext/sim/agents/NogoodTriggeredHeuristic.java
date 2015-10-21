package ext.sim.agents;

public class NogoodTriggeredHeuristic implements Heuristic {

	private int my_id = -1;
	
	public NogoodTriggeredHeuristic(int id) {
		my_id = id;
	}

	@Override
	public void changeOrder(Order order, ABTDOAgent agent) {

		System.err.println("order before for agent " + my_id + ": " + order);
		System.err.flush();
		
		int myIndex = order.getPosition(my_id);
		int agentIndex = order.getPosition(agent.getId());;
		
		int startIndex = order.getPosition(my_id);
		int endIndex = order.getSize() - 1;
		
		if (startIndex != agentIndex)
			order.replace(startIndex + 1, agentIndex);
		
		order.incCounter(myIndex);
	
		for (int i = startIndex + 1; i <= endIndex; i++)
			order.resetCounter(i);
		
		System.err.println("order after for agent " + my_id + ": " + order);
		System.err.flush();
	}
}
