package algorithm;

import problem.Problem;

public class BnBDAC extends BnBICDAC{

	public BnBDAC() {
		super();
	}

	public BnBDAC(Problem problem){
		super(problem);
	}

	@Override
	protected void init(Problem problem){
		super.init(problem);
	}
	
	@Override
	protected void calcSumMinIC(int i) {
		return;	// in order to keek (_sum_min_ic == 0)
	}
	
	@Override
	public String toString(){
		return "BnBDAC";
	}
}
