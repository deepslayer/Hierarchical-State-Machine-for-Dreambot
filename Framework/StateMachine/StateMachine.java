package Framework.StateMachine;

import Framework.Interface.State;
import Framework.Interface.ValidatableState;

import java.util.ArrayList;
import java.util.List;

public class StateMachine {
    private final List<State> states = new ArrayList<>(); // List of states managed by the state machine
    private State currentState; // The current active state

    /**
     * Adds a new state to the state machine.
     *
     * @param state The state to be added.
     */
    public void addState(State state) {
        states.add(state);
    }

    /**
     * Starts the state machine by finding and entering the first valid state.
     */
    /**
     * Starts the state machine by finding and entering the first valid state.
     */
    public void start() {
        // Find the first valid state
        for (State state : states) {
            // Only check validity if the state implements ValidatableState
            if (state instanceof ValidatableState && ((ValidatableState) state).isValid()) {
                currentState = state;
                currentState.enter();  // Enter the first valid state
                return;
            }
            // Explicitly check for ActionState (or any other non-validatable states)
            else if (state instanceof ActionState) {
                currentState = state;
                currentState.enter();  // Enter the ActionState directly
                return;
            }
        }

        // No valid state was found at startup
        // Log could be added here if necessary to track startup failure
    }


    /**
     * Updates the current state of the state machine. If the current state is
     * complete, it transitions to the next valid state.
     */
    public void update() {
        if (currentState != null && !currentState.isComplete()) {
            currentState.execute();  // Keep executing the current state
        } else {
            transitionToNextValidState();  // Move to the next valid state
        }
    }

    /**
     * Transitions from the current state to the next valid state.
     * If no valid states are found, the state machine stops.
     */
    /**
     * Transitions from the current state to the next valid state.
     * If no valid states are found, the state machine stops.
     */
    private void transitionToNextValidState() {
        if (currentState != null) {
            currentState.exit();  // Exit the current state
        }

        // Find the next valid state
        for (State state : states) {
            // If the state is a ValidatableState, check its isValid() method
            if (state instanceof ValidatableState && ((ValidatableState) state).isValid()) {
                currentState = state;
                currentState.enter();  // Enter the valid state
                return;
            }

            // Explicitly handle ActionState (or other non-validatable types)
            if (state instanceof ActionState) {
                currentState = state;
                currentState.enter();  // Enter the ActionState directly
                return;
            }
        }

        // No valid state was found, stop the state machine
        currentState = null;  // No more valid states, state machine stops running
    }



    /**
     * Checks if the state machine is still running (i.e., has an active state).
     *
     * @return true if the state machine has a valid state, false otherwise.
     */
    public boolean isRunning() {
        return currentState != null;
    }
}
