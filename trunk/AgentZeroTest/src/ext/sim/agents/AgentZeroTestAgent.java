package ext.sim.agents;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;

@Algorithm(name = "AgentZeroTest", useIdleDetector = false)
public class AgentZeroTestAgent extends SimpleAgent {

	Assignment cpa = null;

	@Override
	public void start() {
		if (isFirstAgent()) {
			cpa = new Assignment();
			assignCpa();
		}
	}

	private void assignCpa() {
		cpa.assign(getId(), random(getDomain()));
		if (isLastAgent())
			finish(cpa);
		else
			send("CPA", cpa).toNextAgent();
	}

	@WhenReceived("CPA")
	public void handleCPA(Assignment sentCpa) {
		this.cpa = sentCpa;
		assignCpa();
	}
}
