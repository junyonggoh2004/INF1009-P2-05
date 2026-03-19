package io.github.some_example_name.integration;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: verifies physics pipeline across multiple frames using
 * real EntityManager and MovementManager with PhysicsCalculation.
 */
class PhysicsIntegrationTest {

    private EntityManager em;
    private MovementManager mm;

    @BeforeEach
    void setUp() {
        em = new EntityManager();
        mm = new MovementManager();
        mm.init();
    }

    @Test
    void entityMovesOverMultipleFrames() {
        Entity e = em.createEntity();
        Transform t = new Transform(0, 0);
        Motion m = new Motion(100f, 50f);
        mm.register(e.getId(), t, m);

        // Simulate 10 frames at 0.1s each = 1 second total
        for (int i = 0; i < 10; i++) {
            mm.update(0.1f, em);
        }

        assertEquals(100f, t.getX(), 1f);  // 100 px/s * 1s
        assertEquals(50f, t.getY(), 1f);   // 50 px/s * 1s
    }

    @Test
    void accelerationIncreasesVelocityOverTime() {
        Entity e = em.createEntity();
        Transform t = new Transform(0, 0);
        Motion m = new Motion(0f, 0f, 100f, 0f, 0f, false);
        mm.register(e.getId(), t, m);

        mm.update(1.0f, em);

        // After 1s: vx = 0 + 100*1 = 100, position = 0 + 0.5*100*1^2 = 50 (approx, since discrete)
        assertEquals(100f, m.getVx(), 0.1f);
        assertEquals(100f, t.getX(), 1f);
    }

    @Test
    void speedClampingLimitsVelocity() {
        Entity e = em.createEntity();
        Transform t = new Transform(0, 0);
        Motion m = new Motion(1000f, 1000f, 0f, 0f, 200f, false);
        mm.register(e.getId(), t, m);

        mm.update(0.016f, em);

        float speed = (float) Math.sqrt(m.getVx() * m.getVx() + m.getVy() * m.getVy());
        assertEquals(200f, speed, 1f);
    }

    @Test
    void gravityPullsEntityDown() {
        Entity e = em.createEntity();
        Transform t = new Transform(0, 100);
        Motion m = new Motion(0f, 0f, 0f, 0f, 0f, true);
        mm.register(e.getId(), t, m);

        // Run several frames
        for (int i = 0; i < 60; i++) {
            mm.update(0.016f, em);
        }

        // Y should decrease (gravity pulls down in libGDX)
        assertTrue(t.getY() < 100f, "Gravity should move entity downward, y=" + t.getY());
        assertTrue(m.getVy() < 0, "Vertical velocity should be negative, vy=" + m.getVy());
    }

    @Test
    void inactiveEntityDoesNotMove() {
        Entity e = em.createEntity();
        Transform t = new Transform(50, 50);
        Motion m = new Motion(100f, 100f);
        mm.register(e.getId(), t, m);

        e.setActive(false);
        mm.update(1.0f, em);

        assertEquals(50f, t.getX());
        assertEquals(50f, t.getY());
    }

    @Test
    void multipleEntitiesMoveIndependently() {
        Entity e1 = em.createEntity();
        Transform t1 = new Transform(0, 0);
        Motion m1 = new Motion(100f, 0f);
        mm.register(e1.getId(), t1, m1);

        Entity e2 = em.createEntity();
        Transform t2 = new Transform(0, 0);
        Motion m2 = new Motion(0f, 200f);
        mm.register(e2.getId(), t2, m2);

        mm.update(1.0f, em);

        assertEquals(100f, t1.getX(), 0.1f);
        assertEquals(0f, t1.getY(), 0.1f);
        assertEquals(0f, t2.getX(), 0.1f);
        assertEquals(200f, t2.getY(), 0.1f);
    }

    @Test
    void zeroVelocityEntityStaysStationary() {
        Entity e = em.createEntity();
        Transform t = new Transform(100, 200);
        Motion m = new Motion(0f, 0f);
        mm.register(e.getId(), t, m);

        for (int i = 0; i < 100; i++) {
            mm.update(0.016f, em);
        }

        assertEquals(100f, t.getX(), 0.01f);
        assertEquals(200f, t.getY(), 0.01f);
    }

    @Test
    void unregisteredEntityNotAffected() {
        Entity e = em.createEntity();
        Transform t = new Transform(0, 0);
        Motion m = new Motion(100f, 100f);
        mm.register(e.getId(), t, m);

        mm.unregister(e.getId());
        mm.update(1.0f, em);

        // Position unchanged since unregistered
        assertEquals(0f, t.getX());
        assertEquals(0f, t.getY());
    }
}
