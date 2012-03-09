package ext.sim.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
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

    	myAgentView = new Assignment();
    	
    	myValue = -1;
    	
    	computeNeighbors();
    	
    	myNogoodStore = new HashMap<Integer, Vector<Assignment>>();
    	
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
	}


	protected void checkAgentView() {
		
		if (!myAgentView.isConsistentWith(getId(), myValue, getProblem())){
			
			myValue = chooseValue();
			
			if (-1 != myValue)
				send("OK", myValue).toAll(lowerPriorityNeighbors);
				
			else
				backtrack();
		}
	}

	@WhenReceived("OK")
	public void ProcessInfo(Integer value){

    	Assignment tAss = new Assignment();
    	
    	tAss.assign(getCurrentMessage().getSender(), value);
    	
		updateAgentView(tAss);
    	checkAgentView();
	}
	
	@WhenReceived("NOGOOD")
	public void resolveConflict(Assignment nogood){

		SortedSet<Integer> self = new TreeSet<Integer>();
		SortedSet<Integer> agents = new TreeSet<Integer>(higherPriorityNeighbors);
		
		self.add(getId());
		agents.add(getId());
		
		if (coherent(nogood, agents)){
			
			checkAddLink(nogood);
			
			addNogoodToMyNogoodStore(nogood);
			
			myValue = -1;
			
			checkAgentView();
		}
		else if (coherent(nogood, self))
			send("OK", myValue).to(getCurrentMessage().getSender());
	}

	protected void addNogoodToMyNogoodStore(Assignment nogood) {

		Vector<Assignment> tVec = myNogoodStore.get(myValue);
		
		if (null == tVec)
			tVec = new Vector<Assignment>();
		//TODO:Make sure we delete non relevant no Goods
		tVec.add(nogood);
		
		myNogoodStore.put(myValue, tVec);
	}

	private void backtrack() {
		
		Assignment newNogood = solve(myNogoodStore);
		
		if(null == newNogood){
			
			finishWithNoSolution();
			return;
		}
		else{
			
			Integer lowPriorityAgent = getLhsFromNoGood(newNogood);
			
			send("NOGOOD",newNogood).to(lowPriorityAgent);
			
	    	Assignment tAss = new Assignment();
	    	tAss.assign(lowPriorityAgent, -1);
	    	
			updateAgentView(tAss);
			
			checkAgentView();
		}
	}
    
	private Assignment solve(Map<Integer, Vector<Assignment>> myNogoodStore2) {

		//TODO: improve it..
		//TODO:Make sure we are not missing out on agents.DBT style.
		Assignment nogood = new Assignment();
		
		for (Integer var : myAgentView.assignedVariables())
			nogood.assign(var, myAgentView.getAssignment(var));
		
		return nogood;
	}
	
	private Integer getLhsFromNoGood(Assignment assignment) {

		Integer ans = -1;
		
		for (Integer var : assignment.assignedVariables())
			if (ans < var)
				ans = var;
			
		return ans;
	}

	private Integer chooseValue() {
		// TODO Auto-generated method stub
		return null;
	}

    protected void updateAgentView(Assignment assignment) {
		
    	// if the given assignement includes a var with value -1,
    	// we need to unassign him from myAgentView..
    	
    	// TODO Auto-generated method stub
		
	}

    private boolean coherent(Assignment nogood, SortedSet<Integer> agents) {
		// TODO Auto-generated method stub
		return false;
	}
    
	private void checkAddLink(Assignment nogood) {
		// TODO Auto-generated method stub
		
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
