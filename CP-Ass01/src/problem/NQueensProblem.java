package problem;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import algoritm.VariablesPair;

public class NQueensProblem extends Problem {

	public NQueensProblem(int n) {

		super(n, n, 1/n, 1/n);
	}

	@Override
	protected void initConstraints() {
		
		Vector<Vector<Map<VariablesPair, Boolean>>> constraint =
			new Vector<Vector<Map<VariablesPair, Boolean>>>(getN());
		
		Vector<Map<VariablesPair, Boolean>> tmpVec = null;
		Map<VariablesPair, Boolean> tmpMap = null;
		
		for (int i = 0; i < getN(); i++){
			
			tmpVec = new Vector<Map<VariablesPair, Boolean>>(getN());
			
			for (int j = 0; j < getN(); j++){
				
				tmpMap = new HashMap<VariablesPair, Boolean>(getD()*getD());
				
				for (int di = 0; di < getD(); di++){
				
					for (int dj = 0; dj < getD(); dj++){
						
						if (	(di == dj) || (i-j == di-dj) || (j-i == di-dj)
										   || (i-j == dj-di) || (j-i == dj-di)	)
							tmpMap.put(new VariablesPair(di, dj), false);
						
						else
							tmpMap.put(new VariablesPair(di, dj), true);
					}
				}
				
				tmpVec.add(tmpMap);
			}
			
			constraint.add(tmpVec);
		}
		
		setConstraints(constraint);
	}
	
	@Override
	public String toString() {
		return getN() + " Queens";
	}
}
