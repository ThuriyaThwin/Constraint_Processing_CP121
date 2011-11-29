package algoritm;

import java.util.Vector;

import main.Problem;

public abstract class CSPAlgorithm {

	protected static final int UNINITIALIZED	= -1;
	protected static final int INITIALIZED		= 0;
	protected static final int UNKNOWN			= 1;
	protected static final int SOLUTION			= 2;
	protected static final int IMPOSSIBLE		= 3;
	
	protected	Problem					_problem;
	protected	boolean					_consistent;
	protected	int						_status;
	protected	Vector<Vector<Integer>>	_currentDomain;
	
	public CSPAlgorithm() {

		_status = UNINITIALIZED;
	}
	
	public CSPAlgorithm(Problem problem) {
		
		init(problem);
	}
	
	@SuppressWarnings("unchecked")
	protected void init(Problem problem) {
		
		_problem = problem;
		
		_currentDomain = new Vector<Vector<Integer>>();
		
		for (int i = 0; i < _currentDomain.size(); i++)
			_currentDomain.add(
					(Vector<Integer>)_problem.getDomain().get(i).clone());
		
		_status = INITIALIZED;
	}
	
	public String solve(Problem problem){
		
		init(problem);		
		return solve();
	}
	
	public String solve(){
		
		if (UNINITIALIZED == _status)
			return "Please initialize the algorithm with a Problem";
		
		_consistent = true;
		
		_status = UNKNOWN;
		
		int i = 0;											// TODO: should be 1 ?..
		
		while (UNKNOWN == _status){
			
			if (_consistent) i = label(i);
			else i = unlabel(i);
			
			if (i >= _problem.getN()) _status = SOLUTION;	// TODO: should be > ?..
			else if (-1 == i) _status = IMPOSSIBLE;			// TODO: should be 0 ?..
		}
		
		return printSolution();
	}

	protected String printSolution() {
		// TODO Auto-generated method stub
		return "";
	}

	public abstract int label(int i);
	public abstract int unlabel(int i);
}
