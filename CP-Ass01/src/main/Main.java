package main;

import java.util.Vector;

import problem.NQueensProblem;
import problem.Problem;

import algoritm.BTAlgoritm;
import algoritm.CSPAlgorithm;

public class Main {

	public static void main(String[] args) {

		Vector<Problem> problems = new Vector<Problem>();
		
		problems.add(new NQueensProblem(8));
		
		CSPAlgorithm algoritm = new BTAlgoritm();
		
		for (Problem p: problems)
			System.out.println(algoritm.solve(p));
	}
}