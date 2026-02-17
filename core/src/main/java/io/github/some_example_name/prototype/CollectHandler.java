package io.github.some_example_name.prototype;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.Sprite;

/**
 * Collision handler attached to the PLAYER entity.
 * When the player touches a collectible circle, this handler:
 * 1. Marks the circle as collected (starts respawn timer)
 * 2. Hides the circle (sets Sprite invisible)
 * 3. Increments the score
 */
public class CollectHandler implements CollisionHandler {

    private int score;

    public CollectHandler() {
        this.score = 0;
    }

    @Override
    public void onCollision(Entity self, Entity other) {
        // Only act on entities that have a Collectible component
        Collectible col = other.getComponent(Collectible.class);
        if (col == null || col.isCollected()) return;

        // Mark as collected (starts respawn countdown)
        col.collect();

        // Hide the circle visually
        Sprite sprite = other.getComponent(Sprite.class);
        if (sprite != null) {
            sprite.setVisible(false);
        }

        score++;
        System.out.println("Collected! Score: " + score);
    }

    public int getScore() {
        return score;
    }
}