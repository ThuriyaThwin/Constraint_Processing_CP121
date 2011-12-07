package main;

import java.io.PrintWriter;
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
	public static final double	P2_DELTA			= (P2_MAX-P2_MIN)/(NUM_OF_PROBLEMS);

	
	public static void main(String[] args) throws Exception {

		Vector<Vector<Problem>> problemsSets = getProblems(GENERATE);
		
		System.out.println("Finished Generating Problems, Starts solving..");

		CSPAlgorithm FCCBJAlgorithm = new FCCBJAlgorithm();		
		CSPAlgorithm FCCBJDACAlgorithm = new FCCBJDACAlgorithm();
		
		PrintWriter out = new PrintWriter("report.txt");
		
		int i = 0;
		
		for (Vector<Problem> problems: problemsSets){
		
			int j = 0;
			
			System.out.println("\nPROBLEM SET" + i + ":");
			out.append("\nPROBLEM SET" + i++ + ":\n");
			
			for (Problem p: problems){
	
				System.out.println("\nPROBLEM " + j + ": " + p);
				out.append("\nPROBLEM " + j++ + ": " + p + "\n");
				
				FCCBJAlgorithm.solve(p);
				System.out.println(p.printSolution());	
				out.append(p.printSolution() + "\n");
				
				FCCBJDACAlgorithm.solve(p);
				System.out.println(p.printSolution());
				out.append(p.printSolution() + "\n");
			}
		}
		
		out.close();
	}

	private static Vector<Vector<Problem>> getProblems(int how) {

		Vector<Vector<Problem>> problemsSets = null;
		DataManager dataManager = new DataManager();
		
		switch (how){
		
			case GENERATE:
				
				problemsSets = generateProblems();
				break;
		
			case GEN_AND_STORE:
				
				problemsSets = generateProblems();
				dataManager.storeProblems(problemsSets, "problems");
				break;
				
			case RESTORE:
				
				problemsSets = dataManager.restoreProblems("problems");
				break;
				
			default: break;
		}

		return problemsSets;
	}

	private static Vector<Vector<Problem>> generateProblems() {

		Random random = new Random(RANDOM_SEED);
		
		Vector<Vector<Problem>> problemsSets = new Vector<Vector<Problem>>();
		
		Vector<Problem> problems = new Vector<Problem>();
		problems.add(new NQueensProblem(10));
		problemsSets.add(problems);
		 
		for (double p1 = P1_MIN; p1 <= P1_MAX; p1 += P1_DELTA){
			
			problems = new Vector<Problem>();
			
			for (double p2 = P2_MIN; p2 <= P2_MAX; p2 += P2_DELTA)
				problems.add(new Problem(NUM_OF_VARIABLES, NUM_OF_VALUES, p1, p2, random));
			
			problemsSets.add(problems);
		}

		return problemsSets;
	}
}
