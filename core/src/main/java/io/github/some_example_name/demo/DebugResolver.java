package io.github.some_example_name.demo;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.CollisionResolver;
import io.github.some_example_name.movement.Transform;

public class DebugResolver implements CollisionResolver {

    @Override
    public void resolve(Collider aCol, Collider bCol,
                        Transform aTr, Transform bTr,
                        float ax, float ay, float bx, float by) {
        // Part 1: do nothing (no physics / no game logic).
        // You can log if you want, but it will spam:
        // System.out.println("Resolver called");
    }
}
