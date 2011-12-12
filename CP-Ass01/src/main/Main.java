package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

import problem.NQueensProblem;
import problem.Problem;
import problem.ProblemsSetStats;

import algoritm.BTAlgorithm;
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
	
	public static final int		ALL					= 0;
	public static final int		ONLY_UNSOLVED		= 1;
	public static final int		ONLY_SOLVED			= 2;

	public static void main(String[] args) throws Exception {
	
		randomProblemsTests("report.txt", ALL);
		randomProblemsTests("report_unsolved.txt", ONLY_SOLVED);
		randomProblemsTests("report_unsolved.txt", ONLY_UNSOLVED);
		nQueensTests();
	}

	private static void randomProblemsTests(String fileName, int problemsReportType)
			throws FileNotFoundException, Exception {
		
		Random random = new Random(RANDOM_SEED);
		
		PrintWriter out = new PrintWriter(fileName);
		
		for (double p1 = P1_MIN; p1 <= P1_MAX; p1 += P1_DELTA){

			for (double p2 = P2_MIN; p2 <= P2_MAX; p2 += P2_DELTA){

				out.append("P1=" + p1 + ", P2=" + p2 + ":\n");
				out.append(solveProblems(createProblems(p1, p2, random),
						false, false, problemsReportType) + "\n");
			}
		}
		
		out.close();
	}

	private static void nQueensTests() throws FileNotFoundException, Exception {
		
		PrintWriter out = new PrintWriter("queens.txt");
		
		Vector<Problem> problems = new Vector<Problem>(24);
		
		for (int i = 2; i <= 25; i++)
			problems.add(new NQueensProblem(i));
		
		ProblemsSetStats result = solveProblems(problems, true, false, ALL);

		for (int i = 2; i <= 25; i++){
			
			out.append(problems.get(i-2).toString() + "\n");
			out.append("FCCBJ Assignments = " + result.getFCCBJAssignmentsVec().get(i-2) + "\n");
			out.append("FCCBJDAC Assignments = " + result.getFCCBJDACAssignmentsVec().get(i-2) + "\n");
			out.append("FCCBJ CCs = " + result.getFCCBJCCsVec().get(i-2) + "\n");
			out.append("FCCBJDAC CCs = " + result.getFCCBJDACCCsVec().get(i-2) + "\n\n");
		}
		
		out.close();
	}

	private static Vector<Problem> createProblems(double p1, double p2, Random random) {
		
		Vector<Problem> problems = new Vector<Problem>(NUM_OF_PROBLEMS);
		
		for (int i = 0; i < NUM_OF_PROBLEMS; i++)
			problems.add(new Problem(NUM_OF_VARIABLES, NUM_OF_VALUES, p1, p2, random));
			
		return problems;		
	}
	

	private static ProblemsSetStats solveProblems(Vector<Problem> problems,
			boolean debug, boolean btAndCBJ, int problemsReportType) throws Exception {

		CSPAlgorithm BTAlgorithm = new BTAlgorithm();
		CSPAlgorithm CBJAlgorithm = new CBJAlgorithm();
		CSPAlgorithm FCCBJAlgorithm = new FCCBJAlgorithm();		
		CSPAlgorithm FCCBJDACAlgorithm = new FCCBJDACAlgorithm();
		
		StringBuffer debugSB = new StringBuffer();
		
		ProblemsSetStats solvedStats = new ProblemsSetStats();
		ProblemsSetStats unsolvedStats = new ProblemsSetStats();
		ProblemsSetStats allStats = new ProblemsSetStats();
		
		int solvedSize = problems.size();
		int unsolvedSize = problems.size();
		
		allStats.setNumOfProblems(problems.size());
		
		for (Problem p: problems){

			debugSB.append("PROBLEM: " + p + "\n");
			
			if (btAndCBJ){
				
				BTAlgorithm.solve(p);
				if (!p.isSolved()) debugSB.append("UNSOLVED: ");
				debugSB.append(p.printSolution() + "\n");
				
				CBJAlgorithm.solve(p);
				if (!p.isSolved()) debugSB.append("UNSOLVED: ");
				debugSB.append(p.printSolution() + "\n");
			}

			FCCBJAlgorithm.solve(p);
			
			if (p.isSolved()){
				
				solvedStats.addFCCBJAssignments(p.getAssignments());
				solvedStats.addFCCBJCCs(p.getCCs());
				unsolvedSize--;
			}
			else{
				
				unsolvedStats.addFCCBJAssignments(p.getAssignments());
				unsolvedStats.addFCCBJCCs(p.getCCs());
				solvedSize--;
				
				debugSB.append("UNSOLVED: ");
			}
			
			allStats.addFCCBJAssignments(p.getAssignments());
			allStats.addFCCBJCCs(p.getAssignments());

			debugSB.append(p.printSolution() + "\n");
			
			FCCBJDACAlgorithm.solve(p);
			
			if (p.isSolved()){
				
				solvedStats.addFCCBJDACAssignments(p.getAssignments());
				solvedStats.addFCCBJDACCCs(p.getCCs());
				unsolvedSize--;
			}
			else{
				
				unsolvedStats.addFCCBJDACAssignments(p.getAssignments());
				unsolvedStats.addFCCBJDACCCs(p.getCCs());
				solvedSize--;
				
				debugSB.append("UNSOLVED: ");
			}
			
			allStats.addFCCBJDACAssignments(p.getAssignments());
			allStats.addFCCBJDACCCs(p.getAssignments());

			debugSB.append(p.printSolution() + "\n");
		}
		
		solvedStats.setNumOfProblems(solvedSize);
		unsolvedStats.setNumOfProblems(unsolvedSize);
		
		if (debug) System.out.println(debugSB.toString());
		
		switch (problemsReportType){
		
			case ONLY_SOLVED: return solvedStats;
			case ONLY_UNSOLVED: return unsolvedStats;
			
			default: return allStats;
		}
	}
}
