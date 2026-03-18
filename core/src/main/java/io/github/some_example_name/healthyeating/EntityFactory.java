package io.github.some_example_name.healthyeating;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Factory for creating player and food entities (Factory Method pattern).
 */
public class EntityFactory {

    public static final float PLAYER_SIZE = 70f;
    public static final int MAX_HEARTS = 5;

    /**
     * Creates two players with shared HealthBar and ScoreTracker.
     * P1 spawns at worldW/3, P2 at worldW*2/3.
     * Returns {player1, player2}.
     */
    public Entity[] createPlayers(EntityManager em, MovementManager mm,
                                  Stage stage, int threshold,
                                  float worldW, float worldH) {
        HealthBar sharedHealth = new HealthBar(MAX_HEARTS);
        ScoreTracker sharedScore = new ScoreTracker(threshold);

        Entity p1 = createPlayerInternal(em, mm, stage, sharedHealth, sharedScore, 1,
                worldW / 3f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f);
        Entity p2 = createPlayerInternal(em, mm, stage, sharedHealth, sharedScore, 2,
                worldW * 2f / 3f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f);

        return new Entity[]{p1, p2};
    }

    private Entity createPlayerInternal(EntityManager em, MovementManager mm,
                                        Stage stage, HealthBar health, ScoreTracker score,
                                        int playerNumber, float spawnX, float spawnY) {
        Entity player = em.createEntity();

        PlayerTag tag = new PlayerTag(playerNumber);
        tag.setCurrentStage(stage);
        player.add(tag);

        Sprite sprite = new Sprite("player", PLAYER_SIZE, PLAYER_SIZE);
        sprite.setColor(0.3f, 0.5f, 1f, 1f);
        player.add(sprite);

        player.add(new Collider(PLAYER_SIZE, PLAYER_SIZE, 0, 0, 0, false));
        player.add(health);
        player.add(score);

        FoodCollectHandler handler = new FoodCollectHandler();
        player.add(handler);

        mm.register(player.getId(), new Transform(spawnX, spawnY), new Motion());

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
