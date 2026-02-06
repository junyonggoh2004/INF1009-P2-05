package io.github.some_example_name.inputoutput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * CHILD CLASS 2: MusicTrack
 * This handles long background music.
 * It streams from disk instead of loading into RAM.
 */
public class MusicTrack extends GameAudio {

    private Music music;

    public MusicTrack(String id, String filePath) {
        super(id);
        this.music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
    }

    @Override
    public void play() {
        music.setVolume(this.volume);
        music.play();
    }

    @Override
    public void stop() {
        music.stop();
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        music.setVolume(volume); // Music CAN change volume while playing
    }

    // --- SPECIAL FEATURE ---
    // This is unique to MusicTrack. The parent GameAudio doesn't have this.
    public void setLooping(boolean isLooping) {
        music.setLooping(isLooping);
    }

    @Override
    public void dispose() {
        music.dispose();
    }
}
