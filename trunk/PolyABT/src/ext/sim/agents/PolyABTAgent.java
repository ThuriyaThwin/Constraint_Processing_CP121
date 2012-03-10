package ext.sim.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name="PolyABT", useIdleDetector=true)
public class PolyABTAgent extends SimpleAgent {

	protected Assignment						myAgentView				= null;
	protected Integer							myValue					= null;

	protected SortedSet<Integer>				lowerPriorityNeighbors	= null;	// Gamma+
	protected SortedSet<Integer>				higherPriorityNeighbors	= null; // Gamma-

	protected Map<Integer,Vector<Assignment>>	myNogoodStore			= null;

	@Override
	public void start() {

		print(getId() + " starts");

		myAgentView = new Assignment();

		Set<Integer> tDomain = getDomain();
		myValue = (Integer) tDomain.toArray()[0];//Should be something.

		computeNeighbors();

		myNogoodStore = new HashMap<Integer, Vector<Assignment>>();

		send("OK", myValue).toAll(lowerPriorityNeighbors);

		checkAgentView();
	}

	protected void computeNeighbors() {

		lowerPriorityNeighbors = new TreeSet<Integer>();
		higherPriorityNeighbors = new TreeSet<Integer>();

		for (Integer neighbor : getNeighbors()){

			if (getId() < neighbor)
				lowerPriorityNeighbors.add(neighbor);

			else if (neighbor < getId())
				higherPriorityNeighbors.add(neighbor);
		}

		print(getId() + " computeNeighbors: lowerPriorityNeighbors: " + lowerPriorityNeighbors);
		print(getId() + " computeNeighbors: higherPriorityNeighbors: " + higherPriorityNeighbors);
	}


	protected void checkAgentView() {

		print(getId() + " entered checkAgentView");

		if ((-1 == myValue) || !myAgentView.isConsistentWith(getId(), myValue, getProblem())){

			myValue = chooseValue();

			print(getId() + " checkAgentView: myValue changed to: " + myValue);
			
			if (-1 != myValue)
				send("OK", myValue).toAll(lowerPriorityNeighbors);

			else
				backtrack();
		}
	}

	private boolean stopForId(int id){
		return (getId() == id);
	}
	
	@WhenReceived("OK")
	public void ProcessInfo(Integer value){

		print(getId() + " ProcessInfo (got OK) from: " + getCurrentMessage().getSender() + " value: " + value);

		Assignment tAss = new Assignment();

		tAss.assign(getCurrentMessage().getSender(), value);

		updateAgentView(tAss);
		checkAgentView();
	}

	@WhenReceived("NOGOOD")
	public void resolveConflict(Assignment nogood){
		
		print(getId() + " resolveConflict (got NOGOOD) from: " + getCurrentMessage().getSender() + " nogood: " + nogood);

		SortedSet<Integer> self = new TreeSet<Integer>();
		SortedSet<Integer> agents = new TreeSet<Integer>(higherPriorityNeighbors);

		self.add(getId());
		agents.add(getId());

		if (coherent(nogood, agents)){

			print(getId() + " resolveConflict: nogood: " + nogood + " and agents: " + agents + " are not coherent");
			
			checkAddLink(nogood);

			addNogoodToMyNogoodStore(nogood, myValue);

			myValue = -1;

			print(getId() + " resolveConflict: myValue changed to: " + myValue);
			
			checkAgentView();
		}
		else{ //if (coherent(nogood, self))
			print(getId() + " resolveConflict: nogood: " + nogood + " and agents: " + agents + " are coherent so I keep my value: " + myValue);
			send("OK", myValue).to(getCurrentMessage().getSender());
		}
	}

	protected void addNogoodToMyNogoodStore(Assignment nogood, int value) {

		print(getId() + " addNogoodToMyNogoodStore: nogood: " + nogood + " value: " + value);

		Vector<Assignment> tVec = myNogoodStore.get(value);

		if (null == tVec)
			tVec = new Vector<Assignment>();
		
		//TODO:Make sure we delete non relevant no Goods
		tVec.add(nogood);

		print(getId() + " addNogoodToMyNogoodStore: myNogoodStore: " + myNogoodStore + " (after the addition of " + nogood);
		
		myNogoodStore.put(value, tVec);
	}

	private void backtrack() {

		print(getId() + " entering backtrack");

		Assignment newNogood = solve();

		print(getId() + " backtrack: solved newNogod: " + newNogood);
		
		if(newNogood.getNumberOfAssignedVariables() == 0){

			finishWithNoSolution();
			return;
		}
		else{

			Integer lowPriorityAgent = getRhsFromNoGood(newNogood);

			print(getId() + " backtrack: lowPriorityAgent in newNogod: " + newNogood + " is: " + lowPriorityAgent);
			
			send("NOGOOD",newNogood).to(lowPriorityAgent);

			Assignment tAss = new Assignment();
			tAss.assign(lowPriorityAgent, -1);

			//TODO: is it necessary?..
			removeNogoodsWithThisVar(lowPriorityAgent, newNogood.getAssignment(lowPriorityAgent));

			updateAgentView(tAss);
			checkAgentView();
		}
	}

	private void removeNogoodsWithThisVar(Integer var, Integer val) {

		print(getId() + " before removeNogoodsWithThisVar: var: " + var + " val: " + val + " myNogoodStore: " + myNogoodStore);
		
		Vector<Integer> keysToRemove = new Vector<Integer>();

		for(Integer key : myNogoodStore.keySet()){

			Vector<Integer> nogoodsToRemove = new Vector<Integer>();
			Vector<Assignment> nogoodsOfThisKey = myNogoodStore.get(key);

			for (int i = 0; i < nogoodsOfThisKey.size(); i++)
				if (nogoodsOfThisKey.get(i).isAssigned(var) && (nogoodsOfThisKey.get(i).getAssignment(var) == val))
					nogoodsToRemove.add(0, i);

			for (Integer indexToRemove : nogoodsToRemove)
				nogoodsOfThisKey.remove(indexToRemove.intValue());

			if (nogoodsOfThisKey.isEmpty())
				keysToRemove.add(key);
		}

		for (Integer key : keysToRemove)
			myNogoodStore.remove(key);
		
		print(getId() + " after removeNogoodsWithThisVar: var: " + var + " val: " + val + " myNogoodStore: " + myNogoodStore);
	}

	private Assignment solve() {

		print(getId() + " solve");

		//TODO: improve it..
		//TODO:Make sure we are not missing out on agents.DBT style.
		Assignment nogood = new Assignment();

		for (Integer var : myAgentView.assignedVariables())
			nogood.assign(var, myAgentView.getAssignment(var));

		return nogood;
	}

	private Integer getRhsFromNoGood(Assignment assignment) {

		print(getId() + " entering getLhsFromNoGood: assignment: " + assignment);

		Integer ans = -1;

		for (Integer var : assignment.assignedVariables())
			if (ans < var)
				ans = var;

		print(getId() + " exiting getLhsFromNoGood: assignment: " + assignment + " and RHS: " + ans);
		
		return ans;
	}

	private Integer chooseValue() {

		print(getId() + " entering chooseValue");

		SortedSet<Integer> currentDomain = eliminateValuesFromTheDomain();

		print(getId() + " entering chooseValue : currentDomain: " + currentDomain);
		
		for (Integer v : currentDomain){

			boolean consistent = true;

			for (Integer neighbor : higherPriorityNeighbors){

				if (!myAgentView.isAssigned(neighbor))
					continue;

				if (!getProblem().isConsistent(getId(), v, neighbor, myAgentView.getAssignment(neighbor))){

					consistent = false;

					Assignment assignment = new Assignment();

					assignment.assign(neighbor, myAgentView.getAssignment(neighbor));

					addNogoodToMyNogoodStore(assignment, v);
				}
			}

			if (consistent){
				
				print(getId() + " exiting chooseValue : currentDomain: " + currentDomain + " with value: " + v);
				return v;
			}
				
		}

		print(getId() + " exiting chooseValue : currentDomain: " + currentDomain + " with value: -1");
		return -1;
	}

	private SortedSet<Integer> eliminateValuesFromTheDomain() {

		print(getId() + " eliminateValuesFromTheDomain: full domain: " + getDomain() + " myNogoodStore: " + myNogoodStore);

		SortedSet<Integer> currentDomain = new TreeSet<Integer>(getDomain());

		//TODO: we assume that if there is nogood for some value, the nogood is valid.. 
		//TODO: SEARCH Bugs here!!!..

		for (Integer key : myNogoodStore.keySet())
			currentDomain.remove(key);

		return currentDomain;
	}

	protected void updateAgentView(Assignment assignment) {

		print(getId() + " updateAgentView: assignment: " + assignment);

		// if the given assignement includes a var with value -1,
		// we need to unassign him from myAgentView..

		for (Integer var : assignment.assignedVariables()){

			int val = assignment.getAssignment(var);

			if (-1 == val){
				
				print(getId() + " updateAgentView: before removing var: " + var + " from myAgentView: " + myAgentView);
				myAgentView.unassign(var);
				print(getId() + " updateAgentView: after removing var: " + var + " from myAgentView: " + myAgentView);
			}
			else{
				
				print(getId() + " updateAgentView: before assigning var: " + var + " val: " + val + " to myAgentView: " + myAgentView);
				myAgentView.assign(var, val);
				print(getId() + " updateAgentView: after assigning var: " + var + " val: " + val + " to myAgentView: " + myAgentView);
			}
		}

		removeInconsistentNogoods();
	}

	protected void removeInconsistentNogoods() {

		print(getId() + " before removeInconsistentNogoods: myNogoodStore: " + myNogoodStore + " myAgentView: " + myAgentView);

		Vector<Integer> keysToRemove = new Vector<Integer>();

		for(Integer key : myNogoodStore.keySet()){

			Vector<Integer> nogoodsToRemove = new Vector<Integer>();
			Vector<Assignment> nogoodsOfThisKey = myNogoodStore.get(key);

			for (int i = 0; i < nogoodsOfThisKey.size(); i++)
				if (!coherent(getLHSOFNogood(nogoodsOfThisKey.get(i)), myAgentView.assignedVariables()))
//				if (!coherent(myAgentView, getHigherpriorityNeighborsFromNogood(nogoodsOfThisKey.get(i))))
					nogoodsToRemove.add(0,i);

			for (Integer indexToRemove : nogoodsToRemove)
				nogoodsOfThisKey.remove(indexToRemove.intValue());

			if (nogoodsOfThisKey.isEmpty())
				keysToRemove.add(key);
		}

		for (Integer key : keysToRemove)
			myNogoodStore.remove(key);
		
		print(getId() + " before removeInconsistentNogoods: myNogoodStore: " + myNogoodStore + " myAgentView: " + myAgentView);
	}

	private Assignment getLHSOFNogood(Assignment nogood) {

		print(getId() + " before getLHSOFNogood: nogood: " + nogood);
		
		Assignment ans = new Assignment();
		
		for (Integer var : nogood.assignedVariables())
			if (var != getId())
				ans.assign(var, nogood.getAssignment(var));
		
		print(getId() + " after getLHSOFNogood: ans: " + ans);
		
		return ans;
	}

	private boolean coherent(Assignment nogood, Set<Integer> agents) {

		print(getId() + " coherent: nogood: " + nogood + " agents: " + agents);

		//TODO: why we need agents??.. maybe intersect instead of union?..

		for (Integer var : nogood.assignedVariables()){

			//TODO: intersect..
			if (!agents.contains(var))
				continue;
			
			if ((getId() == var) && (nogood.getAssignment(var) != myValue))
				return false;

			if (!myAgentView.isAssigned(var))
				continue;

			if (nogood.getAssignment(var) != myAgentView.getAssignment(var))
				return false;
		}

		return true;
	}

	@WhenReceived("ADL")
	public void setLink(){

		Integer sender = getCurrentMessage().getSender();

		print(getId() + " setLink (got ADL) from " + sender + " lowerPriorityNeighbors before: " + lowerPriorityNeighbors);

		lowerPriorityNeighbors.add(sender);

		print(getId() + " setLink (got ADL) from " + sender + " lowerPriorityNeighbors after: " + lowerPriorityNeighbors);
		
		send("OK", myValue).to(sender);
	}

	private void checkAddLink(Assignment nogood) {

		print(getId() + " checkAddLink: nogood: " + nogood);

		for (Integer var : nogood.assignedVariables()){

			//TODO: != or >
			if ((getId() > var) && !higherPriorityNeighbors.contains(var)){

				send("ADL").to(var);

				print(getId() + " checkAddLink: asked var: " + var + " to be my neighbor");
				
				higherPriorityNeighbors.add(var);

				Assignment assignment = new Assignment();

				assignment.assign(var, nogood.getAssignment(var));

				updateAgentView(assignment);
			}
		}
	}

	@Override
	public void onIdleDetected() {
		finish(myValue);
	}

	protected void print(String string) {
		System.err.println(string);
		System.err.flush();
	}
}
