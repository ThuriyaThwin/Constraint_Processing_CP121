package main;

import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

import problem.NQueensProblem;
import problem.Problem;
import problem.ProblemsSetStats;

import algoritm.CBJAlgorithm;
import algoritm.CSPAlgorithm;
import algoritm.FCCBJAlgorithm;
import algoritm.FCCBJDACAlgorithm;

public class Main {

	public static final int		NUM_OF_PROBLEMS		= 50;
	public static final long	RANDOM_SEED			= 17;
	public static final int		NUM_OF_VARIABLES	= 15;
	public static final int		NUM_OF_VALUES		= 10;
	
	public static final double	P1_MIN				= 0.2;
	public static final double	P1_MAX				= 0.8;
	public static final double	P1_DELTA			= 0.3;
	
	public static final double	P2_MIN				= 0.1;
	public static final double	P2_MAX				= 0.9;
	public static final double	P2_DELTA			= 0.1;

	
	public static void main(String[] args) throws Exception {
	
//		Random random = new Random(RANDOM_SEED);
//		
//		PrintWriter out = new PrintWriter("report.txt");
//		
//		for (double p1 = P1_MIN; p1 <= P1_MAX; p1 += P1_DELTA){
//
//			for (double p2 = P2_MIN; p2 <= P2_MAX; p2 += P2_DELTA){
//
//				out.append("P1=" + p1 + ", P2=" + p2 + ":\n");
//				out.append(solveProblems(createProblems(p1, p2, random), out, false) + "\n");
//			}
//		}
//		
//		out.close();
		
		PrintWriter out2 = new PrintWriter("queens.txt");
		
		Vector<Problem> problems = new Vector<Problem>(24);
		
		for (int i = 2; i <= 25; i++)
			problems.add(new NQueensProblem(i));
		
		out2.append(solveProblems(problems, out2, true) + "\n");
		
		out2.close();
	}

	private static Vector<Problem> createProblems(double p1, double p2, Random random) {
		
		Vector<Problem> problems = new Vector<Problem>(NUM_OF_PROBLEMS);
		
		for (int i = 0; i < NUM_OF_PROBLEMS; i++)
			problems.add(new Problem(NUM_OF_VARIABLES, NUM_OF_VALUES, p1, p2, random));
			
		return problems;		
	}
	

	private static ProblemsSetStats solveProblems(Vector<Problem> problems,
			PrintWriter out, boolean debug) throws Exception {

		CSPAlgorithm CBJAlgorithm = new CBJAlgorithm();
		CSPAlgorithm FCCBJAlgorithm = new FCCBJAlgorithm();		
		CSPAlgorithm FCCBJDACAlgorithm = new FCCBJDACAlgorithm();
		
		int FCCBJAssignments = 0;
		int FCCBJDACAssignments = 0;
		
		int FCCBJCCs = 0;
		int FCCBJDACCCs = 0;
		
		StringBuffer debugSB = new StringBuffer();
		
		for (Problem p: problems){

			debugSB.append("PROBLEM: " + p + "\n");
				
			CBJAlgorithm.solve(p);
			debugSB.append(p.printSolution() + "\n");

			FCCBJAlgorithm.solve(p);
			FCCBJAssignments += p.getAssignments();
			FCCBJCCs += p.getCCs();
			debugSB.append(p.printSolution() + "\n");
			
			FCCBJDACAlgorithm.solve(p);
			FCCBJDACAssignments += p.getAssignments();
			FCCBJDACCCs += p.getCCs();
			debugSB.append(p.printSolution() + "\n");
		}
		
		if (debug) System.out.println(debugSB.toString());
		
		return new ProblemsSetStats(
				FCCBJAssignments, FCCBJDACAssignments, FCCBJCCs, FCCBJDACCCs);
	}
}
