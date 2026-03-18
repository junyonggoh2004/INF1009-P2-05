package io.github.some_example_name.healthyeating;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;
import io.github.some_example_name.healthyeating.FoodTag.FlashType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FoodCollectHandlerTest {

    private Entity player;
    private FoodCollectHandler handler;
    private HealthBar health;
    private ScoreTracker tracker;

    @BeforeEach
    void setUp() {
        player = new Entity(1);
        health = new HealthBar(10);
        tracker = new ScoreTracker(10);
        handler = new FoodCollectHandler();
        player.add(health);
        player.add(tracker);
        player.add(handler);
        player.add(new PlayerTag(1));
    }

    private Entity createFood(FoodType type) {
        Entity food = new Entity(100);
        food.add(new FoodTag(type, 2f));
        food.add(new Sprite("test", 10, 10));
        return food;
    }

    @Test
    void healthyFoodAddsScore() {
        Entity food = createFood(FoodType.HEALTHY);
        handler.onEnter(player, food);
        assertEquals(1, tracker.getScore());
        assertEquals(10, health.getHearts());
    }

    @Test
    void unhealthyFoodDamagesHealth() {
        Entity food = createFood(FoodType.UNHEALTHY);
        handler.onEnter(player, food);
        assertEquals(0, tracker.getScore());
        assertEquals(9, health.getHearts());
    }

    @Test
    void medicineFoodHeals() {
        health.loseHearts(5);
        Entity food = createFood(FoodType.MEDICINE);
        handler.onEnter(player, food);
        assertEquals(7, health.getHearts()); // was 5, heals +2
    }

    @Test
    void olderFoodDealsTwoDamage() {
        Entity food = createFood(FoodType.OLDER);
        handler.onEnter(player, food);
        assertEquals(8, health.getHearts());
    }

    @Test
    void cigaretteDealsTwoDamage() {
        Entity food = createFood(FoodType.CIGARETTE);
        handler.onEnter(player, food);
        assertEquals(8, health.getHearts());
    }

    @Test
    void foodIsCollectedAndHiddenAfterCollision() {
        Entity food = createFood(FoodType.HEALTHY);
        handler.onEnter(player, food);
        FoodTag tag = food.getComponent(FoodTag.class);
        assertTrue(tag.isCollected());
        Sprite sprite = food.getComponent(Sprite.class);
        assertFalse(sprite.isVisible());
    }

    @Test
    void levelUpTriggersAtThreshold() {
        tracker.setThreshold(1);
        Entity food = createFood(FoodType.HEALTHY);
        handler.onEnter(player, food);
        assertTrue(handler.isLevelUpTriggered());
    }

    @Test
    void gameOverTriggersAtZeroHealth() {
        health.loseHearts(9); // 1 HP left
        Entity food = createFood(FoodType.UNHEALTHY);
        handler.onEnter(player, food);
        assertTrue(handler.isGameOverTriggered());
    }

    @Test
    void flashTypeIsCorrectPerFoodType() {
        Entity healthy = createFood(FoodType.HEALTHY);
        handler.onEnter(player, healthy);
        assertEquals(FlashType.NONE, handler.getLastFlashType());

        handler.clearLastCollect();
        Entity unhealthy = createFood(FoodType.UNHEALTHY);
        handler.onEnter(player, unhealthy);
        assertEquals(FlashType.RED, handler.getLastFlashType());

        handler.clearLastCollect();
        Entity medicine = createFood(FoodType.MEDICINE);
        handler.onEnter(player, medicine);
        assertEquals(FlashType.GREEN, handler.getLastFlashType());

        handler.clearLastCollect();
        Entity older = createFood(FoodType.OLDER);
        handler.onEnter(player, older);
        assertEquals(FlashType.PURPLE, handler.getLastFlashType());
    }

    @Test
    void alreadyCollectedFoodIsIgnored() {
        Entity food = createFood(FoodType.HEALTHY);
        handler.onEnter(player, food); // first collect
        handler.clearLastCollect();
        handler.onEnter(player, food); // same food again — should be ignored
        assertEquals(1, tracker.getScore()); // still 1, not 2
    }

    @Test
    void expressionSetToEatingForHealthyFood() {
        Entity food = createFood(FoodType.HEALTHY);
        handler.onEnter(player, food);
        PlayerTag tag = player.getComponent(PlayerTag.class);
        assertEquals(PlayerTag.Expression.EATING, tag.getExpression());
    }

    @Test
    void expressionSetToSadForDamageFood() {
        Entity food = createFood(FoodType.UNHEALTHY);
        handler.onEnter(player, food);
        PlayerTag tag = player.getComponent(PlayerTag.class);
        assertEquals(PlayerTag.Expression.SAD, tag.getExpression());
    }
}
