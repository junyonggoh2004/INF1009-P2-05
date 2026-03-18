package io.github.some_example_name.movement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MotionTest {

    @Test
    void defaultConstructorSetsZero() {
        Motion m = new Motion();
        assertEquals(0f, m.getVx());
        assertEquals(0f, m.getVy());
    }

    @Test
    void velocityConstructor() {
        Motion m = new Motion(5f, -3f);
        assertEquals(5f, m.getVx());
        assertEquals(-3f, m.getVy());
    }

    @Test
    void setVelocity() {
        Motion m = new Motion();
        m.setVx(100f);
        m.setVy(-50f);
        assertEquals(100f, m.getVx());
        assertEquals(-50f, m.getVy());
    }

    @Test
    void accelerationDefaults() {
        Motion m = new Motion();
        assertEquals(0f, m.getAx());
        assertEquals(0f, m.getAy());
    }

    @Test
    void fullConstructor() {
        Motion m = new Motion(1f, 2f, 3f, 4f, 500f, true);
        assertEquals(1f, m.getVx());
        assertEquals(2f, m.getVy());
        assertEquals(3f, m.getAx());
        assertEquals(4f, m.getAy());
        assertEquals(500f, m.getMaxSpeed());
        assertTrue(m.isGravityEnabled());
    }
}
