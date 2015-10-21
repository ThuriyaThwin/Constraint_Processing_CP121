package ext.sim.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name = "ABT", useIdleDetector = true)
public class ABTAgent extends SimpleAgent {

	// http://azapi-test.googlecode.com/svn/trunk/bin/documentation/javadoc/index.html

	private Assignment agent_view = null;
	private Integer current_value = null;
	private Map<Integer, Vector<Assignment>> nogoodsPerRemovedValue = null;
	private Set<Integer> myNeighbors = null;
	private int lower_limit = 0;
	@Override
	public void start() {

		assignFirstVariable();
		
		agent_view = new Assignment();

		nogoodsPerRemovedValue = new HashMap<Integer, Vector<Assignment>>();

		initializeNeighbors();

		// KICK START THE ALGORITHM..
		send("OK", current_value).toAll(myNeighbors);

		print(getId() + " sends OK: to all his neighbors with value "
				+ current_value + " from method 'start'");
	}

	private void initializeNeighbors() {

		myNeighbors = new HashSet<Integer>();

		for (Integer n : getNeighbors())
			if (n > getId())
				myNeighbors.add(n);
	}

	private void assignFirstVariable() {

		current_value = getDomainSize() + 1;
		
		for (Integer d : getDomain())
			if (d < current_value)
				current_value = d;
		this.lower_limit = current_value + 1;
	}

	@WhenReceived("OK")
	public void handleOK(int value) {

		print(getId() + " got OK: from " + getCurrentMessage().getSender()
				+ " with value " + value);

		int sender = getCurrentMessage().getSender();

		agent_view.assign(sender, value);
		removeNonConsistentNoGoods(sender, value);
		checkAgentView(false);
	}

	@WhenReceived("NOGOOD")
	public void handleNOGOOD(Assignment noGood) {

		print(getId() + " got NOGOOD: from " + getCurrentMessage().getSender()
				+ " with noGood " + noGood);

		int old_value = current_value;

		if (isNogoodConsistentWithAgentView(noGood)
				&& noGood.getAssignment(getId()) == current_value) {

			storeNogood(noGood);

			addNewNeighborsFromNogood(noGood);
			checkAgentView(false);
		}
		//TODO:Bug here? when  i recieve NoGood should i switch values for sure?
		if (old_value == current_value) {
			send("OK", current_value).to(getCurrentMessage().getSender());

			print(getId() + " sends OK: to " + getCurrentMessage().getSender()
					+ " with value " + current_value
					+ " from method 'handleNOGOOD'");
		}
	}

	private void storeNogood(Assignment noGood) {
		
		Vector<Assignment> x = nogoodsPerRemovedValue.get(current_value);

		int whereToput = noGood.getAssignment(getId());
		
		if (null == x) {

			Vector<Assignment> y = new Vector<Assignment>();
			y.add(noGood);
			nogoodsPerRemovedValue.put(whereToput, y);
		} else {
			//TODO:How did this happen?But should we support this?repeated NOGoods?
			if(!checkIfNoGoodIsStored(noGood)){
				x.add(noGood);
			}
		}
	}
	
	private boolean checkIfNoGoodIsStored(Assignment noGood){
		
		boolean tAns = false;
		
		Vector<Assignment> x = nogoodsPerRemovedValue.get(current_value);
		//Set<Integer> tAssignedVars = noGood.assignedVariables();
		if(x != null){
			for (Assignment assignment : x) {
				if (assignment.equals(noGood)){
					tAns = true;
				}
			}
		}
		return tAns;
		
	}
	
	private void checkAgentView(boolean flip) {

		if (!agent_view.isConsistentWith(getId(), current_value, getProblem())
				|| !isAgentViewNotConsistentWithNoGoods(current_value) || (flip == true)) {

			int d = getValueFromDWhichConsistentWithAgentView();

			if (-1 == d) // There is no value in D which consistent with
							// agent_view..
				backtrack();

			else {

				current_value = d;
				send("OK", current_value).toAll(myNeighbors);

				print(getId() + " sends OK: to all his neighbors with value "
						+ current_value + " from method 'checkAgentView'");
			}
		}
	}

	private void backtrack() {

		Assignment noGood = resolveInconsistentSubset();

		if (noGood.getNumberOfAssignedVariables() == 0
				|| (isFirstAgent() && (getDomainSize() - 1 == current_value))) {

			print(getId() + " says: NO SOLUTION for problem " + getProblem());

			finishWithNoSolution();
			return;
		}

		int lowestPriorityVar = getTheLowestPriorityVarFromNogood(noGood);

		send("NOGOOD", noGood).to(lowestPriorityVar);

		print(getId() + " sends NOGOOD: to " + lowestPriorityVar
				+ " because its value: " + current_value
				+ " from method 'backtrack'");

		agent_view.unassign(lowestPriorityVar);
		HashMap<Integer, Vector<Assignment>> NoGoodsBackUp = removeNogoodsThatContainThisVariable(lowestPriorityVar,
				noGood.getAssignment(lowestPriorityVar));
		//Check if we removed all noGoods with this var, we should switch, even though we are 
		//Consistent
		checkAgentView(true);
	}

	private int getTheLowestPriorityVarFromNogood(Assignment noGood) {
		
		int minVar = -1;

		for (Integer v : noGood.assignedVariables())
			if (v > minVar)
				minVar = v;
		
		return minVar;
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

				print(getId() + " sends ADD_NEIGHBOR: to " + v
						+ " from method 'addNewNeighborsFromNogood'");

				agent_view.assign(v, noGood.getAssignment(v).intValue());
			}
		}
	}

	@WhenReceived("ADD_NEIGHBOR")
	public void handleADDNEIGHBOR() {

		print(getId() + " got ADD_NEIGHBOR: from "
				+ getCurrentMessage().getSender());

		myNeighbors.add(getCurrentMessage().getSender());

		send("OK", current_value).to(getCurrentMessage().getSender());
		
		print(getId() + " sends OK: to " + getCurrentMessage().getSender()
				+ " with value " + current_value
				+ " from method 'handleADDNEIGHBOR'");
	}

	private int getValueFromDWhichConsistentWithAgentView() {

		for (Integer v : getDomain()){
			if ((v >= lower_limit) && agent_view.isConsistentWith(getId(), v, getProblem()))
				if (isAgentViewNotConsistentWithNoGoods(v)){
					this.lower_limit = v.intValue() + 1;//Don't repeat this variable again.
					return v.intValue();
				}
		}
		//We are going to back track,next time start from the start.
		this.lower_limit = 0+0;
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
				} else if ((var == getId()) && noGood.getAssignment(var) == v) {
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

	private HashMap<Integer, Vector<Assignment>> removeNogoodsThatContainThisVariable(int var, int val) {

		Vector<Integer> tRemovedKeys = new Vector<Integer>();
		HashMap<Integer, Vector<Assignment>> tAnsweHashMap = new HashMap<>();
		for (Integer key : nogoodsPerRemovedValue.keySet()) {

			Vector<Assignment> tNogoods = nogoodsPerRemovedValue.get(key);
			Vector<Assignment> toRemove = new Vector<Assignment>();

			for (Assignment tNogood : tNogoods)
				if (tNogood.isAssigned(var)
						&& tNogood.getAssignment(var) == val)
					toRemove.add(tNogood);

			//tNogoods.remove(toRemove);
			tNogoods.removeAll(toRemove);//Fixed Bug here
			tAnsweHashMap.put(key, toRemove);
			if (tNogoods.isEmpty()){
				//nogoodsPerRemovedValue.remove(key);//Concurrent modification
				tRemovedKeys.add(key);
			}
		}
		for (Integer keyToRemove : tRemovedKeys) {
			nogoodsPerRemovedValue.remove(keyToRemove);
		}
		return tAnsweHashMap;
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
