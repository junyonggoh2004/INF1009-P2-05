package io.github.some_example_name.demo;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.CollisionResolver;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.movement.Transform;

/**
 * Demo resolver:
 * When player collides with something, mark the OTHER entity for deletion.
 */
public class DebugResolver implements CollisionResolver {

    private final Entity player;

    public DebugResolver(Entity player) {
        this.player = player;
    }

    @Override
    public void resolve(Entity a, Collider aCol, Transform aTr,
                        Entity b, Collider bCol, Transform bTr) {

        // Only care about collisions involving the player
        Entity other = null;
        if (a == player) other = b;
        else if (b == player) other = a;
        else return;

        // Mark the other entity for removal (DemoGame will clean it up)
        if (other.getComponent(RemoveEntity.class) == null) {
            other.add(new RemoveEntity());
        }

           
    }
}
