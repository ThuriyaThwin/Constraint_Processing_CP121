package algorithm;

import java.util.Vector;

import problem.Problem;

public class BnBIC extends BnB {

	protected	Vector<Vector<Integer>>	_ic;
	
	public BnBIC() {
		super();
	}
	
	public BnBIC(Problem problem){
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
	protected void updateIC(Vector<Vector<Integer>> remainingDom, int i, Integer v, int x) {

		for (int j = i + 1; j < _problem.getN(); j++){
			
			for (int k = 0; k < _problem.getD(); k++){
			
				int toAdd = (_problem.check(i, v, j, remainingDom.get(j).get(k)) == 0) ? 0 : x;
				Integer oldValue = _ic.get(j).get(k);
				
				_ic.get(j).set(k, new Integer(oldValue + toAdd));
			}
		}
		
		calcSumMinIC(i);
	}
	
	@Override
	protected void restoreIC(Vector<Vector<Integer>> remainingDom, int i, Integer v) {
		updateIC(remainingDom, i, v, -1);
	}

	protected void calcSumMinIC(int i) {

		_sum_min_ic = 0;
		
		for (int j = i + 1; j < _problem.getN(); j++){
			
			int min = _ic.get(j).get(0);
			
			for (int k = 1; k < _problem.getD(); k++){
	
				int x = _ic.get(j).get(k);
				
				if (x < min)
					min = x;
			}
			
			_sum_min_ic += min;
		}
	}
}
