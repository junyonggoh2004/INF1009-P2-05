package io.github.some_example_name.collision;

/**
 * CollisionDetector:
 * -  detection math
 */
public final class CollisionDetector {
    private CollisionDetector() {}

    /**
     * Entry point used by CollisionManager.
     * ax, ay, bx, by are WORLD positions .
     */
    public static boolean intersects(Collider a, Collider b,
                                     float ax, float ay,
                                     float bx, float by) {
        if (a == null || b == null) return false;

        // For now only RECT is supported
        if (a.getShape() != Collider.Shape.RECT || b.getShape() != Collider.Shape.RECT) return false;

        return aabb(a, b, ax, ay, bx, by);
    }

    private static boolean aabb(Collider a, Collider b,
                                float ax, float ay,
                                float bx, float by) {

        float aRight  = ax + a.getWidth();
        float aBottom = ay + a.getHeight();

        float bRight  = bx + b.getWidth();
        float bBottom = by + b.getHeight();

        return (aRight > bx) &&
               (ax < bRight) &&
               (aBottom > by) &&
               (ay < bBottom);
    }
}
