
package io.github.some_example_name.collision;

public final class CollisionMath {
    private CollisionMath() {}

    /**
     * Performs AABB (Axis-Aligned Bounding Box) collision check.
     *
     * Parameters ax, ay and bx, by are WORLD positions
     * (already including collider offsets).
     *
     * Returns true if rectangles overlap.
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
