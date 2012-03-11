package ext.sim.modules;

import java.util.Random;
import java.util.Vector;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.impl.pgen.AbstractProblemGenerator;

@Register(name = "BarabasiAlbertProblem")
public class BarabasiAlbertProblem extends AbstractProblemGenerator {

	/*The network begins with an initial network of m0 nodes. m0 >= 2 and the degree of each node in the initial
	 *  network should be at least 1, otherwise it will always remain disconnected from the rest of the network.
	 *	New nodes are added to the network one at a time. Each new node is connected to M (constant) existing nodes with a 
	 *	probability that is proportional to the number of links that the existing nodes already have. Formally, 
	 *	the probability pi that the new node is connected to node i is (look in wiki for the formula)
	 *	where ki is the degree of node i and the sum is made over all preexisting nodes j. Heavily linked nodes 
	 *	("hubs") tend to quickly accumulate even more links, while nodes with only a few links are unlikely to be 
	 *	chosen as the destination for a new link. The new nodes have a "preference" to attach themselves to the already 
	 *	heavily linked nodes.
	 *	for more information: http://en.wikipedia.org/wiki/Barab%C3%A1si%E2%80%93Albert_model	*/
    
	
	@Variable(name = "n", description = "number of nodes (>=2)", defaultValue = "2")
    int n = 2;
	
	@Variable(name = "d", description = "agent domain size", defaultValue = "1")
    int d = 1;

//	@Variable(name = "degree", description = "initial degree of each nodes (>=1)", defaultValue = "1")
//    int degree = 1;

	@Variable(name = "M", description = "number of connection for each new node", defaultValue = "1")
    int M = 2;
	
	@Variable(name = "m0", description = "number of nodes in the initial network", defaultValue = "2")
    int m0 = 2;
	
	@Variable(name = "p2", description = "p(conflict) between 2 constrained vars", defaultValue="0.4")
    float p2 = 0.4f;
	
    Vector<Integer> initialNetworkNodes;
    Vector<Integer> unInitializeNodes;
    int sumOfDegrees = 0;

	private boolean symetric = true;
    
	private void createInitialNetwork(Problem p, Random rand){
		this.initialNetworkNodes = getInitialAgents(rand);
    	this.unInitializeNodes = new Vector<Integer>(this.getAllNodes());
    	this.unInitializeNodes.removeAll(this.initialNetworkNodes);
    	
		//creating the initial network as a ring. (must be a connectivity graph.)
		for (int i=0; i<this.initialNetworkNodes.size();i++) {
//			if(i==(n-1)){
//				continue;
//			}
			int firstAgent = this.initialNetworkNodes.elementAt(i);
			int secondAgent = this.initialNetworkNodes.elementAt((i+1)%this.m0);
			buildConstraint(firstAgent, secondAgent, p, rand);
			this.sumOfDegrees = this.sumOfDegrees + 2;
		}	
	}

	Vector<Integer> getAllNodes(){
		Vector<Integer> tmpAgentHolder = new Vector<Integer>();
		for(int i=0;i<n;i++){
			tmpAgentHolder.add(i);
		}
		return tmpAgentHolder;
	}
	
	private Vector<Integer> getInitialAgents(Random rand){
		Vector<Integer> ans = new Vector<Integer>();
		//creating a vector with all the agents for the usage of the random function;
		Vector<Integer> tmpAgentHolder = getAllNodes();
		for(int j=0;j<this.m0;j++){
			int agentIdIndex = (int)(rand.nextDouble() * tmpAgentHolder.size());
			int agentId = tmpAgentHolder.elementAt(agentIdIndex);
			tmpAgentHolder.remove(agentIdIndex);
			ans.add(agentId);	
		}
		return ans;
	}

	private int getDegreeOfNode(Problem p, int node){
		return p.getNeighbors(node).size();
	}
	
	private double getPropabilityForConnectionToNode(Problem p, int toNode){
		int nodeDegree = getDegreeOfNode(p, toNode);
		return (((double)nodeDegree) / (double)this.sumOfDegrees);
	}
	
    private void addNode(Problem p, Random rand, int toNode, int newNode) {
		//for (Integer otherNode : this.initialNetworkNodes) {
			if(rand.nextDouble() <= this.getPropabilityForConnectionToNode(p, toNode)){
				buildConstraint(toNode, newNode, p, rand);
				this.sumOfDegrees = this.sumOfDegrees + 2;
			}
		//}
	}
    
    private Vector<Integer> getMRandomNodesFromInitializedNodes(Random rand){
    	int number = (this.M < this.initialNetworkNodes.size() ? this.initialNetworkNodes.size() : this.M);
    	Vector<Integer> ans = new Vector<Integer>();
		Vector<Integer> tmpAgentHolder = new Vector<Integer>(this.initialNetworkNodes);
		for(int j=0;j<number;j++){
			int agentIdIndex = (int)(rand.nextDouble() * tmpAgentHolder.size());
			int agentId = tmpAgentHolder.elementAt(agentIdIndex);
			tmpAgentHolder.remove(agentIdIndex);
			ans.add(agentId);	
		}
		return ans;
    }
    
    @Override
    public void generate(Problem p, Random rand) {
        p.initialize(ProblemType.DCSP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
    	this.createInitialNetwork(p, rand);
    	while(!this.unInitializeNodes.isEmpty()){
    		int newNode = this.unInitializeNodes.remove(0);
    		Vector<Integer> nodesToConnectTo = this.getMRandomNodesFromInitializedNodes(rand);
    		for (Integer nodeToConnectTo : nodesToConnectTo) {
    			addNode(p, rand, nodeToConnectTo, newNode);
			}
    		this.initialNetworkNodes.add(newNode);
    	}
    }
    


	private void buildConstraint(int i, int j, Problem p, Random rand) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                if (i == j) {
                    continue;
                }
                if (rand.nextDouble() < p2) {
                    final int cost = 1;
                    p.setConstraintCost(i, vi, j, vj, cost);  
                    if (symetric ) {
                        p.setConstraintCost(j, vj, i, vi, cost);
                    }
                }
            }
        }
    }

    
    
}
