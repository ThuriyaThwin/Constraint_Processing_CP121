package problem;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

import algoritm.VariablesPair;

public class Problem {

	protected	int											_n;
	protected	int											_d;
	protected	double										_p1;
	protected	double										_p2;
	protected	Vector<Integer>								_v;
	protected	Vector<Vector<Integer>>						_domain;
	protected	Vector<Vector<Map<VariablesPair, Boolean>>>	_constraints;
	protected	boolean										_solved;
	protected	int											_CCs;
	protected	int											_assignments;
	
	public Problem(int n, int d, double p1, double p2) {

		setN(n);
		setD(d);
		setP1(p1);
		setP2(p2);
		
		Vector<Integer> tmpV = new Vector<Integer>(n);
		
		for (int i = 0; i < n; i++)
			tmpV.add(new Integer(-1));

		setV(tmpV);
		
		setDomain(new Vector<Vector<Integer>>(n));
		
		for (int i = 0; i < n; i++){
			
			Vector<Integer> tmpVec = new Vector<Integer>(d);
			
			for (int j = 0; j < d; j++)
				tmpVec.add(new Integer(j));
			
			getDomain().add(tmpVec);
		}
		
		initConstraints();
		
		setSolved(false);
		setCCs(0);
		setAssignments(0);
	}

	protected void initConstraints() {
		// TODO Auto-generated method stub
		
		setConstraints(new Vector<Vector<Map<VariablesPair, Boolean>>>());
	}

	// TODO for further use...
	public Problem(int n, int d, double p1, double p2, Random random) {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * returns true iff there is no conflict between <var1,val1>
	 * to <var2,val2> in this CSP problem instance
	 * 
	 * @param var1
	 * @param val1
	 * @param var2
	 * @param val2
	 * @return
	 */
	public boolean check(int var1, int val1, int var2, int val2){
				
		incCCs();
		
		return getConstraints().get(var1).get(var2).get(new VariablesPair(val1, val2));
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "";
	}
	
	public String printSolution() {
		
		StringBuilder sb = new StringBuilder("Assignment = ");
		
		if (!isSolved())
			sb.append("UNSOLVED: ");

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

	public void setConstraints(Vector<Vector<Map<VariablesPair, Boolean>>> constraints) {
		this._constraints = constraints;
	}

	public Vector<Vector<Map<VariablesPair, Boolean>>> getConstraints() {
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
	
	//TODO: need a way to save the information about the previous generated matrices
}
