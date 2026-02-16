package io.github.some_example_name.collision;

import io.github.some_example_name.movement.Transform;

/**
 * CollisionDetector
 *
 *Any class that implements this interface decides
 *how to check if two colliders overlap.
 *
 * This allows us to have different detection strategies
 * (e.g., rectangle, circle, polygon) without changing
 * the CollisionManager.
 */
public interface CollisionDetector {
    boolean intersects(Collider a, Transform ta, Collider b, Transform tb);
}

