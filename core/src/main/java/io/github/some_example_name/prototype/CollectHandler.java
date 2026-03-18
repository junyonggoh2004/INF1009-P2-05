package io.github.some_example_name.prototype;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.inputoutput.output.AudioManager;

/**
 * Global collision callback.
 * When the player touches a collectible circle, this handler:
 * 1. Marks the circle as collected (starts respawn timer)
 * 2. Hides the circle (sets Sprite invisible)
 * 3. Increments the score
 */
public class CollectHandler implements CollisionHandler {

    private int score;
    private AudioManager audioManager;

    public CollectHandler(AudioManager audioManager) {
        this.score = 0;
        this.audioManager = audioManager;
    }

    @Override
    public void onEnter(Entity a, Entity b) {
        // We only care about collisions that involve the player
        boolean aIsPlayer = a.hasComponent(PlayerTag.class);
        boolean bIsPlayer = b.hasComponent(PlayerTag.class);

        if (!aIsPlayer && !bIsPlayer) return; // ignore non-player collisions

        Entity other = aIsPlayer ? b : a;

        // Only act on entities that have a Collectible component
        Collectible col = other.getComponent(Collectible.class);
        if (col == null || col.isCollected()) return;

        // Mark as collected (starts respawn countdown)
        col.collect();

        // Hide the circle visually
        Sprite sprite = other.getComponent(Sprite.class);
        if (sprite != null) sprite.setVisible(false);

        score++;
        System.out.println("Collected! Score: " + score);

        // Trigger sound effect
        if (audioManager != null) {
            audioManager.playAudio("bubble_click");
        }
    }
    
    public int getScore() {
        return score;
    }


}