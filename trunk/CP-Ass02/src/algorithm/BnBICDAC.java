package algorithm;

import java.util.Vector;

import problem.Problem;

public class BnBICDAC extends BnBIC {
	
	public BnBICDAC() {
		super();
	}
	
	public BnBICDAC(Problem problem){
		super(problem);
	}
	
	@Override
	protected void init(Problem problem){
		super.init(problem);
	}
	
	@Override
	protected int dac(int i){
		
		int dac = 0;
		
		for (int j = i + 1; j < _problem.getN(); j++){
			
			boolean found = false;
			
			for (int k = 0; k < _problem.getD() && !found; k++)
				if (_ic.get(j).get(k) == 0)
					found = true;
			
			if (!found) dac++;
		}
		
		return dac;
	}
	
	@Override
	public String toString(){
		return "BnBICDAC";
	}
}
