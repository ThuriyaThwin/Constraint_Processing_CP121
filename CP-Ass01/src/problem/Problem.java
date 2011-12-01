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
	
	public Problem(int n, int d, double p1, double p2) {

		setN(n);
		setD(d);
		setP1(p1);
		setP2(p2);
		
		setV(new Vector<Integer>(n));
		
		setDomain(new Vector<Vector<Integer>>(n));
		
		for (int i = 0; i < n; i++){
			
			Vector<Integer> tmpVec = new Vector<Integer>(d);
			
			for (int j = 1; j <= d; j++)		//TODO: should be 0 ?..
				tmpVec.add(new Integer(j));
			
			getDomain().add(tmpVec);
		}
		
		initConstraints();
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
				
		return getConstraints().get(var1).get(var2).get(new VariablesPair(val1, val2));
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < getN(); i++)
			sb.append(" <" + i + "," + getV().get(i) + "> ");
		
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
	
	//TODO: need a way to save the information about the previous generated matrices
}
