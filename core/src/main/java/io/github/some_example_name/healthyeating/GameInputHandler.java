package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;

/**
 * Handles player movement input.
 * P1 uses WASD, P2 uses Arrow Keys.
 */
public class GameInputHandler {

    private static final float PLAYER_SPEED = 300f;

    /** Handles movement. In singleplayer, P1 uses WASD + Arrows. In multiplayer, P1=WASD, P2=Arrows. */
    public void handleMovement(Entity player1, Entity player2, MovementManager mm) {
        boolean singlePlayer = (player2 == null);

        // Player 1
        if (player1 != null) {
            Motion m1 = mm.getMotion(player1.getId());
            if (m1 != null) {
                float vx = 0f, vy = 0f;
                if (Gdx.input.isKeyPressed(Input.Keys.W)) vy += PLAYER_SPEED;
                if (Gdx.input.isKeyPressed(Input.Keys.S)) vy -= PLAYER_SPEED;
                if (Gdx.input.isKeyPressed(Input.Keys.A)) vx -= PLAYER_SPEED;
                if (Gdx.input.isKeyPressed(Input.Keys.D)) vx += PLAYER_SPEED;
                // In singleplayer, also accept arrow keys
                if (singlePlayer) {
                    if (Gdx.input.isKeyPressed(Input.Keys.UP))    vy += PLAYER_SPEED;
                    if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  vy -= PLAYER_SPEED;
                    if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  vx -= PLAYER_SPEED;
                    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) vx += PLAYER_SPEED;
                }
                m1.setVx(vx);
                m1.setVy(vy);
            }
        }

        // Player 2: Arrow Keys (multiplayer only)
        if (player2 != null) {
            Motion m2 = mm.getMotion(player2.getId());
            if (m2 != null) {
                float vx = 0f, vy = 0f;
                if (Gdx.input.isKeyPressed(Input.Keys.UP))    vy += PLAYER_SPEED;
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  vy -= PLAYER_SPEED;
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  vx -= PLAYER_SPEED;
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) vx += PLAYER_SPEED;
                m2.setVx(vx);
                m2.setVy(vy);
            }
        }
    }
}
