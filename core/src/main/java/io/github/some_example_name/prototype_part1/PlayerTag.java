package io.github.some_example_name.prototype_part1;

import io.github.some_example_name.entity.Component;

/**
 * Tag component that identifies an entity as the player.
 *
 * Tag components carry no data — they simply mark an entity
 * so that systems can find it. This follows the ECS pattern
 * where behavior is determined by which components an entity has.
 *
 * Example Usage:
 * <pre>
 *     Entity player = em.createEntity();
 *     player.add(new PlayerTag());
 *
 *     // Later, find the player:
 *     for (Entity e : em.getActiveEntities()) {
 *         if (e.hasComponent(PlayerTag.class)) {
 *             // this is the player
 *         }
 *     }
 * </pre>
 */
public class PlayerTag implements Component {
    // Marker only — no fields or methods needed
}