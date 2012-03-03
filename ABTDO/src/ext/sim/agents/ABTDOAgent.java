package ext.sim.agents;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;

@Algorithm(name="ABTDO", useIdleDetector=true)
public class ABTDOAgent extends SimpleAgent {

    // Current order which is an ordered list of pairs.
    // Every pair includes the ID of one of the agents and a counter
    
    // Each agent can
    // propose a new order for agents that have lower priority, each time it replaces its
    // assignment.
    
    // The counters attached to each agent ID in the order list form a time-stamp.
    // Initially, all time-stamp counters are set to zero and all agents start with the same
    // Current Order.
    
    // Each agent Ai that proposes a new order, changes the order of the
    // pairs in its own ordered list and updates the counters as follows:
    // 1. The counters of agents with higher priority than Ai, according to the
    //    Current order, are not changed.
    // 2. The counter of Ai is incremented by one.
    // 3. The counters of agents with lower priority than Ai in the Current order are set
    //    to zero.



    @Override
    public void start() {
    	//TODO: ADD INITIALIZING CODE HERE - DO NOT INITIALIZE ANYTHING IN THE CONSTRACTOR!
        if (isFirstAgent()) {
            //TODO: KICK START THE ALGORITHM
        }
    }
}
