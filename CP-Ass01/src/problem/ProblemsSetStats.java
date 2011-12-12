package problem;

import java.util.Vector;

import main.Main;

public class ProblemsSetStats {

	protected	int				_numOfProblems;
	
	protected	int				_fCCBJAssignments;
	protected	int				_fCCBJDACAssignments;
	protected	int				_fCCBJCCs;
	protected	int				_fCCBJDACCCs;
	
	protected	Vector<Integer>	_fCCBJAssignmentsVec;
	protected	Vector<Integer>	_fCCBJDACAssignmentsVec;
	protected	Vector<Integer>	_fCCBJCCsVec;
	protected	Vector<Integer>	_fCCBJDACCCsVec;

	public ProblemsSetStats() {

		_numOfProblems = 0;
		
		_fCCBJAssignments = 0;
		_fCCBJDACAssignments = 0;
		_fCCBJCCs = 0;
		_fCCBJDACCCs = 0;
		
		_fCCBJAssignmentsVec = new Vector<Integer>();
		_fCCBJDACAssignmentsVec = new Vector<Integer>();
		_fCCBJCCsVec = new Vector<Integer>();
		_fCCBJDACCCsVec = new Vector<Integer>();
	}
	
	public ProblemsSetStats(int fCCBJAssignments, int fCCBJDACAssignments,
			int fCCBJCCs, int fCCBJDACCCs) {

		_numOfProblems = Main.NUM_OF_PROBLEMS;
		
		_fCCBJAssignments = fCCBJAssignments;
		_fCCBJDACAssignments = fCCBJDACAssignments;
		_fCCBJCCs = fCCBJCCs;
		_fCCBJDACCCs = fCCBJDACCCs;
	}
	
	@Override
	public String toString() {

		String ans = "";
		
		ans += "Average FCCBJ Assignments = " + (double)_fCCBJAssignments/_numOfProblems + "\n";
		ans += "Average FCCBJDAC Assignments = " + (double)_fCCBJDACAssignments/_numOfProblems + "\n";
		ans += "Average FCCBJ CCs = " + (double)_fCCBJCCs/_numOfProblems + "\n";
		ans += "Average FCCBJDAC CCs = " + (double)_fCCBJDACCCs/_numOfProblems + "\n";
		
		 return ans;
	}
	
	public void incNumOfProblems(){
		
		_numOfProblems++;
	}
	
	public void setNumOfProblems(int num){
		
		_numOfProblems = num;
	}
	
	public void addFCCBJAssignments(int num){
		
		_fCCBJAssignments += num;
		_fCCBJAssignmentsVec.add(num);
	}
	
	public void addFCCBJDACAssignments(int num){
		
		_fCCBJDACAssignments += num;
		_fCCBJDACAssignmentsVec.add(num);
	}
	
	public void addFCCBJCCs(int num){
		
		_fCCBJCCs += num;
		_fCCBJCCsVec.add(num);
	}
	
	public void addFCCBJDACCCs(int num){
		
		_fCCBJDACCCs += num;
		_fCCBJDACCCsVec.add(num);
	}
	
	public int getNumOfProblems(){
		return _numOfProblems;
	}
	
	public Vector<Integer> getFCCBJAssignments(){	
		return _fCCBJAssignmentsVec;
	}
	
	public Vector<Integer> getFCCBJDACAssignments(){
		return _fCCBJDACAssignmentsVec;
	}
	
	public Vector<Integer> getFCCBJCCs(){
		return _fCCBJCCsVec;
	}
	
	public Vector<Integer> getFCCBJDACCCs(){
		return _fCCBJDACCCsVec;
	}
}
