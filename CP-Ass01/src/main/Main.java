package main;

import java.util.Vector;

import problem.NQueensProblem;
import problem.Problem;

import algoritm.BTAlgoritm;
import algoritm.CSPAlgorithm;

public class Main {

	public static void main(String[] args) throws Exception {

		Vector<Problem> problems = new Vector<Problem>();
		
		problems.add(new NQueensProblem(10));
		
		CSPAlgorithm algoritm = new BTAlgoritm();
		
		for (Problem p: problems){
			
			algoritm.solve(p);
			System.out.println(p);
			System.out.println(p.printSolution());
		}
	}
}
