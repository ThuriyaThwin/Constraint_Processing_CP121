package main;

import java.util.Random;
import java.util.Vector;

import data.DataManager;

import problem.NQueensProblem;
import problem.Problem;

import algoritm.CSPAlgorithm;
import algoritm.FCCBJAlgorithm;
import algoritm.FCCBJDACAlgorithm;

public class Main {

	public static final int		GENERATE			= 1;
	public static final int		GEN_AND_STORE		= 2;
	public static final int		RESTORE				= 4;
	
	public static final int		NUM_OF_PROBLEMS		= 50;
	public static final long	RANDOM_SEED			= 17;
	public static final int		NUM_OF_VARIABLES	= 15;
	public static final int		NUM_OF_VALUES		= 10;
	
	public static final double	P1_MIN				= 0.2;
	public static final double	P1_MAX				= 0.8;
	public static final double	P1_DELTA			= 0.1;
	
	public static final double	P2_MIN				= 0.1;
	public static final double	P2_MAX				= 0.9;
	public static final double	P2_DELTA			= (P2_MAX-P2_MIN)/(NUM_OF_PROBLEMS/7);

	
	public static void main(String[] args) throws Exception {

		Vector<Problem> problems = getProblems(GENERATE);
		
		System.out.println("Finished Generating Problems, Starts solving..");

		CSPAlgorithm FCCBJAlgorithm = new FCCBJAlgorithm();		
		CSPAlgorithm FCCBJDACAlgorithm = new FCCBJDACAlgorithm();
		
		int i = 0;
		
		for (Problem p: problems){

			System.out.println("\nPROBLEM " + i++ + ": " + p);
			
			FCCBJAlgorithm.solve(p);
			System.out.println(p.printSolution());	
			
			FCCBJDACAlgorithm.solve(p);
			System.out.println(p.printSolution());
		}
	}

	private static Vector<Problem> getProblems(int how) {

		Vector<Problem> problems = null;
		DataManager dataManager = new DataManager();
		
		switch (how){
		
			case GENERATE:
				
				problems = generateProblems();
				break;
		
			case GEN_AND_STORE:
				
				problems = generateProblems();
				dataManager.storeProblems(problems, "problems");
				break;
				
			case RESTORE:
				
				problems = dataManager.restoreProblems("problems");
				break;
				
			default: break;
		}

		return problems;
	}

	private static Vector<Problem> generateProblems() {

		Random random = new Random(RANDOM_SEED);
		
		Vector<Problem> problems = new Vector<Problem>();
		
		problems.add(new NQueensProblem(10));
		 
		for (double p1 = P1_MIN; p1 <= P1_MAX; p1 += P1_DELTA)
			for (double p2 = P2_MIN; p2 <= P2_MAX; p2 += P2_DELTA)
				problems.add(new Problem(NUM_OF_VARIABLES, NUM_OF_VALUES, p1, p2, random));
		
		return problems;
	}
}
