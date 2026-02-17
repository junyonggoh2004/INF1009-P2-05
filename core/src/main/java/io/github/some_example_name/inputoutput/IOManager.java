package io.github.some_example_name.inputoutput;

import com.badlogic.gdx.utils.Disposable;
import io.github.some_example_name.inputoutput.input.InputHandler;
import io.github.some_example_name.inputoutput.output.OutputHandler;

/**
 * THE CONNECTOR
 * This manager groups all "Inputs" (Keyboard/Mouse) and "Outputs" (Audio/Log)
 * into one place so the EngineCore can access them easily.
 */
public class IOManager implements Disposable {

    private OutputHandler outputHandler;
    private InputHandler inputHandler;

    public IOManager() {
        // Initialize
        this.outputHandler = new OutputHandler();
        this.inputHandler = new InputHandler();
    }

    // --- MAIN LOOP ---
    // The EngineCore calls this.
    public void update(float dt) {
        // 1. Process Inputs
        inputHandler.update(dt);

        // 2. Process Outputs
        // Currently, our OutputHandler doesn't need a constant update loop
        // because it reacts to events (like "play sound"), but we keep this
        // here in case we need it later (e.g., fading out music over time).
    }

    // --- GETTERS ---

    public OutputHandler output() {
        return outputHandler;
    }

    public InputHandler input() {
        return inputHandler;
    }

    // These methods provide quick access to common input operations
    
    /**
     * Checks if an action is currently active (pressed or held).
     * @param actionName 
     * @return 
     */
    public boolean isActionActive(String actionName) {
        return inputHandler.isActionActive(actionName);
    }
    
    /**
     * Checks if an action was just pressed this frame.
     * @param actionName 
     * @return 
     */
    public boolean isActionPressed(String actionName) {
        return inputHandler.isActionPressed(actionName);
    }
    
    /**
     * Checks if an action is being held.
     * @param actionName 
     * @return
     */
    public boolean isActionHeld(String actionName) {
        return inputHandler.isActionHeld(actionName);
    }
    
    /**
     * Checks if an action was just released.
     * @param actionName 
     * @return 
     */
    public boolean isActionReleased(String actionName) {
        return inputHandler.isActionReleased(actionName);
    }

    // --- CLEANUP ---
    @Override
    public void dispose() {
        // Clean up OutputHandler
        if (outputHandler != null) {
            outputHandler.dispose();
        }

        // Clean up InputHandler
        if (inputHandler != null) {
            inputHandler.dispose();
        }
    }
}
