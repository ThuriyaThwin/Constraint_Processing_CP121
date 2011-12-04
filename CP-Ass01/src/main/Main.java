package main;

import java.util.Vector;

import problem.NQueensProblem;
import problem.Problem;

import algoritm.BTAlgorithm;
import algoritm.CBJAlgorithm;
import algoritm.CSPAlgorithm;
import algoritm.FCCBJAlgorithm;
import algoritm.FCCBJDACAlgorithm;

public class Main {

	public static void main(String[] args) throws Exception {

		Vector<Problem> problems = new Vector<Problem>();
		
		problems.add(new NQueensProblem(8));
		
		CSPAlgorithm algoritm = new BTAlgorithm();
		
		for (Problem p: problems){
			
			algoritm.solve(p);
//			System.out.println(p);
			System.out.println(p.printSolution());
		}
		
		algoritm = new CBJAlgorithm();
		
		for (Problem p: problems){
			
			algoritm.solve(p);
//			System.out.println(p);
			System.out.println(p.printSolution());
		}
		
		algoritm = new FCCBJAlgorithm();
		
		for (Problem p: problems){
			
			algoritm.solve(p);
//			System.out.println(p);
			System.out.println(p.printSolution());
		}
		
		algoritm = new FCCBJDACAlgorithm();
		
		for (Problem p: problems){
			
			algoritm.solve(p);
//			System.out.println(p);
			System.out.println(p.printSolution());
		}
	}
}
