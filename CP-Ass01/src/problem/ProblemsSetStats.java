package problem;

import java.math.BigInteger;
import java.util.Vector;

public class ProblemsSetStats {

	protected	BigInteger			_fCCBJAssignments;
	protected	BigInteger			_fCCBJDACAssignments;
	protected	BigInteger			_fCCBJCCs;
	protected	BigInteger			_fCCBJDACCCs;
	
	protected	Vector<BigInteger>	_fCCBJAssignmentsVec;
	protected	Vector<BigInteger>	_fCCBJDACAssignmentsVec;
	protected	Vector<BigInteger>	_fCCBJCCsVec;
	protected	Vector<BigInteger>	_fCCBJDACCCsVec;

	public ProblemsSetStats() {
		
		_fCCBJAssignments = new BigInteger("0");
		_fCCBJDACAssignments = new BigInteger("0");
		_fCCBJCCs = new BigInteger("0");
		_fCCBJDACCCs = new BigInteger("0");
		
		_fCCBJAssignmentsVec = new Vector<BigInteger>();
		_fCCBJDACAssignmentsVec = new Vector<BigInteger>();
		_fCCBJCCsVec = new Vector<BigInteger>();
		_fCCBJDACCCsVec = new Vector<BigInteger>();
	}
	
	@Override
	public String toString() {

		BigInteger numOfProblems = new BigInteger(String.valueOf(_fCCBJAssignmentsVec.size()));
		
		if (0 == numOfProblems.compareTo(new BigInteger("0"))) return "EMPTY\n";
		
		String ans = "";
		
		ans += "Average FCCBJ Assignments = " + _fCCBJAssignments.divide(numOfProblems) + "\n";
		ans += "Average FCCBJDAC Assignments = " + _fCCBJDACAssignments.divide(numOfProblems) + "\n";
		ans += "Average FCCBJ CCs = " + _fCCBJCCs.divide(numOfProblems) + "\n";
		ans += "Average FCCBJDAC CCs = " + _fCCBJDACCCs.divide(numOfProblems) + "\n";
		
		return ans;
	}
	
	public void addFCCBJAssignments(BigInteger num){
		
		_fCCBJAssignments = _fCCBJAssignments.add(num);
		_fCCBJAssignmentsVec.add(num);
	}
	
	public void addFCCBJDACAssignments(BigInteger num){
		
		_fCCBJDACAssignments = _fCCBJDACAssignments.add(num);
		_fCCBJDACAssignmentsVec.add(num);
	}
	
	public void addFCCBJCCs(BigInteger num){
		
		_fCCBJCCs = _fCCBJCCs.add(num);
		_fCCBJCCsVec.add(num);
	}
	
	public void addFCCBJDACCCs(BigInteger num){
		
		_fCCBJDACCCs = _fCCBJDACCCs.add(num);
		_fCCBJDACCCsVec.add(num);
	}
	
	public BigInteger getFCCBJAssignments(){
		return _fCCBJAssignments;
	}
	
	public BigInteger getFCCBJDACAssignments(){
		return _fCCBJDACAssignments;
	}
	
	public BigInteger getFCCBJCCs(){
		return _fCCBJCCs;
	}
	
	public BigInteger getFCCBJDACCCs(){
		return _fCCBJDACCCs;
	}	
	
	public Vector<BigInteger> getFCCBJAssignmentsVec(){	
		return _fCCBJAssignmentsVec;
	}
	
	public Vector<BigInteger> getFCCBJDACAssignmentsVec(){
		return _fCCBJDACAssignmentsVec;
	}
	
	public Vector<BigInteger> getFCCBJCCsVec(){
		return _fCCBJCCsVec;
	}
	
	public Vector<BigInteger> getFCCBJDACCCsVec(){
		return _fCCBJDACCCsVec;
	}
}
