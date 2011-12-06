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
		
		problems = generateProblems();
		
		DataManager dataManager = new DataManager();
		
		dataManager.storeProblems(problems, "problems");
		
		problems = dataManager.restoreProblems("problems");
		
		System.out.println("Finished Generating Problems, Starts solving..");

		CSPAlgorithm FCCBJAlgorithm = new FCCBJAlgorithm();		
		CSPAlgorithm FCCBJDACAlgorithm = new FCCBJDACAlgorithm();
		
		for (Problem p: problems){

			System.out.println(p);
			
			FCCBJAlgorithm.solve(p);
			System.out.println(p.printSolution());	
			
			FCCBJDACAlgorithm.solve(p);
			System.out.println(p.printSolution());
		}
	}

	private static Vector<Problem> generateProblems() {

		Vector<Problem> problems = new Vector<Problem>();
		
		problems.add(new NQueensProblem(8));
		
		return problems;
	}
}
