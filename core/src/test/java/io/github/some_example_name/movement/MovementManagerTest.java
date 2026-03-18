package io.github.some_example_name.movement;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovementManagerTest {

    private MovementManager mm;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        mm = new MovementManager();
        mm.init();
        em = new EntityManager();
    }

    @Test
    void registerAndRetrieveTransform() {
        Transform t = new Transform(10f, 20f);
        mm.register(1, t, new Motion());
        assertSame(t, mm.getTransform(1));
        assertTrue(mm.hasTransform(1));
    }

    @Test
    void registerAndRetrieveMotion() {
        Motion m = new Motion(5f, 10f);
        mm.register(1, new Transform(), m);
        assertSame(m, mm.getMotion(1));
        assertTrue(mm.hasMotion(1));
    }

    @Test
    void unregisterRemovesBoth() {
        mm.register(1, new Transform(), new Motion());
        mm.unregister(1);
        assertNull(mm.getTransform(1));
        assertNull(mm.getMotion(1));
        assertFalse(mm.hasTransform(1));
    }

    @Test
    void applyMovesEntityByVelocity() {
        Entity e = em.createEntity();
        Transform t = new Transform(0f, 0f);
        Motion m = new Motion(100f, 200f);
        mm.register(e.getId(), t, m);

        mm.apply(1.0f, em);

        assertEquals(100f, t.getX(), 0.01f);
        assertEquals(200f, t.getY(), 0.01f);
    }

    @Test
    void applyWithDeltaTimeScalesCorrectly() {
        Entity e = em.createEntity();
        Transform t = new Transform(0f, 0f);
        Motion m = new Motion(100f, 0f);
        mm.register(e.getId(), t, m);

        mm.apply(0.5f, em);

        assertEquals(50f, t.getX(), 0.01f);
    }

    @Test
    void applySkipsInactiveEntities() {
        Entity e = em.createEntity();
        e.setActive(false);
        Transform t = new Transform(0f, 0f);
        mm.register(e.getId(), t, new Motion(100f, 0f));

        mm.apply(1.0f, em);

        assertEquals(0f, t.getX());
    }

    @Test
    void applyIgnoresZeroDeltaTime() {
        Entity e = em.createEntity();
        Transform t = new Transform(0f, 0f);
        mm.register(e.getId(), t, new Motion(100f, 0f));

        mm.apply(0f, em);
        assertEquals(0f, t.getX());
    }
}
