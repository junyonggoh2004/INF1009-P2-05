package io.github.some_example_name.inputoutput;

/**
 * THE HUB
 * This class handles everything the engine "outputs" to the user.
 * 1. Audio (Sound/Music)
 * 2. Visual Logs (The "Console" text)
 */
public class OutputHandler {

    // COMPOSITION: OutputHandler "owns" the AudioManager
    private AudioManager audioManager;

    public OutputHandler() {
        this.audioManager = new AudioManager();
    }

    // --- ACCESSOR ---
    // This allows the rest of the game to get the Audio Manager to play sounds.
    public AudioManager getAudioManager() {
        return audioManager;
    }

    // --- CONSOLE LOGGING ---
    // Right now it prints to the debug window. Later, you could make it print to a GUI box.
    public void log(String message) {
        // We add a tag [ENGINE] so it's easy to read in the messy debug window
        System.out.println("[ENGINE]: " + message);
    }

    // --- CLEANUP ---
    public void dispose() {
        // When OutputHandler dies, it takes the AudioManager with it.
        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}
