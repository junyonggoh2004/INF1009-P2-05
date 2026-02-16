package io.github.some_example_name.collision;

import io.github.some_example_name.movement.Transform;

/**
 * Checks if two rectangular colliders overlap.
 *
 * How it works:
 * Each collider has a size (width/height).
 * Each entity has a position stored in Transform.
 * We calculate the world position of both rectangles.
 * Then we check if their areas overlap. *
 */
public class RectCollisionDetector implements CollisionDetector {

    @Override
    public boolean intersects(Collider a, Transform ta, Collider b, Transform tb) {
        
        // If anything is missing, they cannot collide

    	if (a == null || b == null || ta == null || tb == null) return false;

        // Calculate the actual position of each rectangle
        float ax = ta.getX() + a.getOffsetX();
        float ay = ta.getY() + a.getOffsetY();
        float bx = tb.getX() + b.getOffsetX();
        float by = tb.getY() + b.getOffsetY();

        // Calculate the right and top edges
        float aRight = ax + a.getWidth();
        float aBottom = ay + a.getHeight();
        float bRight = bx + b.getWidth();
        float bBottom = by + b.getHeight();

        // Two rectangles overlap if:
        // - A's right side is past B's left side
        // - A's left side is before B's right side
        // - A's top is above B's bottom
        // - A's bottom is below B's top
        return (aRight > bx) && (ax < bRight) &&
               (aBottom > by) && (ay < bBottom);
    }
}
