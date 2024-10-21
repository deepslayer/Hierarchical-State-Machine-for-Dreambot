package Framework.Interface;

public interface State {

    void enter();    // Method to handle any setup when entering the state
    void execute();  // Method to be called to process the state’s tasks
    void exit();     // Cleanup method when exiting the state
    boolean isComplete();  // Checker to see if the state has completed its task
}