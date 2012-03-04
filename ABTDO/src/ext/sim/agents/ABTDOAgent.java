package ext.sim.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.ds.ImmutableSet;

@Algorithm(name = "ABTDO", useIdleDetector = true)
public class ABTDOAgent extends SimpleAgent {

	private Assignment agent_view = null;
	private Integer current_value = null;
	private Map<Integer, Vector<Assignment>> nogoodsPerRemovedValue = null;
	private Set<Integer> myAllNeighbors = null;

	private Order current_order = null;
	private Heuristic heuristic = null;

	@Override
	public void start() {

		assignFirstVariable();

		agent_view = new Assignment();

		nogoodsPerRemovedValue = new HashMap<Integer, Vector<Assignment>>();

		initializeNeighbors();

		current_order = new Order(getNumberOfVariables());

		heuristic = new RandomHeuristic();

		// KICK START THE ALGORITHM..
		send("OK", current_value).toAll(myAllNeighbors); // TODO: to all..

		print(getId() + " sends OK: to all his neighbors with value "
				+ current_value + " from method 'start'");
	}

	private void assignFirstVariable() {

		current_value = getDomainSize() + 1;

		for (Integer d : getDomain())
			if (d < current_value)
				current_value = d;
	}

	private void initializeNeighbors() {

		myAllNeighbors = new HashSet<Integer>();

		for (Integer n : getNeighbors())
			myAllNeighbors.add(n);
	}

	@WhenReceived("OK")
	public void handleOK(int value) {

		print(getId() + " got OK: from " + getCurrentMessage().getSender()
				+ " with value " + value);

		int sender = getCurrentMessage().getSender();

		agent_view.assign(sender, value);
		removeNonConsistentNoGoods(sender, value);
		checkAgentView();
	}

	private void removeNonConsistentNoGoods(int var, int val) {

		for (Integer key : nogoodsPerRemovedValue.keySet()) {

			Vector<Assignment> tNogoods = nogoodsPerRemovedValue.get(key);

			Vector<Assignment> toRemove = new Vector<Assignment>();

			for (Assignment tNogood : tNogoods)
				if (tNogood.isAssigned(var)
						&& tNogood.getAssignment(var) != val)
					toRemove.add(tNogood);

			tNogoods.remove(toRemove);

			if (tNogoods.isEmpty())
				nogoodsPerRemovedValue.remove(key);
		}
	}

	@WhenReceived("ORDER")
	public void handleORDER(Order received_order) {

		print(getId() + " got ORDER: from " + getCurrentMessage().getSender()
				+ " with order " + received_order);

		if (received_order.compareTo(current_order) > 0) {

			print(getId() + " says that the received_order: " + received_order
					+ " is more up to date than the current_order: "
					+ current_order);

			current_order = received_order;

			// TODO: what parameters?..
			removeNonConsistentNoGoods(getId(), current_value);

			checkAgentView();
		}
	}

	@WhenReceived("NOGOOD")
	public void handleNOGOOD(Assignment noGood) {

		print(getId() + " got NOGOOD: from " + getCurrentMessage().getSender()
				+ " with noGood " + noGood);

		int lowestAgent = getTheLowestPriorityAgentFromNoGood(noGood, -1);

		if (getId() != lowestAgent) {

			send("NOGOOD", noGood).to(lowestAgent);

			print(getId()
					+ " sends NOGOOD: to "
					+ lowestAgent
					+ " because he is lower than me and appearing in the received nogood, from method 'handleNOGOOD'");

			send("OK", current_value).to(getCurrentMessage().getSender());

			print(getId() + " sends OK: to " + getCurrentMessage().getSender()
					+ " with value " + current_value
					+ " from method 'handleNOGOOD'");
		} else {

			if (isNogoodConsistentWithAgentView(noGood)
					&& noGood.getAssignment(getId()) == current_value) {

				storeNogood(noGood);

				addNewNeighborsFromNogood(noGood);
				checkAgentView();
			} else {

				send("OK", current_value).to(getCurrentMessage().getSender());

				print(getId() + " sends OK: to "
						+ getCurrentMessage().getSender() + " with value "
						+ current_value + " from method 'handleNOGOOD'");
			}
		}
	}

	// 
	private int getTheLowestPriorityAgentFromNoGood(Assignment noGood, int except) {

		// lowest priority = highest position

		int minAgent = -1;

		ImmutableSet<Integer> nogoodVariables = noGood.assignedVariables();

		for (int agent : nogoodVariables)
			if (agent != except && current_order.getPosition(agent) > minAgent)
				minAgent = agent;

		return minAgent;
	}

	private boolean isNogoodConsistentWithAgentView(Assignment noGood) {

		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();
		ImmutableSet<Integer> agentViewVariables = agent_view
				.assignedVariables();

		for (Integer v : noGoodVariables) {

			if (!agentViewVariables.contains(v))
				continue;

			else if (noGood.getAssignment(v.intValue()) != agent_view
					.getAssignment(v.intValue()))
				return false;
		}

		return true;
	}

	private void storeNogood(Assignment noGood) {

		Vector<Assignment> x = nogoodsPerRemovedValue.get(current_value);

		if (null == x) {

			Vector<Assignment> y = new Vector<Assignment>();
			y.add(noGood);
			nogoodsPerRemovedValue.put(current_value, y);
		} else {

			x.add(noGood);
		}
	}

	private void addNewNeighborsFromNogood(Assignment noGood) {

		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();

		for (Integer v : noGoodVariables) {

			if (!myAllNeighbors.contains(v) && (getId() != v)) {

				send("ADD_NEIGHBOR").to(v);

				print(getId() + " sends ADD_NEIGHBOR: to " + v
						+ " from method 'addNewNeighborsFromNogood'");

				agent_view.assign(v, noGood.getAssignment(v).intValue());
			}
		}
	}

	private void checkAgentView() {

		if (!isThisValueConsistentWithAllHigherPriorityAssignmentsInAgentView(
				current_value, getHighestPriorityNeighbors())) {

			int d = getValueFromDWhichConsistentWithAllHigherPriorityAssignmentsInAgentView();

			if (-1 == d) // There is no value in D which consistent with
							// agent_view..
				backtrack();

			else {

				current_value = d;

				heuristic.changeOrder(current_order, this);

				send("OK", current_value).toAll(myAllNeighbors);

				print(getId() + " sends OK: to all his neighbors with value "
						+ current_value + " from method 'checkAgentView'");

				send("ORDER", current_order)
						.toAll(getLowestPriorityNeighbors());

				print(getId()
						+ " sends ORDER: to all his lower priority neighbors with order: "
						+ current_order + " from method 'checkAgentView'");
			}
		}
	}

	// TODO: is this ok?..
	private int getValueFromDWhichConsistentWithAllHigherPriorityAssignmentsInAgentView() {

		Set<Integer> higherPriorityNeighbors = getHighestPriorityNeighbors();

		for (Integer v : getDomain())
			if (isThisValueConsistentWithAllHigherPriorityAssignmentsInAgentView(
					v, higherPriorityNeighbors))
				return v;

		return -1;
	}

	// TODO: is this ok?..
	private boolean isThisValueConsistentWithAllHigherPriorityAssignmentsInAgentView(
			Integer value, Set<Integer> higherPriorityNeighbors) {

		boolean ans = true;

		for (Integer var : higherPriorityNeighbors) {

			if (!agent_view.isAssigned(var))
				continue;

			if (!getProblem().isConsistent(getId(), value, var,
					agent_view.getAssignment(var))) {

				ans = false;
				break;
			}
		}

		return ans;
	}

	private Set<Integer> getLowestPriorityNeighbors() {

		Set<Integer> lowerPriorityNeighbors = new HashSet<Integer>();

		int myPosition = current_order.getPosition(getId());

		for (int neighbor : myAllNeighbors)
			if (myPosition < current_order.getPosition(neighbor))
				lowerPriorityNeighbors.add(neighbor);

		return lowerPriorityNeighbors;
	}

	private Set<Integer> getHighestPriorityNeighbors() {

		Set<Integer> higherPriorityNeighbors = new HashSet<Integer>();

		int myPosition = current_order.getPosition(getId());

		for (int neighbor : myAllNeighbors)
			if (myPosition > current_order.getPosition(neighbor))
				higherPriorityNeighbors.add(neighbor);

		return higherPriorityNeighbors;
	}

	private void backtrack() {

		Assignment noGood = resolveInconsistentSubset();

		if (noGood.getNumberOfAssignedVariables() == 0
				|| (isFirstAgent() && (getDomainSize() - 1 == current_value))) {

			print(getId() + " says: NO SOLUTION for problem " + getProblem());

			finishWithNoSolution();
			return;
		}

		int lowestPriorityVar = getTheLowestPriorityAgentFromNoGood(noGood, getId());

		send("NOGOOD", noGood).to(lowestPriorityVar);

		print(getId() + " sends NOGOOD: to " + lowestPriorityVar
				+ " because its value: " + current_value
				+ " from method 'backtrack'");

		agent_view.unassign(lowestPriorityVar);

		removeNogoodsThatContainThisVariable(lowestPriorityVar,
				noGood.getAssignment(lowestPriorityVar));

		checkAgentView();
	}

	private Assignment resolveInconsistentSubset() {

		// TODO WTF??... something which related to DBT??..

		Assignment nogood = new Assignment();

		for (Integer var : agent_view.assignedVariables())
			nogood.assign(var, agent_view.getAssignment(var));

		return nogood;
	}

	private void removeNogoodsThatContainThisVariable(int var, int val) {

		for (Integer key : nogoodsPerRemovedValue.keySet()) {

			Vector<Assignment> tNogoods = nogoodsPerRemovedValue.get(key);
			Vector<Assignment> toRemove = new Vector<Assignment>();

			for (Assignment tNogood : tNogoods)
				if (tNogood.isAssigned(var)
						&& tNogood.getAssignment(var) == val)
					toRemove.add(tNogood);

			tNogoods.remove(toRemove);

			if (tNogoods.isEmpty())
				nogoodsPerRemovedValue.remove(key);
		}
	}

	@WhenReceived("ADD_NEIGHBOR")
	public void handleADDNEIGHBOR() {

		print(getId() + " got ADD_NEIGHBOR: from "
				+ getCurrentMessage().getSender());

		myAllNeighbors.add(getCurrentMessage().getSender());

		send("OK", current_value).to(getCurrentMessage().getSender());

		print(getId() + " sends OK: to " + getCurrentMessage().getSender()
				+ " with value " + current_value
				+ " from method 'handleADDNEIGHBOR'");
	}

	@Override
	public void onIdleDetected() {
		finish(current_value);
	}

	private void print(String string) {
		 System.err.println(string);
		 System.err.flush();
	}
}
