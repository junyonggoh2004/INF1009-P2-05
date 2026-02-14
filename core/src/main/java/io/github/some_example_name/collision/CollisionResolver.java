package io.github.some_example_name.collision;

import io.github.some_example_name.movement.Transform;

public interface CollisionResolver {
    void resolve(Collider aCol, Collider bCol,
                 Transform aTr, Transform bTr,
                 float ax, float ay, float bx, float by);
}
