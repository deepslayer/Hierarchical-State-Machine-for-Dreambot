## About the Framework: Hierarchical State Machine for OSRS Botting

This framework provides a **hierarchical state machine** architecture designed for scalable, organized bot development in Old School RuneScape (OSRS). Built on a combination of three core classes—`AbstractState`, `SequenceState`, and `DecisionState`—this framework offers flexibility for handling complex decision-making, sequential task execution, and adaptive logic. The design prioritizes always staying responsive to game updates by never blocking the bot in a tight loop, ensuring smooth integration with DreamBot's `onLoop()` method.

### Key Features

#### 1. **Three Core State Types:**
- **AbstractState**: The foundation of all states, ensuring essential methods (`enter()`, `execute()`, `exit()`, `isComplete()`, and `isValid()`) are implemented in all states.
- **SequenceState**: Executes multiple child states in sequential order. Once one substate completes, it proceeds to the next, ensuring tasks are executed step-by-step without interruption.
- **DecisionState**: Implements branching logic, evaluating which substate is valid to run based on game conditions. The first valid substate is executed, allowing dynamic and conditional decisions. It can include `SequenceState` or other `DecisionState` objects as substates, enabling **nested decision-making**.

#### 2. **Nested State Handling:**
One of the framework's most powerful features is its ability to **nest different state types** inside each other. You can mix `DecisionState` and `SequenceState` types to create **multi-layered decision-making** and sequential execution logic.
- **DecisionState with Nested SequenceStates**: A `DecisionState` can decide which `SequenceState` to run based on game conditions. For example, it might choose to run a combat sequence if you're in combat or a healing sequence if your health is low.
- **DecisionState within a DecisionState**: You can nest `DecisionState` objects inside other `DecisionState` objects, creating a hierarchy of conditional logic. This allows for **multi-level decision-making**, where one decision leads to another decision before settling on a sequence of actions.

This nesting capability ensures that your bot can handle highly complex scenarios without needing separate scripts for each.

#### 3. **Game State Responsiveness**:
The framework is built with **bot responsiveness** as a priority. One key aspect is ensuring that the bot always returns control to DreamBot’s `onLoop()` method **between each state execution**, whether it's a `DecisionState`, `SequenceState`, or `AbstractState`. This ensures:
- The bot is always working with the most up-to-date game state.
- DreamBot can continue handling tasks in the background (e.g., game hooks, input, and interaction management).
- The bot is never locked in an internal while-loop, which could freeze or make it unresponsive.

This design ensures that even while processing complex decision logic or executing long sequences of actions, the bot always gives control back to the DreamBot environment.

#### 4. **Dynamic State Evaluation and Execution:**
- **isValid()**: Each state has its own `isValid()` method, allowing the bot to check the game conditions and determine whether a state should execute. This makes the framework adaptive to game changes, enabling real-time responses to different scenarios.
- **isComplete()**: The `isComplete()` method ensures that states are only marked as finished when they have completed their tasks. This feature is critical for sequential tasks where a series of actions must occur before moving on to the next state.

#### 5. **No Blocking Loops**:
In contrast to systems where a state may keep control in a while-loop until completion, this framework **never gets stuck in a loop**. Each state, whether sequential or conditional, returns control back to `onLoop()` after every execution. This ensures that:
- **DreamBot's core logic** continues to operate uninterrupted.
- **Game conditions are always current**, as the bot constantly checks for updates.
- States can be re-evaluated and adjusted as game conditions change, preventing the bot from getting stuck in outdated logic or frozen tasks.

#### 6. **Sequential Execution in SequenceState**:
In `SequenceState`, a series of subtasks (or steps) are executed one after the other. Each substate must complete before moving to the next. However, even while executing these steps sequentially, the bot will always return to `onLoop()` between each substate, ensuring it stays responsive to the current game state.

#### 7. **Decision-Based Execution in DecisionState**:
The `DecisionState` class provides a branching mechanism where it checks each child state’s `isValid()` method to find the most appropriate task to run. If a nested `DecisionState` is valid, the bot can dive deeper into that decision tree, or if a `SequenceState` is valid, the bot will execute it in order.

#### 8. **State Transition and Looping:**
- **After a state completes** (whether it’s a `SequenceState` or `DecisionState`), the state machine will always return to the top and start evaluating from the first state again. This ensures that the bot adapts in real-time to the current game conditions.
- **Sequential States** continue their steps until completion, and **decision states** dynamically choose the appropriate state for the current context.

### Why This Framework is Effective

- **Mixed Nested States**: By mixing `DecisionState` and `SequenceState`, you can create highly complex yet organized logic for bots. The bot can easily switch between decision-making and sequential task execution within the same structure.
- **Never Blocks**: The framework always returns control to `onLoop()` between executions, meaning the bot never gets stuck in a loop and remains fully responsive to the game environment.
- **Dynamic and Flexible**: With nested decision-making and sequential execution, the bot can adapt to various scenarios, whether it's fighting, healing, training, or navigating.
- **Real-Time Responsiveness**: Thanks to the bot always returning to the main loop, it continuously reacts to real-time game updates, keeping the logic fresh and applicable to current conditions.
- **Ease of Use**: Adding new states or modifying existing logic is straightforward, and the `DecisionState` and `SequenceState` classes make it easy to organize both conditional logic and sequential tasks.

This hierarchical state machine framework is a powerful tool for building advanced OSRS bots, offering flexibility, adaptability, and efficiency while ensuring that your bot always operates in sync with the game's real-time state.

```markdown

## How the Framework Works

- **Real-Time Game State Responsiveness**: After every state execution, the bot returns control to DreamBot’s `onLoop()`, ensuring that the bot always operates on the most current game state.
- **Sequential and Conditional Logic**: `SequenceState` ensures that tasks are performed step-by-step in a specific order, while `DecisionState` enables flexible branching logic for adaptive bots.
- **Nested States**: States can be nested to handle multi-layered logic, such as making decisions based on health levels, combat engagement, or loot availability.

---

## How to Use the Framework

### 1. **Setting Up the Main Class**

Here’s a generic example of how to initialize the state machine in DreamBot:

```java
package Main;

import Framework.StateMachine.StateMachine;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

import static org.dreambot.api.utilities.Logger.log;

@ScriptManifest(author = "Your Name", name = "Combat State Machine Bot", version = 1.0, description = "A simple combat bot using state machine", category = Category.COMBAT)
public class Main extends AbstractScript {
    private StateMachine stateMachine;

    @Override
    public void onStart() {
        log("Script Started...");

        // Initialize the state machine
        stateMachine = new StateMachine();

        // Add combat-related states
        stateMachine.addState(new CombatDecisionState(stateMachine));
        stateMachine.addState(new LootSequenceState(stateMachine));

        // Start the state machine
        stateMachine.start();

        log("State machine initialized and started.");
    }

    @Override
    public int onLoop() {
        if (stateMachine.isRunning()) {
            stateMachine.update();
        } else {
            stop();  // Stop if no valid states are running
        }
        return 1000;  // Delay between loops
    }

    @Override
    public void onExit() {
        log("Stopping Bot...");
    }
}
```

### 2. **Creating States**

Here’s how to create the states for attacking, healing, and looting.

#### Attack State Example

```java
package States;

import Framework.StateMachine.AbstractState;
import Framework.StateMachine.StateMachine;

import static org.dreambot.api.utilities.Logger.log;

public class AttackState extends AbstractState {

    public AttackState(StateMachine machine) {
        super(machine);
    }

    @Override
    public boolean isValid() {
        return Players.getlocal.getHealthPercent() > 50 && !Players.getlocal.isInCombat(); 
    }

    @Override
    public void enter() {
        log("Entering Attack State");
    }

    @Override
    public void execute() {
        // Code to find and attack an NPC here
        log("Executing Attack State");
        complete = true;  // Mark as complete after attacking
    }

    @Override
    public void exit() {
        log("Exiting Attack State");
    }
}
```

#### Heal State Example

```java
package States;

import Framework.StateMachine.AbstractState;
import Framework.StateMachine.StateMachine;

import static org.dreambot.api.utilities.Logger.log;

public class HealState extends AbstractState {

    public HealState(StateMachine machine) {
        super(machine);
    }

    @Override
    public boolean isValid() {
        return Players.getlocal.getHealthPercent() < 50;  // Heal when health is below 50%
    }

    @Override
    public void enter() {
        log("Entering Heal State");
    }

    @Override
    public void execute() {
        // Code to eat food or heal
        log("Executing Heal State");
        complete = true;  // Mark as complete after healing
    }

    @Override
    public void exit() {
        log("Exiting Heal State");
    }
}
```

#### Loot State Example

```java
package States;

import Framework.StateMachine.SequenceState;
import Framework.StateMachine.StateMachine;

import static org.dreambot.api.utilities.Logger.log;

public class LootSequenceState extends SequenceState {

    public LootSequenceState(StateMachine machine) {
        super(machine);
        addSubState(new WalkToLootState(machine));  // Walk to loot
        addSubState(new PickupLootState(machine));  // Pick up loot
    }

    @Override
    public boolean isValid() {
        log("LootSequenceState isValid() called");
        return !Inventory.isFull();  // Loot only if inventory isn't full
    }
}
```

### 3. **Creating Decision and Sequence States**

`DecisionState` handles conditional logic, while `SequenceState` performs a series of actions in order.

#### Combat Decision State

```java
package States;

import Framework.StateMachine.DecisionState;
import Framework.StateMachine.StateMachine;

import static org.dreambot.api.utilities.Logger.log;

public class CombatDecisionState extends DecisionState {

    public CombatDecisionState(StateMachine machine) {
        super(machine);
        addSubState(new HealState(machine));  // Heal if health is low
        addSubState(new AttackState(machine));  // Attack if not in combat and health is sufficient
    }

    @Override
    public boolean isValid() {
        log("CombatDecisionState isValid() called");
        return true;  // Always valid during combat
    }
}
```

### 4. **Running the State Machine**

After adding your states in the `onStart()` method, the state machine will automatically evaluate and run each state based on the game conditions.

```java
@Override
public void onStart() {
    log("Script Started...");

    // Initialize the state machine
    stateMachine = new StateMachine();

    // Add states to handle combat and looting
    stateMachine.addState(new CombatDecisionState(stateMachine));
    stateMachine.addState(new LootSequenceState(stateMachine));

    // Start the state machine
    stateMachine.start();

    log("State machine initialized and started.");
}
```

---

### Summary

This framework offers a powerful and flexible way to build scalable bots using **DecisionState** and **SequenceState** structures. You can easily create nested decision-making trees or sequential tasks to handle even the most complex scenarios in OSRS botting.

- **Always stays responsive**: The bot returns control to DreamBot after every state execution, allowing it to adapt to the latest game state.
- **Dynamic decision-making**: With `DecisionState`, the bot can intelligently switch between actions based on conditions (e.g., heal, attack, or loot).
- **Organized and scalable**: The framework allows easy addition of new states, making it suitable for both simple and complex bots.
```
