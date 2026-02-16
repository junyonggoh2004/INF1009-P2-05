package io.github.some_example_name.collision;

import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.entity.Entity;

public interface CollisionResolver {
    void resolve(Entity a, Collider aCol, Transform aTr,
                 Entity b, Collider bCol, Transform bTr);
}
