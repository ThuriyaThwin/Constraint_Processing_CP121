package problem;

import java.util.Map;
import java.util.Random;

import algorithm.VariablesPair;

public class COPProblem extends Problem{

	private	int	_mc;
	
	public COPProblem(int n, int d, double p1, double p2, int mc) {
		super(n, d, p1, p2);
		setMC(mc);
	}
	
	public COPProblem(int n, int d, double p1, double p2, int mc, Random random) {
		super(n, d, p1, p2, random);
		setMC(mc);
	}

	@Override
	protected void setEdgeCost(Map<VariablesPair, Integer> tmpMap,
			boolean dontHaveConstarint, int di, int dj) {

		Integer rand = new Integer((int)(getRandom().nextDouble() * getMC()));
		
		tmpMap.put(new VariablesPair(di, dj), rand);
	}

	protected void setMC(int _mc) {
		this._mc = _mc;
	}

	protected int getMC() {
		return _mc;
	}
}
