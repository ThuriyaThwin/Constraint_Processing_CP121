package ext.sim.agents;

import java.util.Set;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name = "ABT", useIdleDetector = false)
public class ABTAgent extends SimpleAgent {

//	http://azapi-test.googlecode.com/svn/trunk/bin/documentation/javadoc/index.html
	
	private Assignment	agent_view		= null;
	private Integer		current_value	= null;
	private Integer		old_value		= null;
	
	@Override
	public void start() {

		agent_view		= new Assignment();
		current_value	= new Integer(0);
		old_value		= new Integer(-1);

		// TODO:	KICK START THE ALGORITHM..
		//			giving the first variable of the domain maybe?..
	}
	
	@WhenReceived("OK")
	public void handleOK(int value) {

		agent_view.assign(getCurrentMessage().getSender(), value);
		// TODO: "remove non consistent NOGOODS";
		checkAgentView();
	}

	@WhenReceived("NOGOOD")
	public void handleNOGOOD(Assignment noGood) {
		
		old_value = current_value;
		
		if (isNogoodConsistentWithAgentView(noGood) &&
			noGood.isConsistentWith(getId(), current_value, getProblem())){
			
			// TODO: "store noGood"
			addNewNeighborsFromNogood(noGood);
			checkAgentView();
		}
		
		if (old_value == current_value)
			 send("OK", current_value).to(getCurrentMessage().getSender());
	}

	private void checkAgentView() {

		if (!agent_view.isConsistentWith(getId(), current_value, getProblem())){
			
			if (!isThereAValueInDWhichConsistentWithAgentView())
				backtrack();
			
			else{
				
				current_value = getValueFromDWhichConsistentWithAgentView();
				send("OK", current_value).toAllAgentsAfterMe();	// TODO: is it going to send the message to the low_priority_neighbors??..
			}
		}
	}

	private void backtrack() {
		
		Assignment noGood = resolveInconsistentSubset(); //TODO: is it should be a global variable?..
		
		if (noGood.getNumberOfAssignedVariables() == 0){
			
			send("NO_SOLUTION").toAllAgentsAfterMe(); // TODO: is this sufficient?...
			finish();
			return;
		}
		
		int lowerPriorityVar = -1;
		
		for (Integer v : noGood.assignedVariables())
			if (v > lowerPriorityVar)
				lowerPriorityVar = v;
		
		send("NOGOOD", noGood).to(lowerPriorityVar);
		
		agent_view.unassign(lowerPriorityVar);
		
		// TODO: remove all Nogoods caontaining 'lowerPriorityVar' and 'noGood.getAssignment(lowerPriorityVar)'
		
		checkAgentView();
	}

	private boolean isNogoodConsistentWithAgentView(Assignment noGood) {
		
		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();
		ImmutableSet<Integer> agentViewVariables = agent_view.assignedVariables();
		
		for (Integer v : noGoodVariables){
			
			if (!agentViewVariables.contains(v))
				continue;
			
			else if (noGood.getAssignment(v.intValue()) != agent_view.getAssignment(v.intValue()))
				return false;
		}
		
		return true;
	}

	private void addNewNeighborsFromNogood(Assignment noGood) {

		ImmutableSet<Integer> noGoodVariables = noGood.assignedVariables();
		Set<Integer> neighbors = getNeighbors();
		
		for (Integer v : noGoodVariables){
			
			if (!neighbors.contains(v)){
				
				send("ADD_NEIGHBOR").to(v);
				agent_view.assign(v, noGood.getAssignment(v));
			}
		}
	}

	@WhenReceived("ADD_NEIGHBOR")
	public void handleADDNEIGHBOR(){
		getNeighbors().add(getCurrentMessage().getSender());
		// TODO: add the sender also to the agent_view or something else??..
	}
	
	private boolean isThereAValueInDWhichConsistentWithAgentView() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private int getValueFromDWhichConsistentWithAgentView() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private Assignment resolveInconsistentSubset() {
		// TODO Auto-generated method stub
		return null;
	}

	@WhenReceived("NO_SOLUTION")
	public void handleNOSOLUTION(){
		finish();	// TODO: is this sufficient?..
	}

	
}
