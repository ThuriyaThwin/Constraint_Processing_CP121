package ext.sim.agents;

import bgu.dcr.az.api.Agent;

public interface Heuristic {

	public void changeOrder(Order order, Agent agent);
}
