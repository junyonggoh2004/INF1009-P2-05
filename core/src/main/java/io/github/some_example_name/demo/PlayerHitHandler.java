package io.github.some_example_name.demo;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Entity;

public class PlayerHitHandler implements CollisionHandler {

    private float hitFlashTimer = 0f;

    @Override
    public void onCollision(Entity self, Entity other) {
        // flash red
        hitFlashTimer = 0.25f;

        // mark bomb to be removed by DemoGame (no manager calls here)
        if (other != null) {
            other.add(new RemoveMe());
        }
    }

    public boolean isHitFlashActive() {
        return hitFlashTimer > 0f;
    }

    public void tick(float dt) {
        if (hitFlashTimer > 0f) hitFlashTimer -= dt;
        if (hitFlashTimer < 0f) hitFlashTimer = 0f;
    }
}
