package ext.sim.agents;

public class NoChangeHeuristic implements Heuristic {

	@Override
	public void changeOrder(Order order, ABTDOAgent agent) {

		System.err.println("order before for agent " + agent.getId() + ": " + order);
		System.err.flush();
		
		int startIndex = order.getPosition(agent.getId());
		int endIndex = order.getSize() - 1;
		
		order.incCounter(startIndex);
		
		for (int i = startIndex + 1; i <= endIndex; i++)
			order.resetCounter(i);
		
		System.err.println("order after for agent " + agent.getId() + ": " + order);
		System.err.flush();
	}
}
