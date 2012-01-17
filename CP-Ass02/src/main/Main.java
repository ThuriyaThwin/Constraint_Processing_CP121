package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;
import java.util.Vector;

import problem.COPProblem;
import problem.MaxCSPProblem;
import problem.NQueensProblem;
import problem.Problem;
import problem.ProblemsSetStats;

import algorithm.Algorithm;
import algorithm.BnB;
import algorithm.BnBIC;
import algorithm.BnBICDAC;


public class Main {

	public static final int		NUM_OF_PROBLEMS		= 50;
	public static final long	RANDOM_SEED			= 1717;
	public static final int		NUM_OF_VARIABLES	= 15;
	public static final int		NUM_OF_VALUES		= 10;
	
	public static final double	P1_MIN				= 0.2;
	public static final double	P1_MAX				= 0.8;
	public static final double	P1_DELTA			= 0.3;
	
	public static final double	P2_MIN				= 0.1;
	public static final double	P2_MAX				= 0.9;
	public static final double	P2_DELTA			= 0.1;
	
	public static final int		ALL					= 0;
	public static final int		ONLY_UNSOLVED		= 1;
	public static final int		ONLY_SOLVED			= 2;
	
	public static final int		MC					= 0;	//TODO

	public static void main(String[] args) throws Exception {
	
//		nQueensTests();
		randomProblemsTests("report.txt", ALL);
//		randomProblemsTests("report_solved.txt", ONLY_SOLVED);
//		randomProblemsTests("report_unsolved.txt", ONLY_UNSOLVED);
	}

//	private static void nQueensTests() throws FileNotFoundException, Exception {
//		
//		PrintWriter out = new PrintWriter("queens.txt");
//		
//		Vector<Problem> problems = new Vector<Problem>(24);
//		
////		for (int i = 2; i <= 25; i++)
////			problems.add(new NQueensProblem(i));
//		
//		problems.add(new NQueensProblem(10));
//		
//		ProblemsSetStats result = solveProblems(problems, true, true, ALL);
//
////		for (int i = 2; i <= 25; i++){
////			
////			out.append(problems.get(i-2).toString() + "\n");
////			out.append("FCCBJ Assignments = " + result.getFCCBJAssignmentsVec().get(i-2) + "\n");
////			out.append("FCCBJDAC Assignments = " + result.getFCCBJDACAssignmentsVec().get(i-2) + "\n");
////			out.append("FCCBJ CCs = " + result.getFCCBJCCsVec().get(i-2) + "\n");
////			out.append("FCCBJDAC CCs = " + result.getFCCBJDACCCsVec().get(i-2) + "\n\n");
////		}
//		
//		out.close();
//	}
	
	private static void randomProblemsTests(String fileName, int problemsReportType)
			throws FileNotFoundException, Exception {

		Random random = new Random(RANDOM_SEED);
		
		PrintWriter out = new PrintWriter(fileName);
		
		for (double p1 = P1_MIN; p1 <= P1_MAX; p1 += P1_DELTA){
		
			for (double p2 = P2_MIN; p2 <= P2_MAX; p2 += P2_DELTA){
		
				out.append("P1=" + p1 + ", P2=" + p2 + ":\n");
				out.append(solveProblems(createMaxCSPProblems(p1, p2, random),
						true, false, problemsReportType) + "\n");
			}
		}
		
		out.close();
	}

	private static Vector<Problem> createMaxCSPProblems(double p1, double p2, Random random) {
		
		Vector<Problem> problems = new Vector<Problem>(NUM_OF_PROBLEMS);
		
		for (int i = 0; i < NUM_OF_PROBLEMS; i++)
			problems.add(new MaxCSPProblem(NUM_OF_VARIABLES, NUM_OF_VALUES, p1, p2, random));
			
		return problems;		
	}
	
//	private static Vector<Problem> createCOPProblems(double p1, double p2, Random random) {
//		
//		Vector<Problem> problems = new Vector<Problem>(NUM_OF_PROBLEMS);
//		
//		for (int i = 0; i < NUM_OF_PROBLEMS; i++)
//			problems.add(new COPProblem(NUM_OF_VARIABLES, NUM_OF_VALUES, p1, p2, MC, random));
//			
//		return problems;		
//	}

	private static ProblemsSetStats solveProblems(Vector<Problem> problems,
			boolean debug, boolean bnb, int problemsReportType) throws Exception {

		Algorithm BnB = new BnB();
		Algorithm BnBIC = new BnBIC();		
		Algorithm BnBICDAC = new BnBICDAC();
		
		StringBuffer debugSB = new StringBuffer();
		
		ProblemsSetStats solvedStats = new ProblemsSetStats();
		ProblemsSetStats unsolvedStats = new ProblemsSetStats();
		ProblemsSetStats allStats = new ProblemsSetStats();
		
		for (Problem p: problems){

			debugSB.append("PROBLEM: " + p + "\n");
			
			if (bnb){
				
				BnB.solve(p);
				if (!p.isSolved()) debugSB.append("UNSOLVED: ");
				debugSB.append(p.printSolution() + "\n");
			}

			BnBIC.solve(p);
			
			if (p.isSolved()){
				
				solvedStats.addFCCBJAssignments(new BigInteger(String.valueOf(p.getAssignments())));
				solvedStats.addFCCBJCCs(new BigInteger(String.valueOf(p.getCCs())));
			}
			else{
				
				unsolvedStats.addFCCBJAssignments(new BigInteger(String.valueOf(p.getAssignments())));
				unsolvedStats.addFCCBJCCs(new BigInteger(String.valueOf(p.getCCs())));
				
				debugSB.append("UNSOLVED: ");
			}
			
			allStats.addFCCBJAssignments(new BigInteger(String.valueOf(p.getAssignments())));
			allStats.addFCCBJCCs(new BigInteger(String.valueOf(p.getCCs())));

			debugSB.append(p.printSolution() + "\n");
			
			BnBICDAC.solve(p);
			
			if (p.isSolved()){
				
				solvedStats.addFCCBJDACAssignments(new BigInteger(String.valueOf(p.getAssignments())));
				solvedStats.addFCCBJDACCCs(new BigInteger(String.valueOf(p.getCCs())));
			}
			else{
				
				unsolvedStats.addFCCBJDACAssignments(new BigInteger(String.valueOf(p.getAssignments())));
				unsolvedStats.addFCCBJDACCCs(new BigInteger(String.valueOf(p.getCCs())));
				
				debugSB.append("UNSOLVED: ");
			}
			
			allStats.addFCCBJDACAssignments(new BigInteger(String.valueOf(p.getAssignments())));
			allStats.addFCCBJDACCCs(new BigInteger(String.valueOf(p.getCCs())));

			debugSB.append(p.printSolution() + "\n");
		}
		
		if (debug) System.out.println(debugSB.toString());
		
		switch (problemsReportType){
		
			case ONLY_SOLVED: return solvedStats;
			case ONLY_UNSOLVED: return unsolvedStats;
			
			default: return allStats;
		}
	}
}
