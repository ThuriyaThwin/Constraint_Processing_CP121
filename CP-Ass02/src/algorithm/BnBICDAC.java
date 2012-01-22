package algorithm;

import java.util.Vector;

import problem.Problem;

public class BnBICDAC extends BnBIC {

	protected	Vector<Vector<Integer>>	_dac;
	protected	int						_min_dac;

	public BnBICDAC() {
		super();
	}

	public BnBICDAC(Problem problem){
		super(problem);
	}

	@Override
	protected void init(Problem problem){
		
		super.init(problem);
		
		_dac = new Vector<Vector<Integer>>(_problem.getN());
		
		Vector<Integer> tmpVec = null;
		
		for (int i = 0; i < _problem.getN(); i++){
			
			tmpVec = new Vector<Integer>(_problem.getD());
			
			for (int j = 0; j < _problem.getN(); j++)
				tmpVec.add(new Integer(0));
			
			_dac.add(tmpVec);
		}
	}

	@Override
	protected void updateDAC() {
		
		for (int j1 = _problem.getN() - 1; j1 >= 0; j1--){
			
			for (int vj1 = 0; vj1 < _problem.getD(); vj1++){
				
				int count = 0;
				
				for (int j2 = j1 + 1; j2 < _problem.getN(); j2++){
					
					boolean found = false;
					
					for (int vj2 = 0; vj2 < _problem.getD() && !found; vj2++){
						
						if (_problem.check(j1, vj1, j2, vj2) == 0)
							found = true;;
					}
					
					if (!found) count++;
				}
				
				_dac.get(j1).set(vj1, new Integer(count));
			}
		}
	}
	
	@Override
	protected void calcSumMinIC(int i) {
		
		_sum_min_ic = 0;
		
		for (int j = i + 1; j < _problem.getN(); j++){
			
			int min = _ic.get(j).get(0) + _dac.get(j).get(0);
			
			for (int k = 1; k < _problem.getD(); k++){
	
				int x = _ic.get(j).get(k) + _dac.get(j).get(k);
				
				if (x < min)
					min = x;
			}
			
			_sum_min_ic += min;
		}
	}
	
	@Override
	protected int dac(int i, int v) {
		return _dac.get(i).get(v);
	}

	@Override
	public String toString(){
		return "BnBICDAC";
	}
}
