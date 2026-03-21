package io.github.some_example_name.prototype_part1;

import io.github.some_example_name.entity.Component;

/**
 * Component for entities that can be collected by the player
 * and optionally respawn after a delay.
 *
 * Lifecycle:
 * 1. Entity starts with collected = false, visible on screen
 * 2. Player touches it → call collect()
 * 3. Game hides the entity (Sprite.setVisible(false))
 * 4. Each frame → call tick(dt) to count down respawn timer
 * 5. When isReadyToRespawn() returns true → call respawn()
 * 6. Game shows the entity again (Sprite.setVisible(true))
 *
 * Example Usage:
 * <pre>
 *     Entity circle = em.createEntity();
 *     circle.add(new Collectible(3.0f)); // respawns after 3 seconds
 *
 *     // On collision:
 *     Collectible c = circle.getComponent(Collectible.class);
 *     c.collect();
 *
 *     // Each frame:
 *     c.tick(dt);
 *     if (c.isReadyToRespawn()) {
 *         c.respawn();
 *     }
 * </pre>
 */
public class Collectible implements Component {

    /** How long (in seconds) before the entity respawns */
    private final float respawnDelay;

    /** Countdown timer (decrements each frame while collected) */
    private float respawnTimer;

    /** Whether this entity is currently collected (hidden) */
    private boolean collected;

    /**
     Creates a Collectible with the given respawn delay.
     @param respawnDelay Time in seconds before respawning after collection
     */
    public Collectible(float respawnDelay) {
        this.respawnDelay = respawnDelay;
        this.respawnTimer = 0f;
        this.collected = false;
    }

    /**
     Marks this entity as collected and starts the respawn timer.
     */
    public void collect() {
        this.collected = true;
        this.respawnTimer = respawnDelay;
    }

    /**
     Updates the respawn timer. Call every frame.
     @param dt Delta time in seconds
     */
    public void tick(float dt) {
        if (collected && respawnTimer > 0f) {
            respawnTimer -= dt;
        }
    }

    /**
     Checks if this entity is currently collected (hidden).
     @return true if collected and waiting to respawn
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     Checks if the respawn timer has elapsed.
     @return true if collected and timer has run out
     */
    public boolean isReadyToRespawn() {
        return collected && respawnTimer <= 0f;
    }

    /**
     Resets the entity to its uncollected state.
     Call this when making the entity visible again.
     */
    public void respawn() {
        this.collected = false;
        this.respawnTimer = 0f;
    }

    /**
     Gets the remaining time before respawn.
     @return Seconds remaining, or 0 if not collected
     */
    public float getRemainingTime() {
        return Math.max(0f, respawnTimer);
    }

    /**
     Gets the configured respawn delay.
     @return Respawn delay in seconds
     */
    public float getRespawnDelay() {
        return respawnDelay;
    }

    @Override
    public String toString() {
        return "Collectible{collected=" + collected
                + ", timer=" + String.format("%.1f", respawnTimer)
                + "/" + String.format("%.1f", respawnDelay) + "}";
    }
}