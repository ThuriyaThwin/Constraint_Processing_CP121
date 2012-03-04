package ext.sim.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;

@Algorithm(name = "ABTDO", useIdleDetector = true)
public class ABTDOAgent extends SimpleAgent {

	// Current order which is an ordered list of pairs.
	// Every pair includes the ID of one of the agents and a counter

	// Each agent can
	// propose a new order for agents that have lower priority, each time it
	// replaces its
	// assignment.

	// The counters attached to each agent ID in the order list form a
	// time-stamp.
	// Initially, all time-stamp counters are set to zero and all agents start
	// with the same
	// Current Order.

	// Each agent Ai that proposes a new order, changes the order of the
	// pairs in its own ordered list and updates the counters as follows:
	// 1. The counters of agents with higher priority than Ai, according to the
	// Current order, are not changed.
	// 2. The counter of Ai is incremented by one.
	// 3. The counters of agents with lower priority than Ai in the Current
	// order are set
	// to zero.

	private Assignment agent_view = null;
	private Integer current_value = null;
	private Map<Integer, Vector<Assignment>> nogoodsPerRemovedValue = null;
	private Set<Integer> myAllNeighbors = null;
	private Set<Integer> myLowerPriorityNeighbors = null;
	private Order current_order = null;

	@Override
	public void start() {

		assignFirstVariable();

		agent_view = new Assignment();

		nogoodsPerRemovedValue = new HashMap<Integer, Vector<Assignment>>();

		initializeNeighbors();

		current_order = new Order(getNumberOfVariables());

		// KICK START THE ALGORITHM..
		send("OK", current_value).toAll(myNeighbors); // TODO: to all?..

		print(getId() + " sends OK: to all his neighbors with value "
				+ current_value + " from method 'start'");
	}

	private void initializeNeighbors() {

		myAllNeighbors = new HashSet<Integer>();
		myLowerPriorityNeighbors = new HashSet<Integer>();

		for (Integer n : getNeighbors()) {

			myAllNeighbors.add(n);

			if (n > getId())
				myLowerPriorityNeighbors.add(n);
		}
	}

	private void assignFirstVariable() {

		current_value = getDomainSize() + 1;

		for (Integer d : getDomain())
			if (d < current_value)
				current_value = d;
	}

	@Override
	public void onIdleDetected() {
		finish(current_value);
	}

	private void print(String string) {
		// System.err.println(string);
		// System.err.flush();
	}
}
