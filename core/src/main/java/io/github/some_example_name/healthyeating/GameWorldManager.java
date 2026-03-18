package io.github.some_example_name.healthyeating;

import java.util.Random;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.factory.PlayerEntityFactory;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;

/**
 * Manages world simulation: food respawns, player clamping, food wrapping.
 */
public class GameWorldManager {

    private static final float SPAWN_MARGIN = 60f;
    private final Random rng = new Random();

    /** Ticks food timers and respawns collected food at random positions. */
    public void handleRespawns(float dt, EntityManager em, MovementManager mm,
                               float worldW, float worldH) {
        for (Entity e : em.getActiveEntities()) {
            FoodTag food = e.getComponent(FoodTag.class);
            if (food == null) continue;

            food.tick(dt);

            if (food.isReadyToRespawn()) {
                food.respawn();
                Sprite sprite = e.getComponent(Sprite.class);
                if (sprite != null) sprite.setVisible(true);

                Transform t = mm.getTransform(e.getId());
                if (t != null) {
                    float size = (sprite != null) ? sprite.getWidth() : 35f;
                    t.setPosition(
                        SPAWN_MARGIN + rng.nextFloat() * (worldW - size - SPAWN_MARGIN * 2),
                        SPAWN_MARGIN + rng.nextFloat() * (worldH - size - SPAWN_MARGIN * 2));
                }
            }
        }
    }

    /** Clamps a player entity within screen bounds. */
    public void clampPlayerToScreen(Entity player, MovementManager mm,
                                    float worldW, float worldH) {
        if (player == null) return;
        Transform t = mm.getTransform(player.getId());
        if (t == null) return;
        float size = PlayerEntityFactory.PLAYER_SIZE;
        if (t.getX() < 0) t.setX(0);
        if (t.getY() < 0) t.setY(0);
        if (t.getX() + size > worldW) t.setX(worldW - size);
        if (t.getY() + size > worldH) t.setY(worldH - size);
    }

    /** Wraps food entities around screen edges. */
    public void wrapFood(EntityManager em, MovementManager mm,
                         float worldW, float worldH) {
        for (Entity e : em.getActiveEntities()) {
            if (!e.hasComponent(FoodTag.class)) continue;
            Sprite sprite = e.getComponent(Sprite.class);
            float size = (sprite != null) ? sprite.getWidth() : 35f;
            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            if (t.getX() < -size) t.setX(worldW);
            if (t.getX() > worldW) t.setX(-size);
            if (t.getY() < -size) t.setY(worldH);
            if (t.getY() > worldH) t.setY(-size);
        }
    }
}
