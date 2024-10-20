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
