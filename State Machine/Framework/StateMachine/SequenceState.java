package Framework.StateMachine;

import java.util.ArrayList;
import java.util.List;

public abstract class SequenceState extends AbstractState {
    private final List<State> substates = new ArrayList<>();
    private State currentSubstate;
    private int currentSubstateIndex = 0;

    public SequenceState(StateMachine machine) {
        super(machine);
    }

    public void addSubState(State substate) {
        substates.add(substate);
    }

    @Override
    public void enter() {
        if (!substates.isEmpty()) {
            currentSubstateIndex = 0;
            currentSubstate = substates.get(currentSubstateIndex);
            currentSubstate.enter();
            complete = false;
        }
    }

    @Override
    public void execute() {
        if (currentSubstate != null) {
            if (!currentSubstate.isComplete()) {
                currentSubstate.execute();
            } else {
                currentSubstate.exit();
                currentSubstateIndex++;


                if (currentSubstateIndex < substates.size()) {
                    currentSubstate = substates.get(currentSubstateIndex);
                    currentSubstate.enter();
                } else {
                    complete = true;
                }
            }
        }
    }

    @Override
    public void exit() {
        if (currentSubstate != null && !currentSubstate.isComplete()) {
            currentSubstate.exit();
        }
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public abstract boolean isValid();
}
