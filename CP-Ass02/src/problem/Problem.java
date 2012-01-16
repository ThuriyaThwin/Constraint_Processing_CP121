package problem;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import algorithm.VariablesPair;

public abstract class Problem {

	protected	int											_n;
	protected	int											_d;
	protected	double										_p1;
	protected	double										_p2;
	protected	Vector<Integer>								_v;
	protected	Vector<Vector<Integer>>						_domain;
	protected	Vector<Vector<Map<VariablesPair, Integer>>>	_constraints;
	protected	boolean										_solved;
	protected	int											_CCs;
	protected	int											_assignments;
	protected	Random										_random;
	
	/**
	 * n – the number of variables.
	 * d – the domain size.
	 * p1 – the probability for a constraint between 2 variables.
	 * p2 – the probability for a conflict between 2 constrained values.
	 */	
	public Problem(int n, int d, double p1, double p2) {
		this(n, d, p1, p2, new Random(17));
	}
	
	public Problem(int n, int d, double p1, double p2, Random random) {
		
		setN(n);
		setD(d);
		setP1(p1);
		setP2(p2);
		
		setRandom(random);
		
		initDataStructures();
		initEdgesConstraints();
	}

	public void initDataStructures() {
		
		Vector<Integer> tmpV = new Vector<Integer>(getN());
		
		for (int i = 0; i < getN(); i++)
			tmpV.add(new Integer(-1));

		setV(tmpV);
		
		setDomain(new Vector<Vector<Integer>>(getN()));
		
		for (int i = 0; i < getN(); i++){
			
			Vector<Integer> tmpVec = new Vector<Integer>(getD());
			
			for (int j = 0; j < getD(); j++)
				tmpVec.add(new Integer(j));
			
			getDomain().add(tmpVec);
		}
		
		setSolved(false);
		setCCs(0);
		setAssignments(0);
	}

	/**
	 * p1 – the probability for a constraint between 2 variables.
	 * p2 – the probability for a conflict between 2 constrained values.
	 */
	protected void initEdgesConstraints() {
		
		Vector<Vector<Map<VariablesPair, Integer>>> tConstraints =
			new Vector<Vector<Map<VariablesPair, Integer>>>(getN());

		Vector<Map<VariablesPair, Integer>> tmpVec = null;
		Map<VariablesPair, Integer> tmpMap = null;
		
		boolean dontHaveConstarint = false;
		
		for (int i = 0; i < getN(); i++) {

			tmpVec = new Vector<Map<VariablesPair, Integer>>(getN());
			
			for (int j = 0; j < getN(); j++){
				
				if (j < i){
					
					tmpVec.add(null);
					continue;
				}
				
				tmpMap = new HashMap<VariablesPair, Integer>(getD()*getD());
				
				dontHaveConstarint = (i == j) || getRandom().nextDouble() > getP1();
				
				for (int di = 0; di < getD(); di++){
					
					for (int dj = 0; dj < getD(); dj++){
						
						setEdgeCost(tmpMap, dontHaveConstarint, di, dj);
					}
				}
				
				tmpVec.add(tmpMap);
			}
			
			tConstraints.add(tmpVec);
		}

		setConstraints(tConstraints);
	}

	protected abstract void setEdgeCost(Map<VariablesPair, Integer> tmpMap,
			boolean dontHaveConstarint, int di, int dj);
	
	/**
	 * returns the cost of the joint assignment {<var1,val1> <var2,val2>}
	 * in this COP problem instance.
	 * 
	 * @param var1
	 * @param val1
	 * @param var2
	 * @param val2
	 * @return
	 */
	public int check(int var1, int val1, int var2, int val2){
				
		incCCs();
		
		if (var1 <= var2)
			return setConstraints().get(var1).get(var2).get(new VariablesPair(val1, val2));
		
		else
			return setConstraints().get(var2).get(var1).get(new VariablesPair(val2, val1));
	}
	
	@Override
	public String toString() {
		return "N=" + getN() + ", D=" + getD() + ", P1=" + getP1() + ", P2=" + getP2();
	}
	
	public String printSolution() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Assignment = ");
		
		for (int i = 0; i < getN(); i++)
			sb.append("<" + i + "," + getV().get(i) + ">,");
		
		sb.deleteCharAt(sb.lastIndexOf(","));
		
		sb.append(" , CCs=" + getCCs() + ", Assignments=" + getAssignments());
		
		return sb.toString();
	}

	protected void setN(int n) {
		this._n = n;
	}

	public int getN() {
		return _n;
	}

	public void setD(int d) {
		this._d = d;
	}

	public int getD() {
		return _d;
	}

	public void setP1(double p1) {
		this._p1 = p1;
	}

	public double getP1() {
		return _p1;
	}

	public void setP2(double p2) {
		this._p2 = p2;
	}

	public double getP2() {
		return _p2;
	}

	protected void setV(Vector<Integer> v) {
		this._v = v;
	}

	public Vector<Integer> getV() {
		return _v;
	}
	
	public void setVi(int i, Integer element) {
		
		incAssignments();
		
		getV().set(i, element);
	}

	protected void setDomain(Vector<Vector<Integer>> currentDomain) {
		this._domain = currentDomain;
	}

	public Vector<Vector<Integer>> getDomain() {
		return _domain;
	}

	public void setConstraints(Vector<Vector<Map<VariablesPair, Integer>>> constraints) {
		this._constraints = constraints;
	}

	public Vector<Vector<Map<VariablesPair, Integer>>> setConstraints() {
		return _constraints;
	}

	public void setSolved(boolean solved) {
		this._solved = solved;
	}

	public boolean isSolved() {
		return _solved;
	}

	public void setCCs(int cCs) {
		_CCs = cCs;
	}

	public int getCCs() {
		return _CCs;
	}
	
	public void incCCs(){
		_CCs++;
	}

	public void setAssignments(int assignments) {
		this._assignments = assignments;
	}

	public int getAssignments() {
		return _assignments;
	}
	
	public void incAssignments(){
		_assignments++;
	}

	public void setRandom(Random random) {
		this._random = random;
	}

	public Random getRandom() {
		return _random;
	}
}
