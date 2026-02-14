package io.github.some_example_name.collision;

import java.util.HashMap;
import java.util.Map;

/**
 * CollisionMath:
 * - holds pure collision formula
 * - provides ONE entry point: intersects(...)
 */
public final class CollisionMath {
    private CollisionMath() {}

    @FunctionalInterface
    private interface Intersector {
        boolean test(Collider a, Collider b, float ax, float ay, float bx, float by);
    }

    // (shapeA, shapeB) -> collision function
    private static final Map<Long, Intersector> RULES = new HashMap<>();

    static {
        // Register supported pairs  
        register(Collider.Shape.RECT, Collider.Shape.RECT, CollisionMath::checkAabb);

          }

    /**
     * Main entry point used by CollisionManager.
     */
    public static boolean intersects(Collider a, Collider b,
                                     float ax, float ay,
                                     float bx, float by) {

        if (a == null || b == null) return false;

        Intersector fn = RULES.get(key(a.getShape(), b.getShape()));
        if (fn == null) return false; // unsupported shape pair
        return fn.test(a, b, ax, ay, bx, by);
    }

    /**
     * register(newShapeA, newShapeB, newFunction)
     */
    public static void register(Collider.Shape a, Collider.Shape b, Intersector fn) {
        if (a == null || b == null || fn == null) return;
        RULES.put(key(a, b), fn);
    }

    private static long key(Collider.Shape a, Collider.Shape b) {
        return (((long) a.ordinal()) << 32) | (b.ordinal() & 0xffffffffL);
    }

    // RECT vs RECT (AABB)
    /**
     * AABB collision check.
     * ax,ay and bx,by = WORLD positions (already including offsets).
     */
    public static boolean checkAabb(Collider a, Collider b,
                                    float ax, float ay,
                                    float bx, float by) {

        float aLeft   = ax;
        float aRight  = ax + a.getWidth();
        float aTop    = ay;
        float aBottom = ay + a.getHeight();

        float bLeft   = bx;
        float bRight  = bx + b.getWidth();
        float bTop    = by;
        float bBottom = by + b.getHeight();

        return (aRight > bLeft) &&
               (aLeft < bRight) &&
               (aBottom > bTop) &&
               (aTop < bBottom);
    }
}