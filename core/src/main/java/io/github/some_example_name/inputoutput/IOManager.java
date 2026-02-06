package io.github.some_example_name.inputoutput;

import com.badlogic.gdx.utils.Disposable;

/**
 * THE CONNECTOR
 * This manager groups all "Inputs" (Keyboard/Mouse) and "Outputs" (Audio/Screen)
 * into one place so the EngineCore can access them easily.
 */
public class IOManager implements Disposable {

    private OutputHandler outputHandler;

    // InputHandler Part (Commented out for now so code compiles):
    // private InputHandler inputHandler;

    public IOManager() {
        // Initialize
        this.outputHandler = new OutputHandler();
        // this.inputHandler = new InputHandler();
    }

    // --- MAIN LOOP ---
    // The EngineCore calls this 60 times a second.
    public void update(float dt) {
        // 1. Process Inputs (Teammate)
        // inputHandler.update(dt);

        // 2. Process Outputs (You)
        // Currently, our OutputHandler doesn't need a constant update loop
        // because it reacts to events (like "play sound"), but we keep this
        // here in case we need it later (e.g., fading out music over time).
    }

    // --- GETTERS ---

    public OutputHandler output() {
        return outputHandler;
    }

    /* public InputHandler input() {
        return inputHandler;
    }
    */

    // --- CLEANUP ---
    @Override
    public void dispose() {
        // Clean up OutputHandler
        if (outputHandler != null) {
            outputHandler.dispose();
        }

        // Clean up InputHandler
        /*
        if (inputHandler != null) {
            inputHandler.dispose();
        }
        */
    }
}
