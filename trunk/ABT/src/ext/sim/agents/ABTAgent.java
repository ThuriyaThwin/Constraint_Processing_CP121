package ext.sim.agents;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
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
		// TODO: remove non consistent NOGOODS;
		checkAgentView();
	}

	@WhenReceived("NOGOOD")
	public void handleNOGOOD(Assignment sentCpa) {
		
		old_value = current_value;
		
		// TODO Auto-generated method stub
//		isConsistentWith
	}
	
	private void checkAgentView() {
		// TODO Auto-generated method stub
	}
	
	private void backtrack() {
		// TODO Auto-generated method stub
	}
}
