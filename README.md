## How-To Guide: Using the Modular State Machine Framework

### Why This Framework Stands Out

The **Modular State Machine Framework** is designed with flexibility and efficiency at its core. Its strength lies in its ability to handle complex bot behaviors without compromising real-time responsiveness. This framework excels because:

- **Real-Time Responsiveness**: Unlike traditional state machines, this framework does not lock itself in a loop while executing tasks. Instead, it breaks tasks into smaller steps, ensuring that control is handed back to **DreamBot's `onLoop()` method** after each execution cycle. This allows the bot to stay responsive to real-time changes in the game state, avoiding issues like missing game updates or inputs.
  
- **One of the most powerful features of this framework is how it **dynamically manages task priority** while **preserving efficiency** in execution. Specifically, **DecisionStates** will reset to the top of their hierarchy once a valid substate completes, allowing them to re-evaluate conditions for dynamic decision-making. This ensures that decisions like healing or attacking are always based on the most up-to-date game state, giving the bot real-time adaptability.

However, **SequenceStates** and **ActionStates** are different. Once they begin execution, they do **not re-evaluate parent states or conditions** after each cycle. These states are designed to **continue where they left off** until their task is complete. For example, if a combat sequence has started (e.g., switching prayers, switching weapons, attacking), it will run through the entire sequence without needing to check higher-level decision states after each step.

This combination of behaviours is what makes the framework so powerful. **Task prioritization is enforced when needed** (through DecisionStates), but **sequential actions execute uninterrupted** when conditions allow, ensuring efficient task handling without unnecessary reevaluation. The ability to return control to `onLoop()` after each state execution keeps the bot highly responsive, but the framework doesn’t force states to reset their hierarchy unless required. This means the bot can dynamically adjust its actions when necessary but stay focused on completing current tasks in an optimal flow. 

This hybrid approach—**resetting only when necessary**—is what gives the bot both flexibility and efficiency, ensuring it reacts to the game state while maintaining task continuity.

- **Modular Design**: The framework is built with **modularity** in mind. By using **ActionStates**, **SequenceStates**, and **DecisionStates**, you can craft complex decision-making and task execution flows by combining smaller, reusable state components. This makes it easy to adapt the bot’s behavior to any game scenario without rewriting the entire state machine.

- **Adaptive and Efficient**: The combination of `DecisionStates` and `SequenceStates` enables adaptive task execution based on game conditions. The bot can prioritize actions like healing, switching prayers, and attacking in the right context without getting stuck in unnecessary tasks.

---

### Components Overview

1. **ActionState**: Represents a single task that may execute over multiple cycles.
2. **SequenceState**: Executes multiple substates in a predefined order, ideal for sequential tasks.
3. **DecisionState**: Makes dynamic decisions by evaluating the validity of its substates, useful for conditional logic and task prioritization.

---

### 1. Action State

#### Overview:
The **ActionState** represents a single task or action like attacking, healing, or looting. These states can execute over multiple cycles until a condition is met. They don't require an `isValid()` check, making them useful in combination with `DecisionState` or `SequenceState`.

#### Features:
- **Persistent Execution**: Continues executing its task until the goal is met.
- **Flexible Completion**: Remains active until explicitly marked complete (e.g., attacking until an enemy dies, healing until health is full). By default, it marks itself as complete after one execution, but you can override `isComplete()` to control this behavior over multiple cycles.

#### When to Use:
- For single tasks that require multiple cycles, such as continuously attacking or healing.
- For tasks that don’t need to be conditionally validated (pair with `DecisionState` or `SequenceState` for conditional logic).

#### Example:

```java
public class AttackState extends AbstractState {
    public AttackState(StateMachine machine) {
        super(machine);
    }

    @Override
    public void enter() {
        log("Entering AttackState");
        super.enter();  // Automatically resets the completion flag on entry
    }

    @Override
    public void execute() {
        log("Executing AttackState: Attacking enemy...");
        if (enemy.isAlive()) {
            // Continue attacking
            return;  // Early return keeps the bot responsive to real-time game updates
        } else {
            markComplete();  // Mark complete when enemy is defeated
        }
    }

    @Override
    public void exit() {
        log("Exiting AttackState");
    }
}
```

#### Key Points:
- **No Validity Check**: Action States don’t require an `isValid()` method; they execute as part of a sequence or decision.
- **Early Returns**: Action States execute in steps and often use early returns to keep DreamBot responsive. This means they execute a portion of their task and return control to `onLoop()` to check the game state again before continuing.
  
Example of Early Return:

```java
@Override
public void execute() {
    switch (playerState) {
        case FIGHTING:
            attackEnemy();
            return;  // Return after partial execution
        case RUNAWAY:
            runFromNPC();
            markComplete();  // Mark completion once the player retreats successfully
            break;
    }
}
```

---

### 2. SequenceState

#### Overview:
The **SequenceState** manages a series of tasks (substates) executed in a specific order. Each substate (whether an ActionState, SequenceState, or DecisionState) runs until it's marked complete, after which the sequence moves to the next subtask.

#### Features:
- **Sequential Execution**: Executes each substate in a fixed order, ensuring tasks are completed step-by-step.
- **Nested Sequences**: Can contain other `SequenceStates` and `DecisionStates`, allowing you to build complex workflows.

#### When to Use:
- For tasks that must occur in a specific order (e.g., switching prayer, then switching weapons, then attacking).
- For chaining multiple actions or decisions within a well-defined workflow.

#### Example:

```java
public class CombatSequenceState extends SequenceState {
    public CombatSequenceState(StateMachine machine) {
        super(machine);
        addSubState(new SwitchPrayerState(machine));  // Step 1: Switch prayer
        addSubState(new SwitchWeaponState(machine));  // Step 2: Switch weapon
        addSubState(new AttackState(machine));        // Step 3: Attack
    }

    @Override
    public boolean isValid() {
        return player.isInCombat();
    }
}
```

#### Key Points:
- **Sequential Execution**: Substates are executed in order, ensuring a structured flow of actions.
- **No Validation for Substates**: Substates within a `SequenceState` don’t need to be validated; they run one after the other automatically.
- **Nested Sequences**: `SequenceStates` can contain other sequences or decisions, creating multi-step workflows.
  
You can combine `DecisionState` and `SequenceState` to create more dynamic workflows:

Example:
```java
public class NestedCombatState extends SequenceState {
    public NestedCombatState(StateMachine machine) {
        super(machine);
        addSubState(new CombatDecisionState(machine));  // Combines decision logic with sequences
        addSubState(new RetreatSequenceState(machine));  // Retreat if decision determines danger
    }
}
```

---

### 3. DecisionState

#### Overview:
A **DecisionState** evaluates the `isValid()` method of its substates to decide which one to execute. The first valid substate is selected, allowing the bot to dynamically respond to changing game conditions (e.g., healing if low on health, or attacking otherwise).

#### Features:
- **Dynamic Decision-Making**: Executes the first valid substate, adapting to game conditions.
- **Substate Flexibility**: Can contain ActionStates, SequenceStates, or other DecisionStates, allowing for flexible hierarchical decisions.

#### When to Use:
- For scenarios where dynamic prioritization is required (e.g., attack if health is good, heal otherwise).
- For complex branching logic where the bot needs to evaluate multiple actions before proceeding.

#### Example:

```java
public class CombatDecisionState extends DecisionState {
    public CombatDecisionState(StateMachine machine) {
        super(machine);
        addSubState(new HealState(machine));   // Heal if low on health
        addSubState(new SwitchPrayerState(machine)); // Switch prayer if needed
        addSubState(new AttackState(machine)); // Attack if able
    }

    @Override
    public boolean isValid() {
        return player.isInCombat();
    }
}
```

#### Key Points:
- **Conditional Logic**: `DecisionState` evaluates its substates based on `isValid()` methods and executes the first valid one.
- **Resets After Completion**: Once a substate completes, the `DecisionState` returns to the top and starts evaluating substates again from the beginning.
- **Nested Decisions**: You can nest multiple `DecisionStates` for more complex decision-making.

---

### Best Practices and Considerations

#### Mixing States:
- **DecisionState + SequenceState**: Combine these types to handle conditional logic and sequential workflows. For example, a `DecisionState` can determine whether to heal or attack, while a `SequenceState` manages the steps for attacking (switch prayer, switch weapon, attack).
  
#### Avoid Infinite Loops:
- Ensure each state either marks itself as complete or transitions properly to avoid being stuck in the same state indefinitely. Use `markComplete()` when a task is finished.

#### Real-Time Adaptation:
- Each state returns control to DreamBot’s `onLoop()` method between executions. This keeps the bot responsive to real-time changes in the game, preventing it from getting stuck in long-running loops.

#### Hierarchical Flexibility:
- `DecisionState` can manage complex decision trees by nesting other `DecisionStates` within itself.
- `SequenceState` ensures that tasks occur in the proper order, while combining it with `DecisionState` allows for dynamic prioritization of those tasks.

---

### Summary of State Types

- **Action (Abstract) State**: Executes a single action across multiple cycles until marked complete. Usually doesn’t require conditional validation and can be paired with other state types.
- **SequenceState**: Executes multiple substates in a defined order. Use it to chain actions together.
- **DecisionState**: Dynamically evaluates substates and executes the first valid one. Ideal for handling conditional logic and decision-making.

By mixing and

 nesting these state types, you can create a flexible and adaptive bot framework capable of handling a wide range of in-game scenarios.
