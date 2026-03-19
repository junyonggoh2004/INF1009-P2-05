package io.github.some_example_name.integration;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.FoodCollectHandler;
import io.github.some_example_name.healthyeating.HealthBar;
import io.github.some_example_name.healthyeating.PlayerTag;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;
import io.github.some_example_name.healthyeating.ScoreTracker;
import io.github.some_example_name.healthyeating.factory.PlayerEntityFactory;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: verifies PlayerEntityFactory creates properly configured
 * player entities with all required components, correct positions, and shared
 * components in multiplayer.
 */
class PlayerCreationPipelineTest {

    private EntityManager em;
    private MovementManager mm;
    private PlayerEntityFactory factory;

    private static final float WORLD_W = 640f;
    private static final float WORLD_H = 480f;

    @BeforeEach
    void setUp() {
        em = new EntityManager();
        mm = new MovementManager();
        mm.init();
        factory = new PlayerEntityFactory();
    }

    @Test
    void singlePlayerHasAllComponents() {
        Entity[] players = factory.createSinglePlayer(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        Entity p1 = players[0];

        assertNotNull(p1);
        assertNull(players[1]);
        assertNotNull(p1.getComponent(PlayerTag.class));
        assertNotNull(p1.getComponent(Sprite.class));
        assertNotNull(p1.getComponent(Collider.class));
        assertNotNull(p1.getComponent(HealthBar.class));
        assertNotNull(p1.getComponent(ScoreTracker.class));
        assertNotNull(p1.getComponent(FoodCollectHandler.class));
        assertNotNull(mm.getTransform(p1.getId()));
        assertNotNull(mm.getMotion(p1.getId()));
    }

    @Test
    void singlePlayerHas5HP() {
        Entity[] players = factory.createSinglePlayer(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        HealthBar health = players[0].getComponent(HealthBar.class);
        assertEquals(5, health.getMaxHearts());
        assertEquals(5, health.getHearts());
    }

    @Test
    void singlePlayerCentered() {
        Entity[] players = factory.createSinglePlayer(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        Transform t = mm.getTransform(players[0].getId());
        float expectedX = WORLD_W / 2f - PlayerEntityFactory.PLAYER_SIZE / 2f;
        float expectedY = WORLD_H / 2f - PlayerEntityFactory.PLAYER_SIZE / 2f;
        assertEquals(expectedX, t.getX(), 0.01f);
        assertEquals(expectedY, t.getY(), 0.01f);
    }

    @Test
    void multiplayerBothHaveAllComponents() {
        Entity[] players = factory.createPlayers(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        Entity p1 = players[0];
        Entity p2 = players[1];

        assertNotNull(p1);
        assertNotNull(p2);

        for (Entity p : players) {
            assertNotNull(p.getComponent(PlayerTag.class));
            assertNotNull(p.getComponent(Sprite.class));
            assertNotNull(p.getComponent(Collider.class));
            assertNotNull(p.getComponent(HealthBar.class));
            assertNotNull(p.getComponent(ScoreTracker.class));
            assertNotNull(p.getComponent(FoodCollectHandler.class));
            assertNotNull(mm.getTransform(p.getId()));
        }
    }

    @Test
    void multiplayerSharesHealthAndScore() {
        Entity[] players = factory.createPlayers(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        HealthBar h1 = players[0].getComponent(HealthBar.class);
        HealthBar h2 = players[1].getComponent(HealthBar.class);
        ScoreTracker s1 = players[0].getComponent(ScoreTracker.class);
        ScoreTracker s2 = players[1].getComponent(ScoreTracker.class);

        // Same object reference — shared, not copies
        assertSame(h1, h2);
        assertSame(s1, s2);
    }

    @Test
    void multiplayerHas10HP() {
        Entity[] players = factory.createPlayers(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        HealthBar health = players[0].getComponent(HealthBar.class);
        assertEquals(10, health.getMaxHearts());
    }

    @Test
    void multiplayerPositionedAtThirds() {
        Entity[] players = factory.createPlayers(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        float size = PlayerEntityFactory.PLAYER_SIZE;

        Transform t1 = mm.getTransform(players[0].getId());
        Transform t2 = mm.getTransform(players[1].getId());

        assertEquals(WORLD_W / 3f - size / 2f, t1.getX(), 0.01f);
        assertEquals(WORLD_W * 2f / 3f - size / 2f, t2.getX(), 0.01f);
    }

    @Test
    void playerNumbersAreCorrect() {
        Entity[] players = factory.createPlayers(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        assertEquals(1, players[0].getComponent(PlayerTag.class).getPlayerNumber());
        assertEquals(2, players[1].getComponent(PlayerTag.class).getPlayerNumber());
    }

    @Test
    void multiplayerSharedDamageAffectsBoth() {
        Entity[] players = factory.createPlayers(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        HealthBar shared = players[0].getComponent(HealthBar.class);

        // P1 takes damage
        shared.loseHearts(3);

        // P2 sees the damage because it's the same object
        assertEquals(7, players[1].getComponent(HealthBar.class).getHearts());
    }

    @Test
    void clearAllEntitiesRemovesEverything() {
        factory.createPlayers(em, mm, Stage.TODDLER, 10, WORLD_W, WORLD_H);
        assertEquals(2, em.getActiveEntities().size());

        factory.clearAllEntities(em, mm);
        assertEquals(0, em.count());
    }
}
