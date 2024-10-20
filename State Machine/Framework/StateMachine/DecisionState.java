package Framework.StateMachine;

import java.util.ArrayList;
import java.util.List;

import static org.dreambot.api.utilities.Logger.log;

public abstract class DecisionState extends AbstractState {
    private final List<State> decisionSubstates = new ArrayList<>();
    private State currentSubstate;

    public DecisionState(StateMachine machine) {
        super(machine);
    }

    public void addSubState(State state) {
        decisionSubstates.add(state);
    }

    @Override
    public void enter() {
        log("Entering DecisionState: " + this.getClass().getSimpleName());
        findNextValidSubstate();
    }

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

    @Override
    public void exit() {
        log("Exiting DecisionState: " + this.getClass().getSimpleName());
        if (currentSubstate != null) {
            currentSubstate.exit();
        }
    }

    @Override
    public boolean isComplete() {
        return currentSubstate == null;
    }

    @Override
    public abstract boolean isValid();


    private void findNextValidSubstate() {

        currentSubstate = null;


        for (State substate : decisionSubstates) {
            if (substate.isValid()) {
                currentSubstate = substate;
                currentSubstate.enter();
                return;
            }
        }

        log("No valid substates found for DecisionState: " + this.getClass().getSimpleName());
    }
}
