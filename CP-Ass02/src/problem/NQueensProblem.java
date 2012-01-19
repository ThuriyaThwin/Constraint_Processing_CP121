package problem;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import main.Main;

import algorithm.VariablesPair;


public class NQueensProblem extends Problem {

	public NQueensProblem(int n) {

		super(n, n, 1/n, 1/n, Main.ZERO_MC);
	}

	@Override
	protected void initEdgesConstraints() {
		
		Vector<Vector<Map<VariablesPair, Integer>>> constraint =
			new Vector<Vector<Map<VariablesPair, Integer>>>(getN());
		
		Vector<Map<VariablesPair, Integer>> tmpVec = null;
		HashMap<VariablesPair, Integer> tmpMap = null;
		
		for (int i = 0; i < getN(); i++){
			
			tmpVec = new Vector<Map<VariablesPair, Integer>>(getN());
			
			for (int j = 0; j < getN(); j++){
				
				tmpMap = new HashMap<VariablesPair, Integer>(getD()*getD());
				
				for (int di = 0; di < getD(); di++){
				
					for (int dj = 0; dj < getD(); dj++){
						
						if (	(di == dj) || (i-j == di-dj) || (j-i == di-dj)
										   || (i-j == dj-di) || (j-i == dj-di)	)
							tmpMap.put(new VariablesPair(di, dj), 1);
						
						else
							tmpMap.put(new VariablesPair(di, dj), 0);
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

	@Override
	protected void setEdgeCost(Map<VariablesPair, Integer> tmpMap,
			boolean dontHaveConstarint, int di, int dj) {
		return;
	}
}
