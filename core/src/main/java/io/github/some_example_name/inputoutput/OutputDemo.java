package io.github.some_example_name.inputoutput;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;

public class OutputDemo extends ApplicationAdapter {

    private IOManager ioManager;

    // --- VOLUME VARIABLES ---
    private float currentVolume = 0.30f; // Start at 30%
    // --- TIMERS  ---
    private float keyTimer = 0.0f;
    // 1. INITIAL_DELAY: How long you hold before it starts scrolling
    private final float INITIAL_DELAY = 0.4f;
    // 2. SCROLL_RATE: How fast it updates while holding (0.1s = 10% volume change per second)
    private final float SCROLL_RATE = 0.1f;

    @Override
    public void create() {
        ioManager = new IOManager();
        System.out.println("--- ENGINE START ---");

        // 1. LOAD AUDIO
        ioManager.output().getAudioManager().loadSound("click", "Bubble-Click.ogg");
        ioManager.output().getAudioManager().loadMusic("bgm", "Krayzius & Brainstorm - Virtual Boy.mp3");

        // 2. ADJUST VOLUME (Now using the clean "Getter" way)
        // Set Music to 30% volume
        GameAudio bgm = ioManager.output().getAudioManager().getAudio("bgm");
        if (bgm != null) {
            bgm.setVolume(0.3f);
        }
        // We ask the manager: "Give me the 'click' object."
        GameAudio click = ioManager.output().getAudioManager().getAudio("click");
        if (click != null) {
            click.setVolume(0.5f);
        }

        // 3. START MUSIC
        ioManager.output().getLogger().log("Starting Background Music...");
        ioManager.output().getAudioManager().playAudio("bgm");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float dt = Gdx.graphics.getDeltaTime();

        // --- MOUSE CLICK ---
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            ioManager.output().getLogger().log("Mouse Clicked! Playing Sound.");
            ioManager.output().getAudioManager().playAudio("click");
        }

        // --- VOLUME CONTROL ---

        // 1. CHECK INPUTS
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean upJustClicked = Gdx.input.isKeyJustPressed(Input.Keys.UP);
        boolean downJustClicked = Gdx.input.isKeyJustPressed(Input.Keys.DOWN);

        int direction = 0; // 0 = None, 1 = Up, -1 = Down

        // 2. DETERMINE ACTION
        if (upJustClicked) {
            // CASE A: User Just Tapped (Instant +1%)
            direction = 1;
            keyTimer = 0; // Reset timer
        } else if (downJustClicked) {
            // CASE B: User Just Tapped (Instant -1%)
            direction = -1;
            keyTimer = 0; // Reset timer
        } else if (upPressed || downPressed) {
            // CASE C: User is Holding (Wait for Delay, then Scroll)
            keyTimer += dt;

            // If timer exceeds Delay + Rate, trigger a change
            if (keyTimer > INITIAL_DELAY) {
                // We subtract the rate so it triggers again in 0.1s
                keyTimer -= SCROLL_RATE;
                direction = upPressed ? 1 : -1;
            }
        } else {
            // CASE D: User let go
            keyTimer = 0;
        }

        // 3. APPLY CHANGE (If any)
        if (direction != 0) {

            // Change by exactly 1% (0.01f)
            if (direction == 1) {
                currentVolume += 0.01f;
            } else {
                currentVolume -= 0.01f;
            }

            // 4. CLEAN UP MATH
            currentVolume = MathUtils.clamp(currentVolume, 0.0f, 1.0f);
            // Snap to nearest 1%
            currentVolume = Math.round(currentVolume * 100.0f) / 100.0f;

            // 5. UPDATE ENGINE
            GameAudio bgm = ioManager.output().getAudioManager().getAudio("bgm");
            if (bgm != null) {
                bgm.setVolume(currentVolume);
            }

            // 6. LOG IT
            ioManager.output().getLogger().log("Volume: " + Math.round(currentVolume * 100) + "%");
        }

        ioManager.update(dt);
    }

    @Override
    public void dispose() {
        System.out.println("--- ENGINE SHUTDOWN ---");
        ioManager.dispose();
    }
}