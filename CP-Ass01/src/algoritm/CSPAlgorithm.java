package algoritm;

import java.util.Vector;

import problem.Problem;


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
		
		for (int i = 0; i < _problem.getN(); i++)
			_currentDomain.add(
					(Vector<Integer>)_problem.getDomain().get(i).clone());
		
		_status = INITIALIZED;
	}
	
	public void solve(Problem problem) throws Exception{
		
		init(problem);
		solve();
	}
	
	public void solve() throws Exception{
		
		if (UNINITIALIZED == _status)
			throw new Exception("Please initialize the algorithm with a Problem");
		
		_problem.initDataStructures();
		
		_consistent = true;
		
		_status = UNKNOWN;
		
		int i = 0;
		
		while (UNKNOWN == _status){
			
			if (_consistent) i = label(i);
			else i = unlabel(i);
			
			if (i >= _problem.getN()){
				
				_status = SOLUTION;
				_problem.setSolved(true);
			}
			else if (-1 == i) _status = IMPOSSIBLE;
		}
	}

	public abstract int label(int i);
	public abstract int unlabel(int i);
}
