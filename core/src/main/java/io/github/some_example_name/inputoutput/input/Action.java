package io.github.some_example_name.inputoutput.input;

/**
 * Represents a game action (e.g., MOVE_LEFT, JUMP) and its current state.
 */
public class Action {

    private String name;
    private ActionState state;
    private boolean wasPressed;

    public Action(String name) {
        this.name = name;
        this.state = ActionState.IDLE;
        this.wasPressed = false;
    }

    public String getName() {
        return name;
    }

    public ActionState getState() {
        return state;
    }

    public void setState(ActionState state) {
        this.state = state;
    }

    public boolean isActive() {
        return state == ActionState.PRESSED || state == ActionState.HELD;
    }

    public boolean isPressed() {
        return state == ActionState.PRESSED;
    }

    public boolean isHeld() {
        return state == ActionState.HELD;
    }

    public boolean isReleased() {
        return state == ActionState.RELEASED;
    }

    public boolean isIdle() {
        return state == ActionState.IDLE;
    }

    /**
     * Call once per frame to update the action state.
     */
    public void update(boolean isCurrentlyPressed) {
        if (isCurrentlyPressed) {
            state = wasPressed ? ActionState.HELD : ActionState.PRESSED;
        } else {
            state = wasPressed ? ActionState.RELEASED : ActionState.IDLE;
        }

        wasPressed = isCurrentlyPressed;
    }

    public void reset() {
        state = ActionState.IDLE;
        wasPressed = false;
    }

    @Override
    public String toString() {
        return "Action{name='" + name + "', state=" + state + "}";
    }
}
