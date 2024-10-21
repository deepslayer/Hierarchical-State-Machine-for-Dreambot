package Framework.StateMachine;

import Framework.Interface.State;
import Framework.Interface.ValidatableState;

import java.util.ArrayList;
import java.util.List;

public abstract class SequenceState extends ActionState implements ValidatableState {
    private final List<State> substates = new ArrayList<>();  // List of substates that make up the sequence
    private State currentSubstate;  // The currently active substate being executed
    private int currentSubstateIndex = 0;  // Keeps track of which substate is currently being executed

    public SequenceState(StateMachine machine) {
        super(machine);
    }

    /**
     * Adds a substate to the SequenceState. These substates will be executed sequentially.
     *
     * @param substate The substate to add (could be ActionState, DecisionState, or another SequenceState).
     */
    public void addSubState(State substate) {
        substates.add(substate);
    }

    /**
     * Called when the SequenceState is entered. It will initialize the sequence by setting the first substate
     * as the current substate and call its enter() method.
     */
    @Override
    public void enter() {
        if (!substates.isEmpty()) {
            currentSubstateIndex = 0;  // Start at the first substate
            currentSubstate = substates.get(currentSubstateIndex);
            currentSubstate.enter();  // Enter the first substate
            resetCompletion();  // Reset the complete flag when entering the sequence

        }
    }

    /**
     * Executes the current substate. If the current substate completes, it will proceed to the next one
     * until all substates have been executed.
     */
    @Override
    public void execute() {
        if (currentSubstate != null) {
            // Check if the current substate is not complete, then execute it
            if (!currentSubstate.isComplete()) {
                currentSubstate.execute();
            } else {
                // If current substate is complete, exit it and move to the next one
                currentSubstate.exit();
                currentSubstateIndex++;

                // Check if there are more substates to execute
                if (currentSubstateIndex < substates.size()) {
                    currentSubstate = substates.get(currentSubstateIndex);
                    currentSubstate.enter();  // Enter the next substate
                } else {
                    markComplete();  // Mark the sequence as complete when all substates are done
                }
            }
        }
    }

    /**
     * Called when the SequenceState is exited. It will also exit the current substate if it is still running.
     */
    @Override
    public void exit() {
        if (currentSubstate != null && !currentSubstate.isComplete()) {
            currentSubstate.exit();  // Exit the current substate if it hasn't finished yet
        }
    }

    /**
     * Checks if the SequenceState is complete. It is marked complete when all substates have been executed.
     *
     * @return true if all substates have been completed, false otherwise.
     */
    @Override
    public boolean isComplete() {
        return super.isComplete();  // Use inherited isComplete() from ActionState
    }

    /**
     * Abstract method that must be implemented by subclasses to determine if this SequenceState should
     * be considered for execution. This can be based on any game-specific conditions.
     *
     * @return true if the state is valid and should be executed, false otherwise.
     */
    @Override
    public abstract boolean isValid();
}
