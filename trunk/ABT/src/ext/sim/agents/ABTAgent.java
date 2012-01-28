package ext.sim.agents;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;

@Algorithm(name="ABT", useIdleDetector=false)
public class ABTAgent extends SimpleAgent {

    @Override
    public void start() {
    	//TODO: ADD INITIALIZING CODE HERE - DO NOT INITIALIZE ANYTHING IN THE CONSTRACTOR!
        if (isFirstAgent()) {
            //TODO: KICK START THE ALGORITHM
        }
    }

    /* This is an example of message handling.
     * With Simple Agent you are not required to define any message classes,
     * All that required is to define the actions to take when a message, with some name, arrives and what fields it should contain.  
     * @WhenReceived("MESSAGE_NAME")
     * public void handleCPA(Type1 field1, Type2 field2, ..., TypeN fieldN) {
 	 * 		Handling code... 
     * }
     * 
     * when you want to send a message all you have to do is call: 
     * send("MESSAGE_NAME", field1, field2, ..., fieldN).to(RECEIVING_AGENT_ID);
     */
}
