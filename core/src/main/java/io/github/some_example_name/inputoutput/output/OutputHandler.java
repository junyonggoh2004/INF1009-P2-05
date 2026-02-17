package io.github.some_example_name.inputoutput.output;

/**
 * THE HUB
 * This class handles everything the engine "outputs" to the user.
 * 1. Audio (Sound/Music)
 * 2. Visual Logs (The "Console" text)
 */
public class OutputHandler {

    private AudioManager audioManager;
    private ConsoleLogger consoleLogger;

    public OutputHandler() {
        this.audioManager = new AudioManager();
        this.consoleLogger = new ConsoleLogger();
    }

    // --- ACCESSOR ---
    // This allows the rest of the game to get the Audio Manager to play sounds.
    public AudioManager getAudioManager() {
        return audioManager;
    }

    // --- CONSOLE LOGGING ---
    // This allows the rest of the engine can use the logger
    public ConsoleLogger getLogger() {
        return consoleLogger;
    }

    // --- CLEANUP ---
    public void dispose() {
        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}
