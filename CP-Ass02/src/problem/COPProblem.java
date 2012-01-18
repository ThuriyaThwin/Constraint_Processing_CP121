package problem;

import java.util.Map;
import java.util.Random;

import algorithm.VariablesPair;

public class COPProblem extends Problem{

	protected	int	_mc;

	public COPProblem(int n, int d, double p1, double p2, int mc) {
		super(n, d, p1, p2, mc);
	}

	public COPProblem(int n, int d, double p1, double p2, int mc, Random random) {
		super(n, d, p1, p2, mc, random);
	}

	@Override
	protected void setEdgeCost(Map<VariablesPair, Integer> tmpMap,
			boolean dontHaveConstarint, int di, int dj) {

		double random = getRandom().nextDouble();

		int rand = (int) ((getMC() == 1) ? Math.round(random) : random * getMC());

		tmpMap.put(new VariablesPair(di, dj), new Integer(rand));
	}
	
	@Override
	public String toString() {
		return "N=" + getN() + ", D=" + getD() + ", P1=" + getP1() + ", MC=" + getMC();
	}
}
