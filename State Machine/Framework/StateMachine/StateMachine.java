package Framework.StateMachine;


import java.util.ArrayList;
import java.util.List;

import static org.dreambot.api.utilities.Logger.log;

public class StateMachine {
    private final List<State> states = new ArrayList<>();
    private State currentState;

    public void addState(State state) {
        states.add(state);
    }

    public void start() {
        // Find the first valid state
        for (State state : states) {
            if (state.isValid()) {
                currentState = state;
               // log("Starting state machine with state: " + currentState.getClass().getSimpleName());
                currentState.enter();  // Enter the first valid state
                return;
            }
        }

    //    log("No valid state found to start the state machine.");
    }

    public void update() {
        if (currentState != null && !currentState.isComplete()) {
            currentState.execute();
        } else {
            transitionToNextValidState();
        }
    }

    private void transitionToNextValidState() {
        if (currentState != null) {
        //    log("Exiting state: " + currentState.getClass().getSimpleName());
            currentState.exit();  // Exit the current state
        }



        // Find the next valid state
        for (State state : states) {
            if (state.isValid()) {
                currentState = state;
          //      log("Entering next valid state: " + currentState.getClass().getSimpleName());
                currentState.enter();  // Enter the first valid state
                return;
            }
        }

        // No valid state was found
    //    log("No valid state found. State machine stopping.");
        currentState = null;  // Stop the state machine
    }


    public boolean isRunning() {
        return currentState != null;
    }
}
