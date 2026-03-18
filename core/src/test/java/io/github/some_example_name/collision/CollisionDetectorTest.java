package io.github.some_example_name.collision;

import io.github.some_example_name.movement.Transform;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CollisionDetectorTest {

    private final CollisionDetector detector = new RectCollisionDetector();

    @Test
    void overlappingRectsCollide() {
        Collider a = new Collider(10, 10, 0, 0, 0, false);
        Collider b = new Collider(10, 10, 0, 0, 0, false);
        Transform ta = new Transform(0, 0);
        Transform tb = new Transform(5, 5);
        assertTrue(detector.intersects(a, ta, b, tb));
    }

    @Test
    void nonOverlappingRectsDoNotCollide() {
        Collider a = new Collider(10, 10, 0, 0, 0, false);
        Collider b = new Collider(10, 10, 0, 0, 0, false);
        Transform ta = new Transform(0, 0);
        Transform tb = new Transform(20, 20);
        assertFalse(detector.intersects(a, ta, b, tb));
    }

    @Test
    void edgeTouchingRectsDoNotCollide() {
        Collider a = new Collider(10, 10, 0, 0, 0, false);
        Collider b = new Collider(10, 10, 0, 0, 0, false);
        Transform ta = new Transform(0, 0);
        Transform tb = new Transform(10, 0);
        assertFalse(detector.intersects(a, ta, b, tb));
    }

    @Test
    void samePositionCollides() {
        Collider a = new Collider(10, 10, 0, 0, 0, false);
        Collider b = new Collider(10, 10, 0, 0, 0, false);
        Transform ta = new Transform(5, 5);
        Transform tb = new Transform(5, 5);
        assertTrue(detector.intersects(a, ta, b, tb));
    }

    @Test
    void offsetAffectsCollision() {
        Collider a = new Collider(10, 10, 0, 0, 0, false);
        Collider b = new Collider(10, 10, 100, 0, 0, false);
        Transform ta = new Transform(0, 0);
        Transform tb = new Transform(5, 0);
        // b's effective x = 5 + 100 = 105, so no overlap
        assertFalse(detector.intersects(a, ta, b, tb));
    }
}
