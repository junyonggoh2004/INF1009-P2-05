package io.github.some_example_name.inputoutput;

import com.badlogic.gdx.Gdx;

public class ConsoleLogger {

    public void log(String message) {
        // Instead of System.out.println, we use LibGDX's tool.
        // The first string is a "Tag" (easier to filter in long logs).
        if (Gdx.app != null) {
            Gdx.app.log("ENGINE", message);
        } else {
            // Fallback if LibGDX isn't ready yet
            System.out.println("[ENGINE]: " + message);
        }
    }

    public void logError(String message) {
        if (Gdx.app != null) {
            Gdx.app.error("ENGINE ERROR", message);
        } else {
            System.err.println("[ENGINE ERROR]: " + message);
        }
    }
}
