package problem;

import java.math.BigInteger;
import java.util.Vector;

public class ProblemsSetStats {

	protected	BigInteger			_bnBAssignments;
	protected	BigInteger			_bnBICAssignments;
	protected	BigInteger			_bnBDACAssignments;
	protected	BigInteger			_bnBICDACAssignments;
	protected	BigInteger			_bnBCCs;
	protected	BigInteger			_bnBICCCs;
	protected	BigInteger			_bnBDACCCs;
	protected	BigInteger			_bnBICDACCCs;

	protected	Vector<BigInteger>	_bnBAssignmentsVec;
	protected	Vector<BigInteger>	_bnBICAssignmentsVec;
	protected	Vector<BigInteger>	_bnBDACAssignmentsVec;
	protected	Vector<BigInteger>	_bnBICDACAssignmentsVec;
	protected	Vector<BigInteger>	_bnBCCsVec;
	protected	Vector<BigInteger>	_bnBICCCsVec;
	protected	Vector<BigInteger>	_bnBDACCCsVec;
	protected	Vector<BigInteger>	_bnBICDACCCsVec;

	public ProblemsSetStats() {

		_bnBAssignments = new BigInteger("0");
		_bnBICAssignments = new BigInteger("0");
		_bnBDACAssignments = new BigInteger("0");
		_bnBICDACAssignments = new BigInteger("0");
		_bnBCCs = new BigInteger("0");
		_bnBICCCs = new BigInteger("0");
		_bnBDACCCs = new BigInteger("0");
		_bnBICDACCCs = new BigInteger("0");

		_bnBAssignmentsVec = new Vector<BigInteger>();
		_bnBICAssignmentsVec = new Vector<BigInteger>();
		_bnBDACAssignmentsVec = new Vector<BigInteger>();
		_bnBICDACAssignmentsVec = new Vector<BigInteger>();
		_bnBCCsVec = new Vector<BigInteger>();
		_bnBICCCsVec = new Vector<BigInteger>();
		_bnBDACCCsVec = new Vector<BigInteger>();
		_bnBICDACCCsVec = new Vector<BigInteger>();
	}

	@Override
	public String toString() {

		BigInteger numOfProblems = new BigInteger(String.valueOf(_bnBICDACCCsVec.size()));

		if (0 == numOfProblems.compareTo(new BigInteger("0"))) return "EMPTY\n";

		String ans = "";

		ans += _bnBAssignments.divide(numOfProblems) + ",";
		ans += _bnBICAssignments.divide(numOfProblems) + ",";
		ans += _bnBDACAssignments.divide(numOfProblems) + ",";
		ans += _bnBICDACAssignments.divide(numOfProblems) + ",";
		ans += _bnBCCs.divide(numOfProblems) + ",";
		ans += _bnBICCCs.divide(numOfProblems) + ",";
		ans += _bnBDACCCs.divide(numOfProblems) + ",";
		ans += _bnBICDACCCs.divide(numOfProblems);

		return ans;
	}

	public void addBnBAssignments(BigInteger num){
		_bnBAssignments = _bnBAssignments.add(num);
		_bnBAssignmentsVec.add(num);
	}

	public void addBnBCCs(BigInteger num) {
		_bnBCCs = _bnBCCs.add(num);
		_bnBCCsVec.add(num);
	}

	public void addBnBICAssignments(BigInteger num) {
		_bnBICAssignments = _bnBICAssignments.add(num);
		_bnBICAssignmentsVec.add(num);
	}

	public void addBnBICCCs(BigInteger num) {
		_bnBICCCs = _bnBICCCs.add(num);
		_bnBICCCsVec.add(num);
	}

	public void addBnBICDACAssignments(BigInteger num) {
		_bnBICDACAssignments = _bnBICDACAssignments.add(num);
		_bnBICDACAssignmentsVec.add(num);
	}

	public void addBnBICDACCCs(BigInteger num) {
		_bnBICDACCCs = _bnBICDACCCs.add(num);
		_bnBICDACCCsVec.add(num);
	}

	public void addBnBDACAssignments(BigInteger num) {
		_bnBDACAssignments = _bnBDACAssignments.add(num);
		_bnBDACAssignmentsVec.add(num);
	}

	public void addBnBDACCCs(BigInteger num) {
		_bnBDACCCs = _bnBDACCCs.add(num);
		_bnBDACCCsVec.add(num);
	}

}
