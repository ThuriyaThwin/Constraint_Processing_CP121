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
		
		Vector<Integer> currSol = new Vector<Integer>(_problem.getN());
		
		for (int i = 0; i < _problem.getN(); i++)
			currSol.add(new Integer(-1));
		
		PEFC3(currSol, 0, 0, (Vector<Vector<Integer>>)_problem.getDomain().clone());

		return null;
	}
	
	protected	int						_best_dist;		// ub
	protected	Vector<Integer>			_best_sol;
//	protected	Vector<Vector<Integer>>	_currentDomain;
	protected	int						_sum_min_ic;			

	@SuppressWarnings("unchecked")
	protected void PEFC3 (Vector<Integer> curr_sol, int dist, int next_var_index, Vector<Vector<Integer>> remaining_dom){
		
		int i = next_var_index;
		int vi = 0;
	
//		while (!remaining_dom.get(i).isEmpty()){
		while (vi < _problem.getD()){
			
			boolean hasBeenUpdated = false;
			
//			Integer v = remaining_dom.get(i).firstElement();
			Integer v = remaining_dom.get(i).get(vi);
			
			_problem.setVi(i, v);
			
			int new_dist = dist + getIC(i, v, curr_sol);
			
			if (i == _problem.getN() - 1){
				
				if (new_dist < _best_dist){
					
					_best_dist = new_dist;
					curr_sol.set(i, v);
					_best_sol = curr_sol;
					
					_problem.setsolutioncost(_best_dist);
					_problem.setV(_best_sol);
					
					//TODO ??..
					if (_best_dist <= _problem.getMC()){
						
						_problem.setSolved(true);
						return;
					}
				}
			}
			else{
				
				if (new_dist + dac(i) + _sum_min_ic < _best_dist){
					
					updateIC(remaining_dom, i, v, 1);
					
					hasBeenUpdated = true;
					
					if (new_dist + _sum_min_ic < _best_dist){
						
						curr_sol.set(i, v);
						PEFC3 (curr_sol, new_dist, i + 1,
								(Vector<Vector<Integer>>)remaining_dom.clone());
					}
				}
			}
			
			//remaining_dom.get(i).removeElementAt(0);
			vi++;

			if (hasBeenUpdated)
				restoreIC(remaining_dom, i, v);
		}
	}

	protected int getIC(int i, Integer v, Vector<Integer> currSol) {

		int sum = 0;
		
		for (int h = 0; h < i; h++ )
			sum += _problem.check(h, currSol.get(h), i, v);
		
		return sum;
	}
	
	protected void updateIC(Vector<Vector<Integer>> remainingDom, int i, Integer v, int x) {
		return;
	}
	
	protected void restoreIC(Vector<Vector<Integer>> remainingDom, int i, Integer v) {
		return;
	}

	protected int dac(int i) {
		return 0;
	}
	
	@Override
	public String toString(){
		return "BnB";
	}
}
