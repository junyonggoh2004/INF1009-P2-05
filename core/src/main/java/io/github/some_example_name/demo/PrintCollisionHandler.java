
package io.github.some_example_name.demo;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Entity;

public class PrintCollisionHandler implements CollisionHandler {
    @Override
    public void onCollision(Entity self, Entity other) {
        System.out.println("Collision ENTER: " + self.getId() + " with " + other.getId());
    }
}
