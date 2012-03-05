package ext.sim.agents;

public class RandomHeuristic implements Heuristic {

	public void changeOrder(Order order, ABTDOAgent agent) {

		System.err.println("order before for agent " + agent.getId() + ": " + order);
		System.err.flush();
		
		int startIndex = -1;
		int endIndex = order.getSize() - 1;
		
		for (int i = 0; i < order.getSize(); i++)
			if (order.getAgent(i) == agent.getId())
				startIndex = i;
		
		if (startIndex != endIndex){
			
			order.incCounter(startIndex);
			
			startIndex++;
		
			for (int i = startIndex; i < endIndex; i++)
				for (int j = i + 1; j <= endIndex; j++)
					if (Math.random() > 0.5)
						order.replace(i, j);
			
			for (int i = startIndex; i <= endIndex; i++)
				order.resetCounter(i);
		}
		
		System.err.println("order after for agent " + agent.getId() + ": " + order);
		System.err.flush();
	}
}
