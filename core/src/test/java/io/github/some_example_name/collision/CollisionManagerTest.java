package io.github.some_example_name.collision;

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

class CollisionManagerTest {

    private EntityManager em;
    private MovementManager mm;
    private CollisionManager cm;

    /** Test handler that records collision events. */
    static class TestHandler implements CollisionHandler {
        final List<String> events = new ArrayList<>();

        @Override
        public void onEnter(Entity self, Entity other) {
            events.add("ENTER:" + self.getId() + ":" + other.getId());
        }

        @Override
        public void onStay(Entity self, Entity other) {
            events.add("STAY:" + self.getId() + ":" + other.getId());
        }

        @Override
        public void onExit(Entity self, Entity other) {
            events.add("EXIT:" + self.getId() + ":" + other.getId());
        }
    }

    @BeforeEach
    void setUp() {
        em = new EntityManager();
        mm = new MovementManager();
        mm.init();
        cm = new CollisionManager();
        cm.init(mm, new RectCollisionDetector(), new NoResolver(), 3);
    }

    private Entity createEntity(float x, float y, float size, int layer) {
        Entity e = em.createEntity();
        e.add(new Collider(size, size, 0, 0, layer, false));
        mm.register(e.getId(), new Transform(x, y), new Motion());
        return e;
    }

    @Test
    void enterFiresOnFirstContact() {
        TestHandler h1 = new TestHandler();
        Entity a = createEntity(0, 0, 20, 0);
        a.add(h1);
        Entity b = createEntity(5, 5, 20, 0);

        cm.update(0.016f, em);

        assertTrue(h1.events.stream().anyMatch(e -> e.startsWith("ENTER:")));
        assertFalse(h1.events.stream().anyMatch(e -> e.startsWith("STAY:")));
    }

    @Test
    void stayFiresOnSubsequentFrames() {
        TestHandler h1 = new TestHandler();
        Entity a = createEntity(0, 0, 20, 0);
        a.add(h1);
        Entity b = createEntity(5, 5, 20, 0);

        cm.update(0.016f, em); // Frame 1: Enter
        h1.events.clear();

        cm.update(0.016f, em); // Frame 2: Stay
        assertTrue(h1.events.stream().anyMatch(e -> e.startsWith("STAY:")));
        assertFalse(h1.events.stream().anyMatch(e -> e.startsWith("ENTER:")));
    }

    @Test
    void exitFiresWhenContactEnds() {
        TestHandler h1 = new TestHandler();
        Entity a = createEntity(0, 0, 20, 0);
        a.add(h1);
        Entity b = createEntity(5, 5, 20, 0);

        cm.update(0.016f, em); // Frame 1: colliding
        h1.events.clear();

        // Move b far away
        mm.getTransform(b.getId()).setPosition(1000, 1000);
        cm.update(0.016f, em); // Frame 2: no longer colliding

        assertTrue(h1.events.stream().anyMatch(e -> e.startsWith("EXIT:")));
    }

    @Test
    void noEventWhenNotColliding() {
        TestHandler h1 = new TestHandler();
        Entity a = createEntity(0, 0, 10, 0);
        a.add(h1);
        Entity b = createEntity(100, 100, 10, 0);

        cm.update(0.016f, em);

        assertTrue(h1.events.isEmpty());
    }

    @Test
    void layerFilteringPreventsCollision() {
        cm.setLayerCollision(0, 1, false);

        TestHandler h1 = new TestHandler();
        Entity a = createEntity(0, 0, 20, 0);
        a.add(h1);
        Entity b = createEntity(5, 5, 20, 1);

        cm.update(0.016f, em);

        assertTrue(h1.events.isEmpty());
    }

    @Test
    void layerFilteringAllowsCollision() {
        cm.setLayerCollision(0, 1, true);

        TestHandler h1 = new TestHandler();
        Entity a = createEntity(0, 0, 20, 0);
        a.add(h1);
        Entity b = createEntity(5, 5, 20, 1);

        cm.update(0.016f, em);

        assertFalse(h1.events.isEmpty());
    }

    @Test
    void bothEntitiesReceiveEvents() {
        TestHandler h1 = new TestHandler();
        TestHandler h2 = new TestHandler();
        Entity a = createEntity(0, 0, 20, 0);
        a.add(h1);
        Entity b = createEntity(5, 5, 20, 0);
        b.add(h2);

        cm.update(0.016f, em);

        assertFalse(h1.events.isEmpty());
        assertFalse(h2.events.isEmpty());
    }

    @Test
    void defaultHandlerMethodsDoNotThrow() {
        // Handler with only onEnter — onStay/onExit use defaults
        CollisionHandler minimal = new CollisionHandler() {
            @Override
            public void onEnter(Entity self, Entity other) { }
        };

        Entity a = createEntity(0, 0, 20, 0);
        a.add(minimal);
        Entity b = createEntity(5, 5, 20, 0);

        // Should not throw on any frame
        cm.update(0.016f, em);
        cm.update(0.016f, em);
        mm.getTransform(b.getId()).setPosition(1000, 1000);
        cm.update(0.016f, em);
    }
}
