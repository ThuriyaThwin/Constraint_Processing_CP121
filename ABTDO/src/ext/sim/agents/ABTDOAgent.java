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

		// heuristic = new RandomHeuristic();
		heuristic = new NoChangeHeuristic();

		// KICK START THE ALGORITHM..
		send("OK", current_value).toAll(myAllNeighbors);

		print(getId() + " sends OK: to all his neighbors: " + myAllNeighbors
				+ " with value " + current_value + " from method 'start'");
	}

	private void assignFirstVariable() {

		current_value = getDomainSize() + 1;

		for (Integer d : getDomain())
			if (d < current_value)
				current_value = d;
	}

	private void initializeNeighbors() {

		myAllNeighbors = new HashSet<Integer>(getNeighbors());

		// for (Integer n : getNeighbors())
		// myAllNeighbors.add(n);
	}

	@WhenReceived("OK")
	public void handleOK(int value) {

		print(getId() + " got OK: from " + getCurrentMessage().getSender()
				+ " with value " + value);

		int sender = getCurrentMessage().getSender();

		// //TODO: ?..
		// if (!getHigherPriorityNeighbors().contains(sender))
		// return;

		agent_view.assign(sender, value);

		removeInconsistentNoGoods(sender, value);

		checkAgentView();
	}

	private void removeInconsistentNoGoods(Integer var, Integer val) {

		print(getId() + " before removeNogoodsWithThisVar: var: " + var
				+ " val: " + val + " myNogoodStore: " + nogoodsPerRemovedValue);

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

		print(getId() + " after removeNogoodsWithThisVar: var: " + var
				+ " val: " + val + " myNogoodStore: " + nogoodsPerRemovedValue);
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

		print(getId() + " got NOGOOD: from " + getCurrentMessage().getSender()
				+ " with noGood " + noGood);

		Integer old_value = current_value;

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

				storeNogood(noGood);

				addNewNeighborsFromNogood(noGood);

				checkAgentView();
			}
			// else {
			//
			// send("OK", current_value).to(getCurrentMessage().getSender());
			//
			// print(getId() + " sends OK: to "
			// + getCurrentMessage().getSender() + " with value "
			// + current_value + " from method 'handleNOGOOD'");
			// }

			// TODO: instead the above..
			if (current_value == old_value) {
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

		Vector<Assignment> tVec = nogoodsPerRemovedValue.get(current_value);

		if (null == tVec) {

			tVec = new Vector<Assignment>();
			tVec.add(noGood);
			nogoodsPerRemovedValue.put(current_value, tVec);
		} else
			tVec.add(noGood);
	}

	private void addNewNeighborsFromNogood(Assignment noGood) {

		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();

		for (Integer v : noGoodVariables) {

			if (!myAllNeighbors.contains(v) && (getId() != v)) {

				send("ADD_NEIGHBOR").to(v);

				print(getId() + " sends ADD_NEIGHBOR to " + v
						+ " from method 'addNewNeighborsFromNogood'");

				agent_view.assign(v, noGood.getAssignment(v).intValue());
			}
		}
	}

	private void checkAgentView() {

		if (!isCurrentAssignmentConsistentWithAllHigherPriorityAssignmentsInAgentView()) {

			int d = getValueFromDWhichConsistentWithAllHigherPriorityAssignmentsInAgentView();

			// There is no value in D which consistent with agent_view..
			if (-1 == d)
				backtrack();

			else {

				current_value = d;

				heuristic.changeOrder(current_order, this);

				send("OK", current_value).toAll(myAllNeighbors);
				// TODO: ?...
				// send("OK", current_value).toAll(getLowerPriorityNeighbors());

				print(getId() + " sends OK: to all his neighbors: "
						+ myAllNeighbors + " with value " + current_value
						+ " from method 'checkAgentView'");

				send("ORDER", current_order).toAll(getLowerPriorityNeighbors());
				// TODO: ?...
				// send("ORDER", current_order).toAllAgentsAfterMe();

				print(getId()
						+ " sends ORDER: to all his lower priority neighbors: "
						+ getLowerPriorityNeighbors() + " with order: "
						+ current_order + " from method 'checkAgentView'");
			}
		}
	}

	private boolean isCurrentAssignmentConsistentWithAllHigherPriorityAssignmentsInAgentView() {

		Set<Integer> higherPriorityNeighbors = getHigherPriorityNeighbors();

		for (Integer neighbor : higherPriorityNeighbors) {

			if (!agent_view.isAssigned(neighbor))
				continue;

			if (!getProblem().isConsistent(getId(), current_value, neighbor,
					agent_view.getAssignment(neighbor)))
				return false;
		}

		return (null == nogoodsPerRemovedValue.get(current_value));

		// TODO NIR's.. remove it !!!!...
		// return !isAgentViewIsSuperSetOfOfNoGood();
	}

	// TODO: NIR's.. remove it !!!!...
	private boolean isAgentViewIsSuperSetOfOfNoGood() {

		for (Integer key : nogoodsPerRemovedValue.keySet()) {

			for (Assignment noGood : nogoodsPerRemovedValue.get(key)) {

				if (agent_view.assignedVariables().containsAll(
						noGood.assignedVariables())) {

					boolean flag = true;

					for (Integer var : noGood.assignedVariables())
						if (noGood.getAssignment(var) != agent_view
								.getAssignment(var))
							flag = false;

					if (flag)
						return true;
				}
			}
		}

		return false;
	}

	private int getValueFromDWhichConsistentWithAllHigherPriorityAssignmentsInAgentView() {

		Set<Integer> higherPriorityNeighbors = getHigherPriorityNeighbors();
		Set<Integer> consistentValues = new HashSet<Integer>();

		for (Integer v : getDomain()) {

			//TODO:?...
//			if (v > current_value) {

				boolean isConsistent = true;

				for (Integer neighbor : higherPriorityNeighbors) {

					if (!agent_view.isAssigned(neighbor))
						continue;

					if (!getProblem().isConsistent(getId(), v, neighbor,
							agent_view.getAssignment(neighbor)))
						isConsistent = false;
				}

				if (isConsistent)
					consistentValues.add(v);
//			}
		}

		for (Integer v : consistentValues)
			if (null == nogoodsPerRemovedValue.get(v))
				// TODO: NIR's.. remove it !!!!...
				// if (!isAgentViewIsSuperSetOfOfNoGood())
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

		Assignment noGood = resolveInconsistentSubset();

		// TODO
		int lowestPriorityVar = getTheHighestLowerPriorityAgentFromNoGood(noGood);

		if (-1 == lowestPriorityVar
				|| noGood.getNumberOfAssignedVariables() == 0) {

			print(getId() + " says: NO SOLUTION for the problem");

			if (isFirstAgent())
				finishWithNoSolution();

			return;
		}

		// int lowestPriorityVar =
		// getTheHighestLowerPriorityAgentFromNoGood(noGood);

		//TODO:?..
		current_value = 0;
				
		send("NOGOOD", noGood).to(lowestPriorityVar);

		print(getId() + " sends NOGOOD: to " + lowestPriorityVar
				+ " because its value: "
				+ agent_view.getAssignment(lowestPriorityVar)
				+ " from method 'backtrack'");

		agent_view.unassign(lowestPriorityVar);

		removeNogoodsThatContainThisVariable(lowestPriorityVar,
				noGood.getAssignment(lowestPriorityVar));

		checkAgentView();
	}

	private int getTheHighestLowerPriorityAgentFromNoGood(Assignment noGood) {

		int tAgent = -1;

		// TODO: != or <
		for (int agent : noGood.assignedVariables())
			// if (current_order.getPosition(agent) < current_order
			// .getPosition(getId())
			// && current_order.getPosition(agent) > tAgent)
			if (agent != getId() && current_order.getPosition(agent) > tAgent)
				tAgent = agent;

		return tAgent;
	}

	private Assignment resolveInconsistentSubset() {

		// Assignment nogood = new Assignment();
		//
		// for (Integer var : agent_view.assignedVariables())
		// nogood.assign(var, agent_view.getAssignment(var));
		//
		// return nogood;

		return agent_view.deepCopy();
	}

	private void removeNogoodsThatContainThisVariable(int var, int val) {

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
	}

	// TODO: check again that we really want to do exactly that..
	private void removeInconsistentNogoodsWhenReceivingOrder() {

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
