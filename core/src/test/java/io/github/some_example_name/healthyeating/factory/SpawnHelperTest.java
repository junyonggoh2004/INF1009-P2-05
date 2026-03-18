package io.github.some_example_name.healthyeating.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

class SpawnHelperTest {

    @Test
    void randomPositionStaysWithinBounds() {
        SpawnHelper helper = new SpawnHelper(new Random(42));
        float worldW = 640f;
        float entitySize = 35f;
        float margin = 60f;

        for (int i = 0; i < 100; i++) {
            float pos = helper.randomPosition(worldW, entitySize, margin);
            assertTrue(pos >= margin, "Position below margin: " + pos);
            assertTrue(pos <= worldW - entitySize - margin, "Position above bound: " + pos);
        }
    }

    @Test
    void randomVelocityWithinRange() {
        SpawnHelper helper = new SpawnHelper(new Random(42));
        float maxSpeed = 150f;

        for (int i = 0; i < 100; i++) {
            float vel = helper.randomVelocity(maxSpeed);
            assertTrue(vel >= -maxSpeed, "Velocity below -max: " + vel);
            assertTrue(vel <= maxSpeed, "Velocity above max: " + vel);
        }
    }

    @Test
    void zeroMaxSpeedReturnsZeroVelocity() {
        SpawnHelper helper = new SpawnHelper(new Random(42));
        assertEquals(0f, helper.randomVelocity(0f));
    }
}
