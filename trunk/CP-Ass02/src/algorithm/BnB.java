package algorithm;

import java.util.Vector;

import problem.Problem;

public class BnB implements Algorithm {

	protected static final int UNINITIALIZED	= -1;
	protected static final int INITIALIZED		= 0;
	protected static final int UNKNOWN			= 1;
	protected static final int SOLUTION			= 2;
	protected static final int IMPOSSIBLE		= 3;
	
	protected	Problem	_problem;
	protected	int		_status;
	
	public BnB(){
		_status = UNINITIALIZED;
	}
	
	public BnB(Problem problem){
		init(problem);
	}
	
	protected void init(Problem problem) {

		_problem = problem;
		_status = INITIALIZED;
		
		//TODO
	}
	
	@Override
	public Vector<Integer> solve(Problem problem) throws Exception {
		
		init(problem);
		return solve();
	}
	
	public Vector<Integer> solve() throws Exception{
		
		if (UNINITIALIZED == _status)
			throw new Exception("Please initialize the algorithm with a Problem");
		
		//TODO
		
		return null;
	}

}
