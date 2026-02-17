package io.github.some_example_name.inputoutput.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * CHILD CLASS 1: SoundEffect
 * This handles short, instant sounds (Jumps, Explosions, Clicks).
 * It inherits from GameAudio.
 */
public class SoundEffect extends GameAudio {

    private Sound sound;

    public SoundEffect(String id, String filePath) {
        super(id); // Call the parent constructor to set the ID
        // Load the file into RAM using LibGDX
        this.sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
    }

    // We MUST implement play() because our parent GameAudio said so.
    @Override
    public void play() {
        // We use the volume variable from the parent class
        sound.play(this.volume);
    }

    @Override
    public void stop() {
        sound.stop();
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        // Note: LibGDX Sound doesn't have a simple "setVolume" for all instances,
        // so we just update our internal number for the NEXT time we play.
    }

    @Override
    public void dispose() {
        sound.dispose(); // Clean up memory!
    }
}
