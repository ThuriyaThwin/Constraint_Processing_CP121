package problem;

import java.util.Map;
import java.util.Random;

import algorithm.VariablesPair;

public class MaxCSPProblem extends Problem {

	public MaxCSPProblem(int n, int d, double p1, double p2) {
		super(n, d, p1, p2);
	}
	
	public MaxCSPProblem(int n, int d, double p1, double p2, Random random) {
		super(n, d, p1, p2, random);
	}

	@Override
	protected void setEdgeCost(Map<VariablesPair, Integer> tmpMap,
			boolean dontHaveConstarint, int di, int dj) {

		if (dontHaveConstarint || getRandom().nextDouble() > getP2())
			tmpMap.put(new VariablesPair(di, dj), 0);
		
		else
			tmpMap.put(new VariablesPair(di, dj), 1);
	}
}
