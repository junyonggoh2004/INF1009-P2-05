package io.github.some_example_name.inputoutput;

import com.badlogic.gdx.utils.Disposable;

/**
 * This is the ABSTRACT PARENT class.
 * It defines the "blueprint" that both Music and SoundEffects must follow.
 */
public abstract class GameAudio implements Disposable {

    // "protected" means only children (SoundEffect, MusicTrack) can access these directly.
    protected String id;
    protected float volume = 1.0f; // Default volume is 100% (1.0f)

    // Constructor: We force every audio file to have an ID (name) when created.
    public GameAudio(String id) {
        this.id = id;
    }

    // --- THE CONTRACT ---
    // These methods are "abstract". We don't write the code here.
    // We are commanding the children: "You MUST write your own version of these!"
    public abstract void play();
    public abstract void stop();
    public abstract void setVolume(float volume);

    // This is a concrete method. We write it once here, and everyone shares it.
    public String getId() {
        return id;
    }
}
