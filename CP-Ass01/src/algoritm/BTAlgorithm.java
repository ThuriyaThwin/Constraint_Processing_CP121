package algoritm;

import java.util.Vector;

import problem.Problem;


public class BTAlgorithm extends CSPAlgorithm {

	public BTAlgorithm(Problem problem) {
		super(problem);
	}
	
	public BTAlgorithm() {
		super();
	}
	
	protected void init(Problem problem) {
		super.init(problem);
	}

	@Override
	public int label(int i) {
		
		_consistent = false;
		
		while  (!_currentDomain.get(i).isEmpty() && !_consistent){

			_consistent = true;

			_problem.setVi(i, _currentDomain.get(i).firstElement());

			int h;
			
			for (h = 0;h < i && _consistent; h++)
				_consistent = _problem.check(i, _problem.getV().get(i), h, _problem.getV().get(h));

			if (!_consistent){
				
				doSomethingLabel(h-1, i);
				_currentDomain.get(i).removeElementAt(0);//hard coded
			}
		}

		return (_consistent) ? i + 1 : i;
	}

	@Override
	public int unlabel(int i) {

		int h = getHFromI(i);
		
		if (-1 != h){ 
		
			doSomethingUnlabel(h, i);
			
			_currentDomain.get(h).remove(_problem.getV().get(h));
			
			_consistent = !_currentDomain.get(h).isEmpty();
		}
		
		return h;
	}

	protected void doSomethingLabel(int hMinusOne, int i) {	}
	
	protected int getHFromI(int i) {
		return i - 1;
	}
	
	protected void doSomethingUnlabel(int h, int i) {
		
		restoreCurrentDomain(i);
	}

	@SuppressWarnings("unchecked")
	public void restoreCurrentDomain(int i) {
		_currentDomain.set(i, (Vector<Integer>)_problem.getDomain().get(i).clone());
	}
}
