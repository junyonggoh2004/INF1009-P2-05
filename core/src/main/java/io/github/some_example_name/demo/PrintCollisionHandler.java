
package io.github.some_example_name.demo;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Component;
import io.github.some_example_name.entity.Entity;

public class PrintCollisionHandler implements CollisionHandler, Component {
    private boolean printed = false;

    @Override
    public void onCollision(Entity self, Entity other) {
        if (printed) return;
        printed = true;
        System.out.println("COLLISION DETECTED: " + self.getId() + " hit " + other.getId());
    }
}
