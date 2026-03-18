package io.github.some_example_name.factory;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.FoodCollectHandler;
import io.github.some_example_name.healthyeating.FoodTag;
import io.github.some_example_name.healthyeating.HealthBar;
import io.github.some_example_name.healthyeating.PlayerTag;
import io.github.some_example_name.healthyeating.ScoreTracker;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;

/**
 * Factory Pattern — creates player entities.
 * Supports single player and multiplayer (shared HealthBar/ScoreTracker).
 */
public class PlayerEntityFactory {

    public static final float PLAYER_SIZE = 70f;
    public static final int SINGLEPLAYER_HEARTS = 5;
    public static final int MULTIPLAYER_HEARTS = 10;

    /**
     * Creates a single player centered on screen.
     * Returns {player, null} for consistency with multiplayer.
     */
    public Entity[] createSinglePlayer(EntityManager em, MovementManager mm,
                                       PlayerTag.Stage stage, int threshold,
                                       float worldW, float worldH) {
        HealthBar health = new HealthBar(SINGLEPLAYER_HEARTS);
        ScoreTracker score = new ScoreTracker(threshold);

        PlayerSpawnSpec spec = new PlayerSpawnSpec(stage, 1, SINGLEPLAYER_HEARTS, threshold,
                worldW / 2f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f, PLAYER_SIZE);

        Entity p1 = createFromSpec(spec, health, score, em, mm);
        return new Entity[]{p1, null};
    }

    /**
     * Creates two players with shared HealthBar (10 HP) and ScoreTracker.
     * P1 spawns at worldW/3, P2 at worldW*2/3.
     * Returns {player1, player2}.
     */
    public Entity[] createPlayers(EntityManager em, MovementManager mm,
                                  PlayerTag.Stage stage, int threshold,
                                  float worldW, float worldH) {
        HealthBar sharedHealth = new HealthBar(MULTIPLAYER_HEARTS);
        ScoreTracker sharedScore = new ScoreTracker(threshold);

        PlayerSpawnSpec spec1 = new PlayerSpawnSpec(stage, 1, MULTIPLAYER_HEARTS, threshold,
                worldW / 3f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f, PLAYER_SIZE);
        PlayerSpawnSpec spec2 = new PlayerSpawnSpec(stage, 2, MULTIPLAYER_HEARTS, threshold,
                worldW * 2f / 3f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f, PLAYER_SIZE);

        Entity p1 = createFromSpec(spec1, sharedHealth, sharedScore, em, mm);
        Entity p2 = createFromSpec(spec2, sharedHealth, sharedScore, em, mm);
        return new Entity[]{p1, p2};
    }

    private Entity createFromSpec(PlayerSpawnSpec spec, HealthBar health, ScoreTracker score,
                                  EntityManager em, MovementManager mm) {
        Entity player = em.createEntity();

        PlayerTag tag = new PlayerTag(spec.getPlayerNumber());
        tag.setCurrentStage(spec.getStage());
        player.add(tag);

        Sprite sprite = new Sprite("player", spec.getSize(), spec.getSize());
        sprite.setColor(0.3f, 0.5f, 1f, 1f);
        player.add(sprite);

        player.add(new Collider(spec.getSize(), spec.getSize(), 0, 0, 0, false));
        player.add(health);
        player.add(score);
        player.add(new FoodCollectHandler());

        mm.register(player.getId(),
                new Transform(spec.getSpawnX(), spec.getSpawnY()),
                new Motion());

        return player;
    }

    /** Destroys all food entities. */
    public void clearFoodEntities(EntityManager em, MovementManager mm) {
        for (Entity e : em.getActiveEntities()) {
            if (e.hasComponent(FoodTag.class)) {
                mm.unregister(e.getId());
                em.destroyEntity(e.getId());
            }
        }
    }

    /** Destroys all entities. */
    public void clearAllEntities(EntityManager em, MovementManager mm) {
        for (Entity e : em.getActiveEntities()) {
            mm.unregister(e.getId());
        }
        em.clear();
    }
}
