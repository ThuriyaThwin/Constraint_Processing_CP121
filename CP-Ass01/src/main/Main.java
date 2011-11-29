package main;

import java.util.Vector;

import algoritm.CSPAlgorithm;

public class Main {

	public static void main(String[] args) {

		Vector<Problem> problems = new Vector<Problem>();
		
		CSPAlgorithm algoritm = new BTAlgoritm();
		
		for (Problem p: problems)
			algoritm.solve(p);
	}
}
