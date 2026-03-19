package io.github.some_example_name.integration;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.collision.CollisionManager;
import io.github.some_example_name.collision.NoResolver;
import io.github.some_example_name.collision.RectCollisionDetector;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: verifies the full collision lifecycle (Enter → Stay → Exit)
 * across multiple frames using real EntityManager, MovementManager, and CollisionManager.
 */
class CollisionLifecycleTest {

    private EntityManager em;
    private MovementManager mm;
    private CollisionManager cm;
    private EventLog log;

    static class EventLog implements CollisionHandler {
        final List<String> events = new ArrayList<>();

        @Override
        public void onEnter(Entity self, Entity other) {
            events.add("ENTER");
        }

        @Override
        public void onStay(Entity self, Entity other) {
            events.add("STAY");
        }

        @Override
        public void onExit(Entity self, Entity other) {
            events.add("EXIT");
        }

        void clear() { events.clear(); }
    }

    @BeforeEach
    void setUp() {
        em = new EntityManager();
        mm = new MovementManager();
        mm.init();
        cm = new CollisionManager();
        cm.init(mm, new RectCollisionDetector(), new NoResolver(), 3);
        log = new EventLog();
    }

    private Entity createEntity(float x, float y, float size) {
        Entity e = em.createEntity();
        e.add(new Collider(size, size, 0, 0, 0, false));
        mm.register(e.getId(), new Transform(x, y), new Motion());
        return e;
    }

    @Test
    void fullLifecycle_enterThenStayThenExit() {
        Entity a = createEntity(0, 0, 20);
        a.add(log);
        Entity b = createEntity(5, 5, 20);

        // Frame 1: overlapping → ENTER
        cm.update(0.016f, em);
        assertEquals(1, log.events.size());
        assertEquals("ENTER", log.events.get(0));

        // Frame 2: still overlapping → STAY
        log.clear();
        cm.update(0.016f, em);
        assertEquals(1, log.events.size());
        assertEquals("STAY", log.events.get(0));

        // Frame 3: still overlapping → STAY again
        log.clear();
        cm.update(0.016f, em);
        assertEquals("STAY", log.events.get(0));

        // Frame 4: move apart → EXIT
        log.clear();
        mm.getTransform(b.getId()).setPosition(500, 500);
        cm.update(0.016f, em);
        assertEquals(1, log.events.size());
        assertEquals("EXIT", log.events.get(0));

        // Frame 5: still apart → no events
        log.clear();
        cm.update(0.016f, em);
        assertTrue(log.events.isEmpty());
    }

    @Test
    void reEnterAfterExit() {
        Entity a = createEntity(0, 0, 20);
        a.add(log);
        Entity b = createEntity(5, 5, 20);

        // Frame 1: ENTER
        cm.update(0.016f, em);
        assertEquals("ENTER", log.events.get(0));

        // Frame 2: move apart → EXIT
        log.clear();
        mm.getTransform(b.getId()).setPosition(500, 500);
        cm.update(0.016f, em);
        assertEquals("EXIT", log.events.get(0));

        // Frame 3: move back → ENTER again (not STAY)
        log.clear();
        mm.getTransform(b.getId()).setPosition(5, 5);
        cm.update(0.016f, em);
        assertEquals("ENTER", log.events.get(0));
    }

    @Test
    void movingEntityCausesCollisionViaPhysics() {
        Entity a = createEntity(0, 0, 20);
        a.add(log);

        // b starts nearby, moving towards a
        // After 0.016s: b.x = 30 - 800*0.016 = 30 - 12.8 = 17.2
        // b spans [17.2, 37.2], a spans [0, 20] → overlap at [17.2, 20]
        Entity b = createEntity(30, 0, 20);
        mm.getMotion(b.getId()).setVx(-800f);

        mm.update(0.016f, em);
        cm.update(0.016f, em);

        assertFalse(log.events.isEmpty(), "Movement should bring entities into collision");
        assertEquals("ENTER", log.events.get(0));
    }

    @Test
    void destroyedEntityDoesNotGenerateStay() {
        Entity a = createEntity(0, 0, 20);
        a.add(log);
        Entity b = createEntity(5, 5, 20);

        // Frame 1: ENTER
        cm.update(0.016f, em);
        assertEquals("ENTER", log.events.get(0));

        // Destroy b
        log.clear();
        b.setActive(false);
        cm.update(0.016f, em);

        // Should not get STAY since b is inactive
        assertFalse(log.events.contains("STAY"));
    }
}
