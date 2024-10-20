package Framework.StateMachine;

public abstract class AbstractState implements State {
    protected StateMachine machine;
    protected boolean complete = false;

    public AbstractState(StateMachine machine) {
        this.machine = machine;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public abstract boolean isValid();

    @Override
    public abstract void enter();

    @Override
    public abstract void execute();

    @Override
    public abstract void exit();
}
