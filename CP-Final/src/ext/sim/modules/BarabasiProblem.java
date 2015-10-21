package ext.sim.modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.impl.pgen.AbstractProblemGenerator;

@Register(name = "BarabasiProblem")
public class BarabasiProblem extends AbstractProblemGenerator{

	@Variable(name = "n", description = "number of nodes (>=2)", defaultValue = "2")
	int n = 2;

	@Variable(name = "d", description = "agent domain size", defaultValue = "1")
	int d = 1;

	@Variable(name = "M", description = "number of linked nodes for each new node", defaultValue = "1")
	int M = 2;

	@Variable(name = "m0", description = "number of nodes in the initial network", defaultValue = "2")
	int m0 = 2;
	
	@Variable(name = "q", description = "Probability to connect to a node,can be rounded smaller", defaultValue = "0.5")
	float q = 0.5f;

	@Variable(name = "p2", description = "probability of constarint between 2 constrained vars", defaultValue="0.4")
	float p2 = 0.4f;

	Vector<Integer> initialNetworkNodes;
	Vector<Integer> unInitializedNodes;
	HashMap<Integer, Integer> NodeDegree;
	int sumOfDegrees = 0;
	private int numberOfConnectionsToStorngNode = 0;
	private boolean symetric = true;

	private boolean initInitialNetwork(Problem problem,Random random){
		this.initialNetworkNodes = createInitialAgents(random);
		this.unInitializedNodes = createAllAgents();
		this.unInitializedNodes.removeAll(this.initialNetworkNodes);
		this.NodeDegree = new HashMap<Integer, Integer>();
		//We will create the intial network as a thread
		for (int i = 0; i < this.initialNetworkNodes.size(); i++) {
			int counter = 0;
			int delta = (int) (random.nextDouble() * this.initialNetworkNodes.size()) + 1;//(int) ((this.m0 * 2)/3);
			delta = (delta ==  this.initialNetworkNodes.size()) ? (delta - 1) : delta;
			while(counter < delta){
				int j = (int) (random.nextDouble() * this.initialNetworkNodes.size());
				while(j == i){
					j = (int) (random.nextDouble() * this.initialNetworkNodes.size());
				}
				int tFirstNode = this.initialNetworkNodes.get(i);
				int tSecondNode = this.initialNetworkNodes.get(j);
				if(!problem.isConstrained(tFirstNode, tSecondNode)){
					connectNodes(tFirstNode,tSecondNode,problem,random);
					this.sumOfDegrees = this.sumOfDegrees + 2;
					counter++;
				}
				else{
					counter++;
				}
			}
		}
		for (int i = 0;i < this.n; i++) {
			this.NodeDegree.put(i,problem.getNeighbors(i).size());
		}
		if(true){
			System.out.println("ShowTime1");
		}
		
		return true;
	}

	private Vector<Integer> createInitialAgents(Random random) {
		Vector<Integer> tAns = new Vector<Integer>();
		Vector<Integer> tAllAgents = createAllAgents();
		for (int i = 0; i < this.m0; i++) {
			int tAgentInd = (int)(random.nextDouble() * tAllAgents.size());
			int tAgent = tAllAgents.get(tAgentInd);
			tAllAgents.remove(tAgentInd);
			tAns.add(tAgent);
		}
		return tAns;
	}
	
	//Tested.
	private Vector<Integer> createAllAgents(){

		Vector<Integer> tAns = new Vector<Integer>();
		for (int i = 0; i < n; i++) {
			tAns.add(i);
		}
		return tAns;
	}

	private void connectNodes(int firstNode, int secondNode,
			Problem problem, Random random)  {
		for (int vi = 0; vi < problem.getDomain().size(); vi++) {
			for (int vj = 0; vj < problem.getDomain().size(); vj++) {
				if (firstNode == secondNode) {
					continue;
				}
				if (random.nextDouble() < p2) {
					final int cost = 1;
					problem.setConstraintCost(firstNode, vi, secondNode, vj, cost);  
					if (symetric) {
						problem.setConstraintCost(secondNode, vj, firstNode, vi, cost);
					}
				}
			}
		}
	}

	private void addNode(Problem problem, Random random, int tNodeSrc,
			int tNodeDst) {
		if((random.nextDouble() * q) <= getPropabilityForConnectionToNode(problem, tNodeDst)){
			int tdegree1Before = problem.getNeighbors(tNodeSrc).size();
			int tdegree2Before = problem.getNeighbors(tNodeDst).size();
			connectNodes(tNodeSrc, tNodeDst, problem, random);
			int tdegree1After = problem.getNeighbors(tNodeSrc).size();
			int tdegree2After = problem.getNeighbors(tNodeDst).size();
			if(tdegree2After > (this.n/3)){
				System.out.println("Connected to a strong componenet");
				numberOfConnectionsToStorngNode++;
			}
			this.NodeDegree.put(tNodeSrc, problem.getNeighbors(tNodeSrc).size());
			this.NodeDegree.put(tNodeDst, problem.getNeighbors(tNodeDst).size());
			this.sumOfDegrees = this.sumOfDegrees + 2;
		}
	}

	private double getPropabilityForConnectionToNode(Problem problem,
			Integer tNodeDst) {
		int nodeDegree = getDegreeOfNode(problem, tNodeDst);
		double tAns = (((double)nodeDegree) / (double)this.sumOfDegrees);
		return tAns;
	}

	private int getDegreeOfNode(Problem problem, Integer tNodeDst) {
		
		return problem.getNeighbors(tNodeDst).size();
	}
	//Tested - o.k
	private Vector<Integer> getNeighborsForNode(Random random) {

		int number = (this.M < this.initialNetworkNodes.size() ? this.initialNetworkNodes.size() : this.M);
		Vector<Integer> ans = new Vector<Integer>();
		Vector<Integer> tmpAgentHolder = new Vector<Integer>(this.initialNetworkNodes);
		for(int i = 0;i < number; i++){
			int agentIdIndex = (int)(random.nextDouble() * tmpAgentHolder.size());
			int agentId = tmpAgentHolder.elementAt(agentIdIndex);
			tmpAgentHolder.remove(agentIdIndex);
			ans.add(agentId);	
		}
		return ans;
	}

	private void initAddedNodes(Problem problem, Random random) {

		while(!this.unInitializedNodes.isEmpty()){
			int tNodeSrc = this.unInitializedNodes.remove(0);
			Vector<Integer> tNodesToConnectToNode = getNeighborsForNode(random);
			for (Integer tNodeDst : tNodesToConnectToNode) {
				addNode(problem,random,tNodeSrc,tNodeDst);
			}
			this.initialNetworkNodes.add(tNodeSrc);
		}
		
	}
	
	@Override
	public void generate(Problem problem, Random random) {
		problem.initialize(ProblemType.DCSP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
		initInitialNetwork(problem,random);
		initAddedNodes(problem,random);
		
		if(true){
			System.out.println("Success + numberOfConnectionsToStorngNode is " + numberOfConnectionsToStorngNode);
		}
	}


}
