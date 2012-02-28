package ext.sim.agents;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.*;

@Algorithm(name = "ABT", useIdleDetector = false)
public class ABTAgent extends SimpleAgent {

//	http://azapi-test.googlecode.com/svn/trunk/bin/documentation/javadoc/index.html
	
	private Assignment	agent_view		= null;
	private Integer		current_value	= null;
	private Integer		old_value		= null;
	
	@Override
	public void start() {

		// TODO: ADD INITIALIZING CODE HERE - DO NOT INITIALIZE ANYTHING IN THE
		// CONSTRACTOR!
		
		agent_view		= new Assignment();
		current_value	= new Integer(0);
		old_value		= new Integer(-1);
		
		//TODO ...
		
//		if (isFirstAgent()) {
//			// TODO: KICK START THE ALGORITHM
//			cpa = new Assignment();
//			assignCpa();
//		}
	}

//	//TODO: remove..
//	private void assignCpa() {
//
//		cpa.assign(getId(), random(getDomain()));
//		
//		if (isLastAgent()) finish(cpa);
//		
//		else send("CPA", cpa).toNextAgent();
//	}
	
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

		// TODO ...
		
		if (!agent_view.isConsistentWith(getId(), current_value, getProblem())){
			
		}
	}
	
	private void backtrack() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
	}
}
