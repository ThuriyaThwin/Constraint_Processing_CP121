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
	
	@SuppressWarnings("unchecked")
	public Vector<Integer> solve() throws Exception{
		
		if (UNINITIALIZED == _status)
			throw new Exception("Please initialize the algorithm with a Problem");
		
		_problem.initDataStructures();
		
		//TODO
		
		_best_sol = new Vector<Integer>(_problem.getN());
		_best_dist = Integer.MAX_VALUE;
		_sum_min_ic = 0;
		
		PEFC3(new Vector<Integer>(), 0, 0, (Vector<Vector<Integer>>)_problem.getDomain().clone());

		return null;
	}
	
	protected	int						_best_dist;		// ub
	protected	Vector<Integer>			_best_sol;
//	protected	Vector<Vector<Integer>>	_currentDomain;
	protected	int						_sum_min_ic;			

	@SuppressWarnings("unchecked")
	protected void PEFC3 (Vector<Integer> curr_sol, int dist, int next_var_index, Vector<Vector<Integer>> remaining_dom){

		int i = next_var_index;
		
//		values := sort-values(v, remaining_dom)	TODO
		
		while (!remaining_dom.get(i).isEmpty()){
			
			Integer v = remaining_dom.get(i).firstElement();
			
			int new_dist = dist + getIC(i, v, curr_sol);
			
			if (i == _problem.getN()){
				
				if (new_dist < _best_dist){
					
					_best_dist = new_dist;
					curr_sol.set(i, v);			// TODO
					_best_sol = curr_sol;
				}
			}
			else{
				
				if (new_dist + _sum_min_ic < _best_dist){
					
					updateIC(remaining_dom, i, v);
					
					if (new_dist + _sum_min_ic < _best_dist){
						
						curr_sol.set(i, v);
						PEFC3 (curr_sol, new_dist, i + 1,
								(Vector<Vector<Integer>>)remaining_dom.clone());
					}
				}
			}
			
			remaining_dom.get(i).removeElementAt(0);
		}
	}

	protected int getIC(int i, Integer v, Vector<Integer> currSol) {

		int sum = 0;
		
		for (int h = 0; h < i; h++ )
			sum += _problem.check(h, currSol.get(h), i, v);
		
		return sum;
	}
	
	protected void updateIC(Vector<Vector<Integer>> remainingDom, int i, Integer v) {
		return;
	}
}
