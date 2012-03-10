package ext.sim.agents;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;

@Algorithm(name = "CBJDO2", useIdleDetector = false)
public class CBJDO2Agent extends SimpleAgent {

	protected SortedSet<Integer> currentDomain = null;
	protected SortedSet<Integer> confSet = null;
	protected Vector<Integer> assignedVariables = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start() {

		currentDomain = new TreeSet(getDomain());
		confSet = new TreeSet<Integer>();
		assignedVariables = new Vector<Integer>();

		if (isFirstAgent()) {

			Assignment cpa = new Assignment();
			cpa.assign(getId(), getFirstElementInCurrentDomain());
			assignedVariables.add(getId());
			
			int nextAgent = getNextAgent();

			send("LABEL", cpa, assignedVariables).to(nextAgent);

			print(getId() + " assigned the value "
					+ getFirstElementInCurrentDomain()
					+ " and sent LABEL to next agent");
		}
	}

	private int getNextAgent() {
		
		Set<Integer> allRemainingVars = new HashSet<Integer>();
		
		for (int i = 0; i < getNumberOfVariables(); i++)
			if (!assignedVariables.contains(i) && getId() != i)
				allRemainingVars.add(i);
		
		if (allRemainingVars.isEmpty())
			return -1;
		
		return random(allRemainingVars);
		
//		return getId() + 1;
	}

	protected int getFirstElementInCurrentDomain() {

		return currentDomain.first();
	}

	private void removeFirstElementFromCurrentDomain() {

		currentDomain.remove(currentDomain.first());
	}

	@WhenReceived("LABEL")
	public void handleLABEL(Assignment cpa, Vector<Integer> av) {

		print(getId() + " got LABEL from " + getCurrentMessage().getSender()
				+ " with cpa: " + cpa + " and av: " + av);

		assignedVariables = av;

		int h = 0;

		boolean consistent = false;

		while (!currentDomain.isEmpty() && !consistent) {

			consistent = true;

			// Just For in the num of CCs
			cpa.isConsistentWith(getId(), getFirstElementInCurrentDomain(), getProblem());
			
			cpa.assign(getId(), getFirstElementInCurrentDomain());

			print(getId() + " tries the value "
					+ getFirstElementInCurrentDomain());

			for (h = 0; h < assignedVariables.size() && consistent; h++)
				consistent = getProblem().isConsistent(getId(),
						cpa.getAssignment(getId()), assignedVariables.get(h),
						cpa.getAssignment(assignedVariables.get(h)));

			if (!consistent) {

				confSet.add(assignedVariables.get(h - 1));
				removeFirstElementFromCurrentDomain();
			}
		}

		desicion(consistent, cpa, getNextAgent());
	}

	protected void desicion(boolean consistent, Assignment cpa, int toWho) {

		if ((cpa.getNumberOfAssignedVariables() == getNumberOfVariables()) && consistent)
			finish(cpa);

		else if ((assignedVariables.size() == 0 && currentDomain.isEmpty()))
			finishWithNoSolution();

		else if (consistent) {
			
			Vector<Integer> tAV = new Vector<Integer>(assignedVariables);
			
			if (!tAV.contains(getId()) && toWho != getId())
				tAV.add(getId());
			
			send("LABEL", cpa, tAV).to(toWho);
		}

		else if (!consistent) {

			if (confSet.isEmpty()) {
				finishWithNoSolution();
				return;
			}

			int h = getH();

			confSet.remove(new Integer(h));

			cpa.unassign(getId());
			
			send("UNLABEL", cpa, confSet).to(h);
		}
	}

	private int getH() {

		int last = assignedVariables.lastElement();

		for (Integer var : assignedVariables)
			if (confSet.contains(var))
				last = var;

		return last;
	}

	@WhenReceived("UNLABEL")
	public void handleUNLABEL(Assignment cpa, SortedSet<Integer> confSetOfI) {

		print(getId() + " got UNLABEL from " + getCurrentMessage().getSender()
				+ " with cpa: " + cpa + " and confSet: " + confSetOfI);

		confSet.addAll(confSetOfI);

		currentDomain.remove(cpa.getAssignment(getId()));
		
		// TODO
		clearAndRestore(cpa);
		
		desicion(!currentDomain.isEmpty(), cpa, getId());
	}
	
	protected void clearAndRestore(Assignment cpa) {
		
		Set<Integer> dontSend = new HashSet<Integer>();
		
		for (Integer var : assignedVariables){
			
			if (getId() == var)
				break;
			
			dontSend.add(var);
		}
		
		for (int i = 0; i < getNumberOfVariables(); i++){
			
			if (!dontSend.contains(i) && getId() != i){
				
				cpa.unassign(i);
				send("CLEAR_AND_RESTORE").to(i);
			}
		}
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@WhenReceived("CLEAR_AND_RESTORE")
	public void handleCLEARANDRESTORE() {

		print(getId() + " got CLEAR_AND_RESTORE from "
				+ getCurrentMessage().getSender());

		confSet.clear();
		currentDomain = new TreeSet(getDomain());
	}

	private void print(String string) {
//		System.err.println(string);
//		System.err.flush();
	}

}
