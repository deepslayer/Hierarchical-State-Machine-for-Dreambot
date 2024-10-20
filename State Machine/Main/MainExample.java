package Main;

import Framework.StateMachine.StateMachine;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(author = "", name = "State Machine Framework", version = 1.0, description = "State Machine Framework", category = Category.COMBAT)
public class Main extends AbstractScript {
    private StateMachine stateMachine;

    @Override
    public void onStart() {
        log("Script Started...");

        // Initialize the state machine
        stateMachine = new StateMachine();

        // Add example states directly to the state machine
        // stateMachine.addState(new YourState1(stateMachine));
        // stateMachine.addState(new YourState2(stateMachine));

        // Start the state machine (find the first valid state)
        stateMachine.start();

        log("State machine initialized and started.");
    }

    @Override
    public int onLoop() {

        if (stateMachine.isRunning()) {
            stateMachine.update();
        } else {
            stop();  // Stop the script if the state machine is not running
        }

        return 1000;
    }

    @Override
    public void onExit() {
        log("Stopping Bot");
    }
}
