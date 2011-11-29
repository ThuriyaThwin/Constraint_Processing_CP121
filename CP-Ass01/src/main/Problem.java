package main;

import java.util.Map;
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
		_d = d;
		_p1 = p1;
		_p2 = p2;
		// TODO _v... , _domain.. , _constraints..
	}

	// TODO for further use...
	public Problem() {
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
		
		return false;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	protected void setN(int _n) {
		this._n = _n;
	}

	public int getN() {
		return _n;
	}

	protected void setV(Vector<Integer> _v) {
		this._v = _v;
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
	
	//TODO: need a way to save the information about the previous generated matrices
	
	
}
