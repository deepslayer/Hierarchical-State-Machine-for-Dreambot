package Framework.StateMachine;

import Framework.Interface.State;

public abstract class ActionState implements State {
    protected StateMachine machine;
    private boolean complete = false;

    public ActionState(StateMachine machine) {
        this.machine = machine;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
    @Override
    public void enter() {
        resetCompletion();  // Reset completion when entering the state by default
    }

    @Override
    public abstract void execute();

    @Override
    public abstract void exit();

    protected void markComplete() {
        this.complete = true;
    }
    protected void resetCompletion() {
        this.complete = false;
    }

}
