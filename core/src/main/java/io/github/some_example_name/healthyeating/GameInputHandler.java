package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.Input;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.inputoutput.input.InputHandler;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;

/**
 * Handles player movement input using the engine's action-based InputHandler.
 * P1 uses WASD (actions P1_UP/DOWN/LEFT/RIGHT).
 * P2 uses Arrow Keys (actions P2_UP/DOWN/LEFT/RIGHT).
 */
public class GameInputHandler {

    private static final float PLAYER_SPEED = 300f;

    // Action name constants
    public static final String P1_UP    = "P1_UP";
    public static final String P1_DOWN  = "P1_DOWN";
    public static final String P1_LEFT  = "P1_LEFT";
    public static final String P1_RIGHT = "P1_RIGHT";
    public static final String P2_UP    = "P2_UP";
    public static final String P2_DOWN  = "P2_DOWN";
    public static final String P2_LEFT  = "P2_LEFT";
    public static final String P2_RIGHT = "P2_RIGHT";

    /** Registers all game-specific input bindings on the engine's InputHandler. */
    public static void bindActions(InputHandler input) {
        // Player 1: WASD
        input.bindKey(Input.Keys.W, P1_UP);
        input.bindKey(Input.Keys.S, P1_DOWN);
        input.bindKey(Input.Keys.A, P1_LEFT);
        input.bindKey(Input.Keys.D, P1_RIGHT);

        // Player 2: Arrow keys
        input.bindKey(Input.Keys.UP,    P2_UP);
        input.bindKey(Input.Keys.DOWN,  P2_DOWN);
        input.bindKey(Input.Keys.LEFT,  P2_LEFT);
        input.bindKey(Input.Keys.RIGHT, P2_RIGHT);
    }

    /** Handles movement using engine actions. In singleplayer, P1 uses both WASD and arrows. */
    public void handleMovement(Entity player1, Entity player2,
                               MovementManager mm, InputHandler input) {
        boolean singlePlayer = (player2 == null);

        // Player 1
        if (player1 != null) {
            Motion m1 = mm.getMotion(player1.getId());
            if (m1 != null) {
                float vx = 0f, vy = 0f;
                if (input.isActionActive(P1_UP))    vy += 1f;
                if (input.isActionActive(P1_DOWN))  vy -= 1f;
                if (input.isActionActive(P1_LEFT))  vx -= 1f;
                if (input.isActionActive(P1_RIGHT)) vx += 1f;
                // In singleplayer, arrows also control P1
                if (singlePlayer) {
                    if (input.isActionActive(P2_UP))    vy += 1f;
                    if (input.isActionActive(P2_DOWN))  vy -= 1f;
                    if (input.isActionActive(P2_LEFT))  vx -= 1f;
                    if (input.isActionActive(P2_RIGHT)) vx += 1f;
                }
                applyNormalized(m1, vx, vy);
            }
        }

        // Player 2: Arrow Keys (multiplayer only)
        if (player2 != null) {
            Motion m2 = mm.getMotion(player2.getId());
            if (m2 != null) {
                float vx = 0f, vy = 0f;
                if (input.isActionActive(P2_UP))    vy += 1f;
                if (input.isActionActive(P2_DOWN))  vy -= 1f;
                if (input.isActionActive(P2_LEFT))  vx -= 1f;
                if (input.isActionActive(P2_RIGHT)) vx += 1f;
                applyNormalized(m2, vx, vy);
            }
        }
    }

    /** Normalizes diagonal movement so speed is consistent in all directions. */
    private void applyNormalized(Motion motion, float vx, float vy) {
        float len = (float) Math.sqrt(vx * vx + vy * vy);
        if (len > 0) {
            motion.setVx((vx / len) * PLAYER_SPEED);
            motion.setVy((vy / len) * PLAYER_SPEED);
        } else {
            motion.setVx(0f);
            motion.setVy(0f);
        }
    }
}
