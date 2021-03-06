package algorithm;

import java.util.Vector;

import problem.Problem;

public class BnB implements Algorithm {

	protected static final int UNINITIALIZED	= -1;
	protected static final int INITIALIZED		= 0;
	protected static final int UNKNOWN			= 1;
	protected static final int SOLUTION			= 2;
	protected static final int IMPOSSIBLE		= 3;

	protected	Problem			_problem;
	protected	int				_status;
	protected	int				_best_dist;
	protected	Vector<Integer>	_best_sol;
	protected	int				_sum_min_ic;

	public BnB(){
		_status = UNINITIALIZED;
	}

	public BnB(Problem problem){
		init(problem);
	}

	protected void init(Problem problem) {

		_problem = problem;
		_status = INITIALIZED;
	}

	@Override
	public void solve(Problem problem) throws Exception {

		init(problem);
		solve();
	}

	@SuppressWarnings("unchecked")
	public void solve() throws Exception{

		if (UNINITIALIZED == _status)
			throw new Exception("Please initialize the algorithm with a Problem");

		_problem.initDataStructures();

		_best_sol = new Vector<Integer>(_problem.getN());
		_best_dist = Integer.MAX_VALUE;
		_sum_min_ic = 0;

		Vector<Integer> currSol = new Vector<Integer>(_problem.getN());

		for (int i = 0; i < _problem.getN(); i++)
			currSol.add(new Integer(-1));

		updateDAC();

		PEFC3(currSol, 0, 0, (Vector<Vector<Integer>>)_problem.getDomain().clone());
	}

	@SuppressWarnings("unchecked")
	protected void PEFC3 (Vector<Integer> curr_sol, int dist, int next_var_index, Vector<Vector<Integer>> remaining_dom){

		if (_best_dist == 0) return;

		int i = next_var_index;
		int vi = 0;

		while (vi < _problem.getD()){

			boolean hasBeenUpdated = false;

			Integer v = remaining_dom.get(i).get(vi);

			_problem.incAssignments();

			int new_dist = dist + getIC(i, v, curr_sol);

			if (i == _problem.getN() - 1){

				if (new_dist < _best_dist){

					_best_dist = new_dist;
					curr_sol.set(i, v);
					_best_sol = curr_sol;

					_problem.setsolutioncost(_best_dist);
					_problem.setV(_best_sol);

					if (_best_dist <= _problem.getMC())
						_problem.setSolved(true);

					if (_best_dist == 0) return;
				}
			}
			else{

				if (new_dist + dac(i,v) + _sum_min_ic < _best_dist){

					updateIC(remaining_dom, i, v, 1);

					hasBeenUpdated = true;

					if (new_dist + _sum_min_ic < _best_dist){

						curr_sol.set(i, v);
						PEFC3 (curr_sol, new_dist, i + 1,
								(Vector<Vector<Integer>>)remaining_dom.clone());
					}
				}
			}

			vi++;

			if (hasBeenUpdated)
				restoreIC(remaining_dom, i, v);

			if (_best_dist == 0) return;
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

	protected void updateDAC() {
		return;
	}

	protected int dac(int i, int v) {
		return 0;
	}

	@Override
	public String toString(){
		return "BnB";
	}
}
