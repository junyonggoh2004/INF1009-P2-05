package io.github.some_example_name.prototype;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.CollisionResolver;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.movement.Transform;

/**
 * A collision resolver that does nothing.
 * Collisions are detected and handlers are notified,
 * but no physical separation/pushing occurs.
 */
public class NoResolver implements CollisionResolver {

    @Override
    public void resolve(Entity a, Collider aCol, Transform aTr,
                        Entity b, Collider bCol, Transform bTr) {
        // Intentionally empty — no physical resolution
    }
}