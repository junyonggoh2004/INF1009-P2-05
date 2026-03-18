package io.github.some_example_name.inputoutput.output;

import java.util.HashMap;
import java.util.Map;

/**
 * THE MANAGER
 * This class controls all audio in the game.
 * It uses POLYMORPHISM to treat Music and Sounds as the same "GameAudio" object.
 */
public class AudioManager {

    // The Library: Stores all loaded audio files by their name (ID).
    // Note: We use <String, GameAudio>. This Map can hold BOTH Music and Sounds.
    private Map<String, GameAudio> audioLibrary;

    // Track the currently playing music so we can stop it if we change scenes.
    private GameAudio currentMusic;

    public AudioManager() {
        audioLibrary = new HashMap<>();
    }

    // --- LOADING METHODS ---

    public void loadSound(String id, String filePath) {
        // Create a specific SoundEffect (Child)
        GameAudio newSound = new SoundEffect(id, filePath);
        // Store it in the generic GameAudio list (Parent)
        audioLibrary.put(id, newSound);
    }

    public void loadMusic(String id, String filePath) {
        // Create a specific MusicTrack (Child)
        GameAudio newMusic = new MusicTrack(id, filePath);
        audioLibrary.put(id, newMusic);
    }

    // --- PLAYBACK METHODS ---

    /**
     * The Play Button.
     * Just give it an ID, and it figures out what to do.
     */
    public void playAudio(String id) {
        GameAudio audio = audioLibrary.get(id);

        if (audio != null) {
            // If this is a music track, stop the previous one first
            if (audio.isMusic()) {
                if (currentMusic != null) {
                    currentMusic.stop();
                }
                currentMusic = audio;
            }

            audio.play();
        } else {
            System.out.println("[AudioManager] Error: Audio ID '" + id + "' not found!");
        }
    }

    public void stopAudio(String id) {
        GameAudio audio = audioLibrary.get(id);
        if (audio != null) {
            audio.stop();
        }
    }

    /**
     * The Getter
     * This allows other classes to grab a specific audio object to change its
     * volume or pitch, without exposing the entire library list.
     */
    public GameAudio getAudio(String id) {
        return audioLibrary.get(id);
    }

    // --- CLEANUP ---

    // Call this when the game closes to free up RAM.
    public void dispose() {
        // Loop through every item in our library and dispose of it
        for (GameAudio audio : audioLibrary.values()) {
            audio.dispose();
        }
        audioLibrary.clear();
    }
}
