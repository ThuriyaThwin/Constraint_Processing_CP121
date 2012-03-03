package ext.sim.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name = "ABT", useIdleDetector = true)
public class ABTAgent extends SimpleAgent {

	// http://azapi-test.googlecode.com/svn/trunk/bin/documentation/javadoc/index.html

	private Assignment							agent_view				= null;
	private Integer								current_value			= null;
	private Map<Integer, Vector<Assignment>>	nogoodsPerRemovedValue	= null;
	private Set<Integer>						myNeighbors				= null;

	@Override
	public void start() {

		current_value = getDomainSize() + 1;

		for (Integer d : getDomain())
			if (d < current_value)
				current_value = d;

		agent_view = new Assignment();

		nogoodsPerRemovedValue = new HashMap<Integer, Vector<Assignment>>();

		myNeighbors = new HashSet<Integer>();

		for (Integer n : getNeighbors())
			if (n > getId())
				myNeighbors.add(n);

		// KICK START THE ALGORITHM..
		send("OK", current_value).toAll(myNeighbors);
		System.err.println("SEND OK: from " + getId()
				+ " toAllAgentsAfterMe with value " + current_value);
		System.err.flush();
	}

	@WhenReceived("OK")
	public void handleOK(int value) {

		System.err.println(getId() + " got OK: from "
				+ getCurrentMessage().getSender() + " with value " + value);
		System.err.flush();

		int sender = getCurrentMessage().getSender();

		agent_view.assign(sender, value);
		removeNonConsistentNoGoods(sender, value);
		checkAgentView();
	}

	@WhenReceived("NOGOOD")
	public void handleNOGOOD(Assignment noGood) {

		int old_value = current_value;

		if (isNogoodConsistentWithAgentView(noGood)
				&& noGood.getAssignment(getId()) == current_value) {

			Vector<Assignment> x = nogoodsPerRemovedValue.get(current_value);

			if (null == x) {

				Vector<Assignment> y = new Vector<Assignment>();
				y.add(noGood);
				nogoodsPerRemovedValue.put(current_value, y);
			} else {

				x.add(noGood);
			}

			addNewNeighborsFromNogood(noGood);
			checkAgentView();
		}

		if (old_value == current_value) {
			send("OK", current_value).to(getCurrentMessage().getSender());
			System.err.println("SEND OK: from " + getId() + " to "
					+ getCurrentMessage().getSender() + " with value: " + current_value);
			System.err.flush();
		}
	}

	private void checkAgentView() {

		if (!agent_view.isConsistentWith(getId(), current_value, getProblem())
				|| !isAgentViewNotConsistentWithNoGoods(current_value)) {

			int d = getValueFromDWhichConsistentWithAgentView();

			if (-1 == d) // There is no value in D which consistent with
							// agent_view..
				backtrack();

			else {

				current_value = d;
				send("OK", current_value).toAll(myNeighbors);

				System.err.println("SEND OK: from " + getId()
						+ " toAllAgentsAfterMe with value " + current_value);
				System.err.flush();
			}
		}
	}

	private void backtrack() {

		Assignment noGood = resolveInconsistentSubset();

		if (noGood.getNumberOfAssignedVariables() == 0
				|| (isFirstAgent() && (getDomainSize() - 1 == current_value))) {

			System.err.println("SEND NO_SOLUTION: from " + getId()
					+ " toAllAgentsAfterMe");
			System.err.flush();
			finishWithNoSolution();
			return;
		}

		int lowerPriorityVar = -1;

		for (Integer v : noGood.assignedVariables())
			if (v > lowerPriorityVar)
				lowerPriorityVar = v;

		send("NOGOOD", noGood).to(lowerPriorityVar);

		System.err.println("SEND NOGOOD: from " + getId() + " to "
				+ lowerPriorityVar + " because its value: " + noGood.getAssignment(lowerPriorityVar));
		System.err.flush();

		agent_view.unassign(lowerPriorityVar);

		removeNogoodsThatContainThisVariable(lowerPriorityVar,
				noGood.getAssignment(lowerPriorityVar));

		checkAgentView();
	}

	private void removeNonConsistentNoGoods(int sender, int value) {

		for (Integer val : nogoodsPerRemovedValue.keySet()) {

			Vector<Assignment> va = nogoodsPerRemovedValue.get(val);
			
			for (Assignment a : va)
				if (a.isAssigned(sender) && a.getAssignment(sender) != value)
					va.remove(val);

			if (va.isEmpty())
				nogoodsPerRemovedValue.remove(val);
		}
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

	private void addNewNeighborsFromNogood(Assignment noGood) {

		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();

		for (Integer v : noGoodVariables) {

			if (!myNeighbors.contains(v) && (getId() != v)) {

				send("ADD_NEIGHBOR").to(v);

				System.err.println("SEND ADD_NEIGHBOR: from " + getId()
						+ " to " + v);
				System.err.flush();

				agent_view.assign(v, noGood.getAssignment(v).intValue());
			}
		}
	}

	@WhenReceived("ADD_NEIGHBOR")
	public void handleADDNEIGHBOR() {
		myNeighbors.add(getCurrentMessage().getSender());
//		send("OK", current_value).to(getCurrentMessage().getSender());
	}

	private int getValueFromDWhichConsistentWithAgentView() {

		for (Integer v : getDomain())
			if (agent_view.isConsistentWith(getId(), v, getProblem()))
				if (isAgentViewNotConsistentWithNoGoods(v))
					return v.intValue();

		return -1;
	}

	private boolean isAgentViewNotConsistentWithNoGoods(Integer v) {

		boolean tAns = false;

		Vector<Assignment> noGoods = nogoodsPerRemovedValue.get(v);

		if (null == noGoods)
			return true;

		for (Assignment noGood : noGoods) {
			tAns = false;
			for (Integer var : noGood.assignedVariables()) {

				if (agent_view.isAssigned(var)
						&& (agent_view.getAssignment(var) == noGood
								.getAssignment(var))) {
					continue;
				} else if ((var == getId())
						&& noGood.getAssignment(var) == current_value) {
					continue;
				} else {
					tAns = true;
				}
			}
			if (!tAns) {
				break;
			}
		}
		return tAns;
	}

	private Assignment resolveInconsistentSubset() {
		
		// TODO WTF??... something which related to DBT??..

		Assignment nogood = new Assignment();

		for (Integer var : agent_view.assignedVariables())
			nogood.assign(var, agent_view.getAssignment(var));
		
		return nogood;
	}

	private void removeNogoodsThatContainThisVariable(int var, int val) {

		for (Integer value : nogoodsPerRemovedValue.keySet()) {

			Vector<Assignment> va = nogoodsPerRemovedValue.get(value);

			for (Assignment a : va)
				if (a.isAssigned(var) && a.getAssignment(var) != val)
					va.remove(val);

			if (va.isEmpty())
				nogoodsPerRemovedValue.remove(val);
		}
	}

	@Override
	public void onIdleDetected() {
		finish(current_value);
	}
}
