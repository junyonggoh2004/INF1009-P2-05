package io.github.some_example_name.factory;

import java.util.Random;

/**
 * Utility for generating random spawn positions and velocities.
 * Centralizes the bounded-random calculations that were previously
 * duplicated across stage scene classes.
 */
public class SpawnHelper {

    private final Random rng;

    public SpawnHelper(Random rng) {
        this.rng = rng;
    }

    /** Returns a random position within the world bounds, respecting margin and entity size. */
    public float randomPosition(float worldExtent, float entitySize, float margin) {
        return margin + rng.nextFloat() * (worldExtent - entitySize - margin * 2);
    }

    /** Returns a random velocity between -maxSpeed and +maxSpeed. Returns 0 if maxSpeed is 0. */
    public float randomVelocity(float maxSpeed) {
        if (maxSpeed <= 0) return 0f;
        return -maxSpeed + rng.nextFloat() * (maxSpeed * 2);
    }
}
