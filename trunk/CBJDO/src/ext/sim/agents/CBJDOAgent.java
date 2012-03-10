package ext.sim.agents;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name = "CBJDO", useIdleDetector = false)
public class CBJDOAgent extends SimpleAgent {

	protected SortedSet<Integer> currentDomain = null;
	protected SortedSet<Integer> confSet = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start() {

		currentDomain = new TreeSet(getDomain());
		confSet = new TreeSet<Integer>();

		if (isFirstAgent()) {

			Assignment cpa = new Assignment();

			cpa.assign(getId(), getFirstElementInCurrentDomain());

			ArrayList<Integer> assignedVariables = new ArrayList<Integer>();
			assignedVariables.add(getId());

			int nextAgent = getNextAgent(assignedVariables);

			send("LABEL", cpa, assignedVariables).to(nextAgent);

			print(getId() + " assigned the value "
					+ getFirstElementInCurrentDomain() + " and sent LABEL to "
					+ nextAgent);
		}
	}

	private int getNextAgent(ArrayList<Integer> assignedVariables) {
		// TODO Auto-generated method stub
		return getId() + 1;
	}

	protected int getFirstElementInCurrentDomain() {

		return currentDomain.first();
	}

	private void removeFirstElementFromCurrentDomain() {

		currentDomain.remove(currentDomain.first());
	}

	@WhenReceived("LABEL")
	public void handleLABEL(Assignment cpa, ArrayList<Integer> assignedVariables) {

		print(getId() + " got LABEL from " + getCurrentMessage().getSender()
				+ " with cpa: " + cpa);

		int h = 0;

		boolean consistent = false;

		while (!currentDomain.isEmpty() && !consistent) {

			consistent = true;

			// Just For in the num of CCs
			cpa.isConsistentWith(getId(), getFirstElementInCurrentDomain(), getProblem());
			
			cpa.assign(getId(), getFirstElementInCurrentDomain());

			print(getId() + " assigned the value "
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

		desicion(consistent, cpa, getNextAgent(assignedVariables),
				assignedVariables);
	}

	protected void desicion(boolean consistent, Assignment cpa, int toWho,
			ArrayList<Integer> assignedVariables) {

		if (cpa.getNumberOfAssignedVariables() == getNumberOfVariables() && consistent)
			finish(cpa);

		else if (assignedVariables.size() == 0 && currentDomain.isEmpty())
			finishWithNoSolution();

		else if (consistent) {
			if (!assignedVariables.contains(getId()))
				assignedVariables.add(getId());
			send("LABEL", cpa, assignedVariables).to(toWho);
		}

		else if (!consistent) {

			if (confSet.isEmpty()) {
				finishWithNoSolution();
				return;
			}

			int h = getHFromConfSet(assignedVariables);

			confSet.remove(new Integer(h));

			cpa.unassign(getId());
			removeMyselfFromAssignedVariables(assignedVariables);
			
			send("UNLABEL", cpa, confSet, assignedVariables).to(h);
		}
	}

	private int getHFromConfSet(ArrayList<Integer> assignedVariables) {

		int last = confSet.last();

		for (Integer var : assignedVariables)
			if (confSet.contains(var))
				last = var;

		return last;
	}

	@WhenReceived("UNLABEL")
	public void handleUNLABEL(Assignment cpa, SortedSet<Integer> confSetOfI,
			ArrayList<Integer> assignedVariables) {

		print(getId() + " got UNLABEL from " + getCurrentMessage().getSender()
				+ " with cpa: " + cpa + " and confSet: " + confSetOfI);
		
		removeMyselfFromAssignedVariables(assignedVariables);
		
		confSet.addAll(confSetOfI);

		clearAndRestoreVariables(assignedVariables);

		currentDomain.remove(cpa.getAssignment(getId()));

		cpa.unassign(getId());
		
		desicion(!currentDomain.isEmpty(), cpa, getId(), assignedVariables);
	}

	protected void clearAndRestoreVariables(ArrayList<Integer> assignedVariables) {

		int sender = getCurrentMessage().getSender();

		boolean toClear = false;

		Vector<Integer> variablesToClear = new Vector<Integer>();

		for (Integer var : assignedVariables) {

			if (toClear)
				variablesToClear.add(var);
			
			if (var == getId())
				toClear = true;

			if (var == sender)
				toClear = false;
		}

		assignedVariables.removeAll(variablesToClear);

		for (Integer var : variablesToClear)
			send("CLEAR_AND_RESTORE").to(var);
	}

	private void removeMyselfFromAssignedVariables(
			ArrayList<Integer> assignedVariables) {

		int index = -1;

		for (int i = 0; i < assignedVariables.size(); i++)
			if (assignedVariables.get(i) == getId())
				index = i;

		if (-1 != index)
			assignedVariables.remove(index);
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
