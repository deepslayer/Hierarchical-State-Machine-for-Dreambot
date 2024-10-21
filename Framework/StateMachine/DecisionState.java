package Framework.StateMachine;

import Framework.Interface.State;
import Framework.Interface.ValidatableState;

import java.util.ArrayList;
import java.util.List;

import static org.dreambot.api.utilities.Logger.log;

public abstract class DecisionState extends ActionState implements ValidatableState {
    private final List<State> decisionSubstates = new ArrayList<>();
    private State currentSubstate;

    public DecisionState(StateMachine machine) {
        super(machine);
    }

    /**
     * Adds a substate to the DecisionState. These substates will be checked in order
     * to find the first valid substate to execute.
     *
     * @param state The substate to add (could be another DecisionState or SequenceState).
     */
    public void addSubState(State state) {
        decisionSubstates.add(state);
    }

    /**
     * Called when entering the DecisionState. This method will search for the first
     * valid substate to run by calling findNextValidSubstate().
     */
    @Override
    public void enter() {
        log("Entering DecisionState: " + this.getClass().getSimpleName());
        findNextValidSubstate();
    }

    /**
     * Continuously executes the current valid substate until it is marked as complete.
     * Once the substate is complete, it will exit and search for the next valid substate.
     */
    @Override
    public void execute() {
        if (currentSubstate != null) {
            if (!currentSubstate.isComplete()) {
                currentSubstate.execute();
            } else {
                currentSubstate.exit();
                findNextValidSubstate();
            }
        }
    }

    /**
     * Called when exiting the DecisionState. It will also exit the current substate
     * if one is still running.
     */
    @Override
    public void exit() {
        log("Exiting DecisionState: " + this.getClass().getSimpleName());
        if (currentSubstate != null) {
            currentSubstate.exit();
        }
    }

    /**
     * Determines whether the DecisionState is complete. A DecisionState is marked
     * complete when there are no valid substates left to execute.
     *
     * @return true if no valid substates remain, otherwise false.
     */
    @Override
    public boolean isComplete() {
        return currentSubstate == null;
    }

    /**
     * Abstract method to be implemented by subclasses. Determines if the
     * DecisionState itself is valid and should be considered for execution.
     *
     * @return true if the DecisionState is valid, otherwise false.
     */
    @Override
    public abstract boolean isValid();

    /**
     * Helper method to find the next valid substate in the decisionSubstates list.
     * If a valid substate is found, it will become the currentSubstate and its enter() method
     * will be called.
     */
    private void findNextValidSubstate() {
        // Reset current substate to null before searching for the next valid substate.
        currentSubstate = null;

        // Iterate over each substate and check if it's valid if it's a ValidatableState.
        for (State substate : decisionSubstates) {
            if (substate instanceof ValidatableState) {
                // For ValidatableState, check isValid() before setting it as currentSubstate
                if (((ValidatableState) substate).isValid()) {
                    currentSubstate = substate;
                    currentSubstate.enter();  // Enter the valid substate.
                    return;
                }
            } else {
                // If it's not a ValidatableState (e.g., ActionState), assume it's valid.
                currentSubstate = substate;
                currentSubstate.enter();  // Enter the state without validation.
                return;
            }
        }

        // If no valid substate was found, log that the DecisionState has no valid substates.
        log("No valid substates found for DecisionState: " + this.getClass().getSimpleName());
    }

}
