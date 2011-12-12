package problem;

import main.Main;

public class ProblemsSetStats {

	protected int _fCCBJAssignments;
	protected int _fCCBJDACAssignments;
	protected int _fCCBJCCs;
	protected int _fCCBJDACCCs;

	public ProblemsSetStats(int fCCBJAssignments, int fCCBJDACAssignments,
			int fCCBJCCs, int fCCBJDACCCs) {

		_fCCBJAssignments = fCCBJAssignments;
		_fCCBJDACAssignments = fCCBJDACAssignments;
		_fCCBJCCs = fCCBJCCs;
		_fCCBJDACCCs = fCCBJDACCCs;
	}
	
	@Override
	public String toString() {

		String ans = "";
		
		ans += "Average FCCBJ Assignments = " + _fCCBJAssignments/(double)Main.NUM_OF_PROBLEMS + "\n";
		ans += "Average FCCBJDAC Assignments = " + _fCCBJDACAssignments/(double)Main.NUM_OF_PROBLEMS + "\n";
		ans += "Average FCCBJ CCs = " + _fCCBJCCs/(double)Main.NUM_OF_PROBLEMS + "\n";
		ans += "Average FCCBJDAC CCs = " + _fCCBJDACCCs/(double)Main.NUM_OF_PROBLEMS + "\n";
		
		 return ans;
	}
}
