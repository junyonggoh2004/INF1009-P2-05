package io.github.some_example_name.integration;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.FoodCollectHandler;
import io.github.some_example_name.healthyeating.FoodTag;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;
import io.github.some_example_name.healthyeating.FoodTag.FlashType;
import io.github.some_example_name.healthyeating.FoodTag.EffectType;
import io.github.some_example_name.healthyeating.HealthBar;
import io.github.some_example_name.healthyeating.PlayerTag;
import io.github.some_example_name.healthyeating.ScoreTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: verifies the full food collection pipeline from collision
 * through score/health changes, expression updates, level-up, and game-over triggers.
 */
class FoodCollectionPipelineTest {

    private Entity player;
    private HealthBar health;
    private ScoreTracker tracker;
    private FoodCollectHandler handler;
    private PlayerTag playerTag;

    @BeforeEach
    void setUp() {
        player = new Entity(1);
        health = new HealthBar(5);
        tracker = new ScoreTracker(3);
        handler = new FoodCollectHandler();
        playerTag = new PlayerTag(1);

        player.add(health);
        player.add(tracker);
        player.add(handler);
        player.add(playerTag);
    }

    private Entity createFood(FoodType type) {
        Entity food = new Entity(100);
        food.add(new FoodTag(type, 2f));
        food.add(new Sprite("test", 35, 35));
        return food;
    }

    @Test
    void collectMultipleHealthyFoodTriggersLevelUp() {
        // Threshold is 3, so collecting 3 healthy foods should trigger level-up
        for (int i = 0; i < 3; i++) {
            Entity food = new Entity(100 + i);
            food.add(new FoodTag(FoodType.HEALTHY, 2f));
            food.add(new Sprite("test", 35, 35));
            handler.onEnter(player, food);
        }

        assertEquals(3, tracker.getScore());
        assertTrue(tracker.hasReachedThreshold());
        assertTrue(handler.isLevelUpTriggered());
        assertFalse(handler.isGameOverTriggered());
    }

    @Test
    void collectUnhealthyUntilGameOver() {
        // 5 HP, unhealthy does -1 each
        for (int i = 0; i < 5; i++) {
            Entity food = new Entity(100 + i);
            food.add(new FoodTag(FoodType.UNHEALTHY, 2f));
            food.add(new Sprite("test", 35, 35));
            handler.onEnter(player, food);
        }

        assertEquals(0, health.getHearts());
        assertTrue(health.isDead());
        assertTrue(handler.isGameOverTriggered());
    }

    @Test
    void medicineHealsAfterDamage() {
        // Take damage first
        Entity unhealthy = createFood(FoodType.UNHEALTHY);
        handler.onEnter(player, unhealthy);
        assertEquals(4, health.getHearts());

        // Heal with medicine (+2)
        Entity medicine = createFood(FoodType.MEDICINE);
        handler.onEnter(player, medicine);
        assertEquals(5, health.getHearts()); // capped at max
    }

    @Test
    void medicineCannotExceedMaxHealth() {
        Entity medicine = createFood(FoodType.MEDICINE);
        handler.onEnter(player, medicine);
        assertEquals(5, health.getHearts()); // already at max, stays at max
    }

    @Test
    void mixedCollectionSequence() {
        // Collect: healthy, unhealthy, healthy, older, medicine, healthy
        handler.onEnter(player, createFood(FoodType.HEALTHY));
        assertEquals(1, tracker.getScore());
        assertEquals(5, health.getHearts());

        handler.onEnter(player, createFood(FoodType.UNHEALTHY));
        assertEquals(1, tracker.getScore()); // score unchanged by unhealthy
        assertEquals(4, health.getHearts());

        handler.onEnter(player, createFood(FoodType.HEALTHY));
        assertEquals(2, tracker.getScore());

        handler.onEnter(player, createFood(FoodType.OLDER));
        assertEquals(2, health.getHearts()); // -2 from older

        handler.onEnter(player, createFood(FoodType.MEDICINE));
        assertEquals(4, health.getHearts()); // +2 from medicine

        handler.onEnter(player, createFood(FoodType.HEALTHY));
        assertEquals(3, tracker.getScore());
        assertTrue(handler.isLevelUpTriggered());
    }

    @Test
    void flashTypesMatchEffectTypes() {
        handler.onEnter(player, createFood(FoodType.HEALTHY));
        assertEquals(FlashType.NONE, handler.getLastFlashType());

        handler.clearLastCollect();
        handler.onEnter(player, createFood(FoodType.UNHEALTHY));
        assertEquals(FlashType.RED, handler.getLastFlashType());

        handler.clearLastCollect();
        handler.onEnter(player, createFood(FoodType.MEDICINE));
        assertEquals(FlashType.GREEN, handler.getLastFlashType());

        handler.clearLastCollect();
        handler.onEnter(player, createFood(FoodType.OLDER));
        assertEquals(FlashType.PURPLE, handler.getLastFlashType());

        handler.clearLastCollect();
        handler.onEnter(player, createFood(FoodType.CIGARETTE));
        assertEquals(FlashType.RED, handler.getLastFlashType());
    }

    @Test
    void expressionChangesWithFoodType() {
        handler.onEnter(player, createFood(FoodType.HEALTHY));
        assertEquals(PlayerTag.Expression.EATING, playerTag.getExpression());

        // Simulate timer reset
        playerTag.updateExpression(1f);
        assertEquals(PlayerTag.Expression.DEFAULT, playerTag.getExpression());

        handler.onEnter(player, createFood(FoodType.UNHEALTHY));
        assertEquals(PlayerTag.Expression.SAD, playerTag.getExpression());
    }

    @Test
    void collectedFoodIsHiddenAndMarked() {
        Entity food = createFood(FoodType.HEALTHY);
        handler.onEnter(player, food);

        FoodTag tag = food.getComponent(FoodTag.class);
        assertTrue(tag.isCollected());

        Sprite sprite = food.getComponent(Sprite.class);
        assertFalse(sprite.isVisible());
    }

    @Test
    void alreadyCollectedFoodIgnored() {
        Entity food = createFood(FoodType.HEALTHY);
        handler.onEnter(player, food); // first collect
        handler.onEnter(player, food); // same food again

        assertEquals(1, tracker.getScore()); // not 2
    }
}
