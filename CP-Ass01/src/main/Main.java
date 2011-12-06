package main;

import java.util.Vector;

import data.DataManager;

import problem.NQueensProblem;
import problem.Problem;

import algoritm.BTAlgorithm;
import algoritm.CBJAlgorithm;
import algoritm.CSPAlgorithm;
import algoritm.FCCBJAlgorithm;
import algoritm.FCCBJDACAlgorithm;

public class Main {

	public static void main(String[] args) throws Exception {

		Vector<Problem> problems;
		CSPAlgorithm algoritm;
		
		problems = generateProblems();
		
//		DataManager dataManager = new DataManager();
		
//		dataManager.storeProblems(problems, "problems");
		
//		problems = dataManager.restoreProblems("problems");
		
		System.out.println("Finished Generating Problems, Starts solving..");
//		
//		algoritm = new BTAlgorithm();
//		
//		for (Problem p: problems){
//			
//			algoritm.solve(p);
////			System.out.println(p);
//			System.out.println(p.printSolution());
//		}
//		
//		algoritm = new CBJAlgorithm();
//		
//		for (Problem p: problems){
//			
//			algoritm.solve(p);
////			System.out.println(p);
//			System.out.println(p.printSolution());
//		}
//		
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

	private static Vector<Problem> generateProblems() {

		Vector<Problem> problems = new Vector<Problem>();
		
		problems.add(new NQueensProblem(8));
		
		return problems;
	}
}
