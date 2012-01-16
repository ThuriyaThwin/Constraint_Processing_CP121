package algorithm;

import java.util.Vector;

import problem.Problem;

public class BnBICDAC extends BnBIC {

protected	Vector<Vector<Integer>>	_ic;
	
	public BnBICDAC() {
		super();
	}
	
	public BnBICDAC(Problem problem){
		super(problem);
	}
	
	@Override
	protected void init(Problem problem){
		
		super.init(problem);
		
		_ic = new Vector<Vector<Integer>>(_problem.getN());
		
		for (int i = 0; i < _problem.getN(); i++)
			_ic.add(new Vector<Integer>(_problem.getD()));
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
}
