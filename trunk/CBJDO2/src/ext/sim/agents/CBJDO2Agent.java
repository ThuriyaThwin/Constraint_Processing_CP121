package ext.sim.agents;

import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import bgu.dcr.az.api.*;
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
		return getId() + 1;
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

			cpa.assign(getId(), getFirstElementInCurrentDomain());

			print(getId() + " tries the value "
					+ getFirstElementInCurrentDomain());

			for (h = 0; h < assignedVariables.size() && consistent; h++)
				consistent = getProblem().isConsistent(getId(),
						cpa.getAssignment(getId()), assignedVariables.get(h),
						cpa.getAssignment(assignedVariables.get(h)));

			if (!consistent) {

				confSet.add(new Integer(h - 1));
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
			assignedVariables.add(getId());
			send("LABEL", cpa, assignedVariables).to(toWho);
		}

		else if (!consistent) {

			if (confSet.isEmpty()) {
				finishWithNoSolution();
				return;
			}

			int h = getH();

			confSet.remove(new Integer(h));

			send("UNLABEL", cpa, confSet).to(h);
		}
	}

	private int getH() {

		int last = -1;

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

		int j;
		
		for (j = 0; j < assignedVariables.size(); j++)
			if (assignedVariables.get(j) == getId())
				break;
		
		for (j++; j < assignedVariables.size(); j++)
			send("CLEAR_AND_RESTORE").to(j);

		currentDomain.remove(cpa.getAssignment(getId()));

		desicion(!currentDomain.isEmpty(), cpa, getId());
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
		System.err.println(string);
		System.err.flush();
	}

}
