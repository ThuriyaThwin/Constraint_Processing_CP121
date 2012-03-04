package ext.sim.agents;

import bgu.dcr.az.api.Agent;

public class RandomHeuristic implements Heuristic {

	public void changeOrder(Order order, Agent agent){
		
		int startIndex = agent.getId();
		int endIndex = order.getSize()-1;
	}
}
