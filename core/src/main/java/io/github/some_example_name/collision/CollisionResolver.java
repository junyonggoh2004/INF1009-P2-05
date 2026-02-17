package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.movement.Transform;

/**
 * Interface for resolving collisions physically.
 * Implementations decide what happens when two entities overlap
 * (e.g., push apart, bounce, stop movement).
 */
public interface CollisionResolver {
    void resolve(Entity a, Collider aCol, Transform aTr,
                 Entity b, Collider bCol, Transform bTr);
}