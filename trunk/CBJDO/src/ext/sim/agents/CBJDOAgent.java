package ext.sim.agents;

import java.util.SortedSet;
import java.util.TreeSet;

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
			send("LABEL", cpa).toNextAgent();

			print(getId() + " assigned the value "
					+ getFirstElementInCurrentDomain()
					+ " and sent LABEL to next agent");
		}
	}

	protected int getFirstElementInCurrentDomain() {

		return currentDomain.first();
	}

	private void removeFirstElementFromCurrentDomain() {

		currentDomain.remove(currentDomain.first());
	}

	@WhenReceived("LABEL")
	public void handleLABEL(Assignment cpa) {

		print(getId() + " got LABEL from " + getCurrentMessage().getSender()
				+ " with cpa: " + cpa);

		int h = 0;

		boolean consistent = false;

		while (!currentDomain.isEmpty() && !consistent) {

			consistent = true;

			cpa.assign(getId(), getFirstElementInCurrentDomain());

			print(getId() + " assigned the value "
					+ getFirstElementInCurrentDomain());

			for (h = 0; h < getId() && consistent; h++)
				consistent = getProblem().isConsistent(getId(),
						cpa.getAssignment(getId()), h, cpa.getAssignment(h));

			if (!consistent) {

				confSet.add(new Integer(h - 1));
				removeFirstElementFromCurrentDomain();
			}
		}

		desicion(consistent, cpa, getId() + 1);

	}

	protected void desicion(boolean consistent, Assignment cpa, int toWho) {

		if (isLastAgent() && consistent)
			finish(cpa);

		else if (isFirstAgent() && currentDomain.isEmpty())
			finishWithNoSolution();

		else if (consistent)
			send("LABEL", cpa).to(toWho);

		else if (!consistent) {

			if (confSet.isEmpty()){
				finishWithNoSolution();
				return;
			}

			int h = confSet.last();

			confSet.remove(new Integer(h));

			send("UNLABEL", cpa, confSet).to(h);
		}
	}

	@WhenReceived("UNLABEL")
	public void handleUNLABEL(Assignment cpa, SortedSet<Integer> confSetOfI) {

		print(getId() + " got UNLABEL from " + getCurrentMessage().getSender()
				+ " with cpa: " + cpa + " and confSet: " + confSetOfI);

		confSet.addAll(confSetOfI);

		int i = getCurrentMessage().getSender();

		for (int j = getId() + 1; j <= i; j++)
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
