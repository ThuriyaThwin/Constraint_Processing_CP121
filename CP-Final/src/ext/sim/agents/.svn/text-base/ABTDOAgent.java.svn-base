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

	private static boolean USE_RANDOM_HEURISTIC = false;
	private static boolean USE_NOGOOD_HEURISTIC = true;

	private Assignment agent_view = null;
	private Integer current_value = null;
	private Map<Integer, Vector<Assignment>> nogoodsPerRemovedValue = null;
	private Set<Integer> myAllNeighbors = null;

	private Order current_order = null;
	private Heuristic heuristic = null;

	@Override
	public void start() {

		current_value = 0;

		agent_view = new Assignment();

		nogoodsPerRemovedValue = new HashMap<Integer, Vector<Assignment>>();

		myAllNeighbors = new HashSet<Integer>(getNeighbors());

		current_order = new Order(getNumberOfVariables());

		if (USE_RANDOM_HEURISTIC)
			heuristic = new RandomHeuristic();

		else if (USE_NOGOOD_HEURISTIC)
			heuristic = new NogoodTriggeredHeuristic(getId());

		// KICK START THE ALGORITHM..
		send("OK", current_value).toAll(myAllNeighbors);

		print(getId() + " sends OK: to all his neighbors: " + myAllNeighbors
				+ " with value " + current_value + " from method 'start'");
	}

	@WhenReceived("OK")
	public void handleOK(int value) {

		print(getId() + " handleOK: got OK from: "
				+ getCurrentMessage().getSender() + " with value: " + value
				+ " and agent_view before: " + agent_view);

		int sender = getCurrentMessage().getSender();

		agent_view.assign(sender, value);

		print(getId() + " handleOK: added var: "
				+ getCurrentMessage().getSender() + " and value: " + value
				+ " to agent_view: " + agent_view);

		removeInconsistentNoGoods(sender, value);

		checkAgentView();
	}

	private void removeInconsistentNoGoods(Integer var, Integer val) {

		print(getId() + " before removeInconsistentNoGoods: var: " + var
				+ " val: " + val + " myNogoodStore: " + nogoodsPerRemovedValue
				+ " agent_view: " + agent_view + " current_value: "
				+ current_value);

		Vector<Integer> keysToRemove = new Vector<Integer>();

		for (Integer key : nogoodsPerRemovedValue.keySet()) {

			Vector<Integer> nogoodsToRemove = new Vector<Integer>();
			Vector<Assignment> nogoodsOfThisKey = nogoodsPerRemovedValue
					.get(key);

			for (int i = 0; i < nogoodsOfThisKey.size(); i++)
				if (nogoodsOfThisKey.get(i).isAssigned(var)
						&& (nogoodsOfThisKey.get(i).getAssignment(var) != val))
					nogoodsToRemove.add(0, i);

			for (Integer indexToRemove : nogoodsToRemove)
				nogoodsOfThisKey.remove(indexToRemove.intValue());

			if (nogoodsOfThisKey.isEmpty())
				keysToRemove.add(key);
		}

		for (Integer key : keysToRemove)
			nogoodsPerRemovedValue.remove(key);

		print(getId() + " after removeInconsistentNoGoods: var: " + var
				+ " val: " + val + " myNogoodStore: " + nogoodsPerRemovedValue
				+ " agent_view: " + agent_view + " current_value: "
				+ current_value);
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

			removeInconsistentNogoodsWhenReceivingOrder();

			checkAgentView();
		}
	}

	@WhenReceived("NOGOOD")
	public void handleNOGOOD(Assignment noGood) {

		print(getId() + " handleNOGOOD: got NOGOOD from: "
				+ getCurrentMessage().getSender() + " with noGood: " + noGood
				+ " and current_value: " + current_value);

		Integer old_value = current_value;
		boolean sent = false;

		int lowestAgent = getTheLowestPriorityAgentFromNoGood(noGood);

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

			if (isNogoodConsistentWithAgentViewAndCurrentAssignment(noGood)) {

				print(getId()
						+ " handleNOGOOD: Nogood is Consistent With Agent View And Current Assignment "
						+ " noGood: " + noGood + " current_value: "
						+ current_value + " agent_view: " + agent_view);

				storeNogood(noGood);

				addNewNeighborsFromNogood(noGood);

				checkAgentView();
				
				if (USE_NOGOOD_HEURISTIC) {

					heuristic.changeOrder(current_order, this);

					send("ORDER", current_order).toAll(getLowerPriorityNeighbors());

					print(getId()
							+ " sends ORDER: to all his lower priority neighbors: "
							+ getLowerPriorityNeighbors() + " with order: "
							+ current_order + " from method 'handleNOGOOD'");
				}
			}
			else {

				sent = true;

				send("OK", current_value).to(getCurrentMessage().getSender());

				print(getId() + " sends OK: to "
						+ getCurrentMessage().getSender() + " with value "
						+ current_value + " from method 'handleNOGOOD'");
			}

			if (current_value == old_value & !sent) {

				print(getId() + " handleNOGOOD: current_value == old_value "
						+ " noGood: " + noGood + " current_value: "
						+ current_value + " old_value: " + old_value
						+ " agent_view: " + agent_view);

				send("OK", current_value).to(getCurrentMessage().getSender());

				print(getId() + " sends OK: to "
						+ getCurrentMessage().getSender() + " with value "
						+ current_value + " from method 'handleNOGOOD'");
			}
		}
	}

	private int getTheLowestPriorityAgentFromNoGood(Assignment noGood) {

		// lowest priority = highest position

		int minAgent = -1;

		ImmutableSet<Integer> nogoodVariables = noGood.assignedVariables();

		for (int agent : nogoodVariables)
			if (current_order.getPosition(agent) > minAgent)
				minAgent = agent;

		print(getId() + " the lowest priority agent in nogood: " + noGood
				+ " is: " + minAgent);

		return minAgent;
	}

	private boolean isNogoodConsistentWithAgentViewAndCurrentAssignment(
			Assignment noGood) {

		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();

		for (Integer v : noGoodVariables) {

			if ((getId() == v) && noGood.getAssignment(v) != current_value)
				return false;

			else if (!agent_view.isAssigned(v))
				continue;

			else if (noGood.getAssignment(v) != agent_view.getAssignment(v))
				return false;
		}

		return true;
	}

	private void storeNogood(Assignment noGood) {

		print(getId() + " before storing nogood: " + noGood
				+ ", current_value: " + current_value + " in: "
				+ nogoodsPerRemovedValue);

		Vector<Assignment> tVec = nogoodsPerRemovedValue.get(current_value);

		if (null == tVec) {

			tVec = new Vector<Assignment>();
			tVec.add(noGood);
			nogoodsPerRemovedValue.put(current_value, tVec);
		} else
			tVec.add(noGood);

		print(getId() + " after storing nogood: " + noGood
				+ ", current_value: " + current_value + " in: "
				+ nogoodsPerRemovedValue);
	}

	private void addNewNeighborsFromNogood(Assignment noGood) {

		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();

		for (Integer v : noGoodVariables) {

			if (!myAllNeighbors.contains(v) && (getId() != v)) {

				send("ADD_NEIGHBOR").to(v);

				print(getId() + " sends ADD_NEIGHBOR to " + v
						+ " from method 'addNewNeighborsFromNogood'");

				print(getId() + " addNewNeighborsFromNogood: got OK from: "
						+ getCurrentMessage().getSender() + " with value: "
						+ noGood.getAssignment(v) + " and agent_view before: "
						+ agent_view);

				agent_view.assign(v, noGood.getAssignment(v).intValue());

				print(getId() + " addNewNeighborsFromNogood: added var: "
						+ getCurrentMessage().getSender() + " and value: "
						+ noGood.getAssignment(v) + " to agent_view: "
						+ agent_view);
			}
		}
	}

	private void checkAgentView() {

		print(getId() + " checkAgentView: agent_view: " + agent_view
				+ " current_value: " + current_value
				+ " nogoodsPerRemovedValue: " + nogoodsPerRemovedValue);

		if (!isCurrentAssignmentConsistentWithAllHigherPriorityAssignmentsInAgentView()) {

			print(getId()
					+ " checkAgentView: current assignment is not consistent with All Higher Priority Assignments In AgentView "
					+ ", agent_view: " + agent_view + " current_value: "
					+ current_value + " nogoodsPerRemovedValue: "
					+ nogoodsPerRemovedValue);

			int d = getValueFromDWhichConsistentWithAllHigherPriorityAssignmentsInAgentView();

			print(getId() + " checkAgentView: chose d = " + d
					+ ", agent_view: " + agent_view + " current_value: "
					+ current_value + " nogoodsPerRemovedValue: "
					+ nogoodsPerRemovedValue);

			// There is no value in D which consistent with agent_view..
			if (-1 == d)
				backtrack();

			else {

				current_value = d;

				if (USE_RANDOM_HEURISTIC) {

					heuristic.changeOrder(current_order, this);

					send("ORDER", current_order).toAll(
							getLowerPriorityNeighbors());

					print(getId()
							+ " sends ORDER: to all his lower priority neighbors: "
							+ getLowerPriorityNeighbors() + " with order: "
							+ current_order + " from method 'checkAgentView'");
				}

				send("OK", current_value).toAll(myAllNeighbors);

				print(getId() + " sends OK: to all his neighbors: "
						+ myAllNeighbors + " with value " + current_value
						+ " from method 'checkAgentView'");

			}
		} else {
			print(getId()
					+ " checkAgentView: current assignment is consistent with All Higher Priority Assignments In AgentView "
					+ ", agent_view: " + agent_view + " current_value: "
					+ current_value + " nogoodsPerRemovedValue: "
					+ nogoodsPerRemovedValue);
		}
	}

	private boolean isCurrentAssignmentConsistentWithAllHigherPriorityAssignmentsInAgentView() {

		Set<Integer> higherPriorityNeighbors = getHigherPriorityNeighbors();

		Assignment tAss = new Assignment();

		for (Integer neighbor : higherPriorityNeighbors) {

			if (!agent_view.isAssigned(neighbor))
				continue;

			tAss.assign(neighbor, agent_view.getAssignment(neighbor));
		}

		if (!tAss.isConsistentWith(getId(), current_value, getProblem()))
			return false;

		return (null == nogoodsPerRemovedValue.get(current_value));
	}

	private int getValueFromDWhichConsistentWithAllHigherPriorityAssignmentsInAgentView() {

		Set<Integer> higherPriorityNeighbors = getHigherPriorityNeighbors();
		Set<Integer> consistentValues = new HashSet<Integer>();

		for (Integer v : getDomain()) {

			if (v != current_value) {

				boolean isConsistent = true;

				Assignment tAss = new Assignment();

				for (Integer neighbor : higherPriorityNeighbors) {

					if (!agent_view.isAssigned(neighbor))
						continue;

					tAss.assign(neighbor, agent_view.getAssignment(neighbor));
				}

				isConsistent = tAss.isConsistentWith(getId(), v, getProblem());

				if (isConsistent)
					consistentValues.add(v);
			}
		}

		for (Integer v : consistentValues)
			if (null == nogoodsPerRemovedValue.get(v))
				return v;

		return -1;
	}

	private Set<Integer> getLowerPriorityNeighbors() {

		Set<Integer> lowerPriorityNeighbors = new HashSet<Integer>();

		int myPosition = current_order.getPosition(getId());

		for (int neighbor : myAllNeighbors)
			if (myPosition < current_order.getPosition(neighbor))
				lowerPriorityNeighbors.add(neighbor);

		return lowerPriorityNeighbors;
	}

	private Set<Integer> getHigherPriorityNeighbors() {

		Set<Integer> higherPriorityNeighbors = new HashSet<Integer>();

		int myPosition = current_order.getPosition(getId());

		for (int neighbor : myAllNeighbors)
			if (myPosition > current_order.getPosition(neighbor))
				higherPriorityNeighbors.add(neighbor);

		return higherPriorityNeighbors;
	}

	private void backtrack() {

		print(getId() + " backtrack: agent_view: " + agent_view
				+ " current_value: " + current_value
				+ " nogoodsPerRemovedValue: " + nogoodsPerRemovedValue);

		Assignment noGood = resolveInconsistentSubset();

		int lowestPriorityVar = getTheHighestLowerPriorityAgentFromNoGood(noGood);

		if (-1 == lowestPriorityVar
				|| noGood.getNumberOfAssignedVariables() == 0) {

			print(getId() + " says: NO SOLUTION for the problem");

			finishWithNoSolution();

			return;
		}

		send("NOGOOD", noGood).to(lowestPriorityVar);

		print(getId() + " sends NOGOOD: to " + lowestPriorityVar
				+ " because its value: "
				+ agent_view.getAssignment(lowestPriorityVar)
				+ " from method 'backtrack'");

		print(getId() + " backtrack: before removing lowestPriorityVar: "
				+ lowestPriorityVar + " from agent_view: " + agent_view);

		agent_view.unassign(lowestPriorityVar);

		print(getId() + " backtrack: after removing lowestPriorityVar: "
				+ lowestPriorityVar + " from agent_view: " + agent_view);

		removeNogoodsThatContainThisVariable(lowestPriorityVar,
				noGood.getAssignment(lowestPriorityVar));

		checkAgentView();
	}

	private int getTheHighestLowerPriorityAgentFromNoGood(Assignment noGood) {

		int tAgent = -1;

		for (int agent : noGood.assignedVariables())
			if (agent != getId() && tAgent < current_order.getPosition(agent))
				tAgent = agent;

		print(getId() + " The Highest Lower Priority Agent in NoGood: "
				+ noGood + " and order: " + current_order + " is: " + tAgent);

		return tAgent;
	}

	private Assignment resolveInconsistentSubset() {

		Assignment nogood = new Assignment();

		for (Integer var : agent_view.assignedVariables())
			nogood.assign(var, agent_view.getAssignment(var));

		return nogood;

		// return agent_view.deepCopy();
	}

	private void removeNogoodsThatContainThisVariable(int var, int val) {

		print(getId() + " removeNogoodsThatContainThisVariable: var: " + var
				+ " val: " + val + " nogoods before: " + nogoodsPerRemovedValue);

		Vector<Integer> keysToRemove = new Vector<Integer>();

		for (Integer key : nogoodsPerRemovedValue.keySet()) {

			Vector<Integer> nogoodsToRemove = new Vector<Integer>();
			Vector<Assignment> nogoodsOfThisKey = nogoodsPerRemovedValue
					.get(key);

			for (int i = 0; i < nogoodsOfThisKey.size(); i++)
				if (nogoodsOfThisKey.get(i).isAssigned(var)
						&& (nogoodsOfThisKey.get(i).getAssignment(var) == val))
					nogoodsToRemove.add(0, i);

			for (Integer indexToRemove : nogoodsToRemove)
				nogoodsOfThisKey.remove(indexToRemove.intValue());

			if (nogoodsOfThisKey.isEmpty())
				keysToRemove.add(key);
		}

		for (Integer key : keysToRemove)
			nogoodsPerRemovedValue.remove(key);

		print(getId() + " removeNogoodsThatContainThisVariable: var: " + var
				+ " val: " + val + " nogoods after: " + nogoodsPerRemovedValue);
	}

	private void removeInconsistentNogoodsWhenReceivingOrder() {

		print(getId() + " removeNogoodsThatContainThisVariable: order: "
				+ current_order + " nogoods before: " + nogoodsPerRemovedValue);

		Vector<Integer> keysToRemove = new Vector<Integer>();

		for (Integer var : getLowerPriorityNeighbors()) {

			for (Integer key : nogoodsPerRemovedValue.keySet()) {

				Vector<Integer> nogoodsToRemove = new Vector<Integer>();
				Vector<Assignment> nogoodsOfThisKey = nogoodsPerRemovedValue
						.get(key);

				for (int i = 0; i < nogoodsOfThisKey.size(); i++)
					if (nogoodsOfThisKey.get(i).isAssigned(var))
						nogoodsToRemove.add(0, i);

				for (Integer indexToRemove : nogoodsToRemove)
					nogoodsOfThisKey.remove(indexToRemove.intValue());

				if (nogoodsOfThisKey.isEmpty())
					keysToRemove.add(key);
			}
		}

		for (Integer key : keysToRemove)
			nogoodsPerRemovedValue.remove(key);

		print(getId() + " removeNogoodsThatContainThisVariable: order: "
				+ current_order + " nogoods after: " + nogoodsPerRemovedValue);
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
