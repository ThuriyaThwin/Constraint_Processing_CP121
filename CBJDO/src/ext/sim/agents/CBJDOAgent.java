package ext.sim.agents;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name="CBJDO", useIdleDetector=false)
public class CBJDOAgent extends SimpleAgent {
	
	protected Set<Integer>			currentDomain	= null;
	protected SortedSet<Integer>	confSet			= null;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start() {
		
		currentDomain = new HashSet(getDomain());
		confSet = new TreeSet<Integer>();
		
		if (isFirstAgent()) {
		
			Assignment cpa = new Assignment();
			cpa.assign(getId(), getFirstElementInCurrentDomain());
			send("LABEL", cpa).toNextAgent();
		}
	}

	protected int getFirstElementInCurrentDomain() {
		
		// TODO Auto-generated method stub
		return 0;
	}

	private void removeFirstElementFromCurrentDomain() {
		
		// TODO Auto-generated method stub
	}

	@WhenReceived("LABEL")
	public void handleLABEL(Assignment cpa){

		int h = 0;
		
		boolean consistent = false;
		
		while  (!currentDomain.isEmpty() && !consistent){
			
			consistent = true;

			cpa.assign(getId(), getFirstElementInCurrentDomain());

			for (h = 0; h < getId() && consistent; h++)
				consistent = getProblem().isConsistent(getId(), cpa.getAssignment(getId()), h, cpa.getAssignment(h));
			
			if (!consistent){
				
				confSet.add(new Integer(h - 1));
				removeFirstElementFromCurrentDomain();
			}
		}
		
		desicion(consistent, cpa);
		
	}

	@WhenReceived("UNLABEL")
	public void handleUNLABEL(){

	
	}

	protected void desicion(boolean consistent, Assignment cpa) {
		
		if (isLastAgent())
			finish(cpa);
		
		else if (isFirstAgent() && currentDomain.isEmpty())
			finishWithNoSolution();
		
		else if (consistent)
			send("LABEL", cpa).toNextAgent();
		
		else if (consistent)
			send("UNLABEL", cpa).toPreviousAgent();
	}
}
