package io.github.some_example_name.healthyeating;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.Sprite;

/**
 * Collision handler attached to the player entity.
 * When the player collides with a food entity:
 * - Healthy food: adds score
 * - Unhealthy food: loses 1 heart
 * - Older unhealthy (alcohol/vape): loses 2 hearts
 * - Cigarette: loses 3 hearts
 * - Medicine: heals 1 heart
 *
 * Implements CollisionHandler (Observer pattern).
 */
public class FoodCollectHandler implements CollisionHandler {

    private boolean levelUpTriggered = false;
    private boolean gameOverTriggered = false;
    private boolean lastCollectHealthy = false;
    private boolean lastCollectUnhealthy = false;
    private boolean lastCollectMedicine = false;
    private boolean lastCollectOlder = false;

    @Override
    public void onEnter(Entity self, Entity other) {
        FoodTag food = other.getComponent(FoodTag.class);
        if (food == null || food.isCollected()) return;

        ScoreTracker tracker = self.getComponent(ScoreTracker.class);
        HealthBar health = self.getComponent(HealthBar.class);
        if (tracker == null || health == null) return;

        clearLastCollect();

        PlayerTag tag = self.getComponent(PlayerTag.class);

        switch (food.getType()) {
            case HEALTHY:
                tracker.addScore(food.getPoints());
                lastCollectHealthy = true;
                if (tag != null) tag.setExpression(PlayerTag.Expression.EATING);
                break;
            case MEDICINE:
                health.gainHearts(food.getPoints());
                lastCollectMedicine = true;
                if (tag != null) tag.setExpression(PlayerTag.Expression.EATING);
                break;
            case OLDER:
                health.loseHearts(Math.abs(food.getPoints()));
                lastCollectOlder = true;
                if (tag != null) tag.setExpression(PlayerTag.Expression.SAD);
                break;
            case UNHEALTHY:
            case CIGARETTE:
                health.loseHearts(Math.abs(food.getPoints()));
                lastCollectUnhealthy = true;
                if (tag != null) tag.setExpression(PlayerTag.Expression.SAD);
                break;
        }

        // Mark food as collected and hide it
        food.collect();
        Sprite sprite = other.getComponent(Sprite.class);
        if (sprite != null) sprite.setVisible(false);

        // Check level up
        if (tracker.hasReachedThreshold()) {
            levelUpTriggered = true;
        }

        // Check game over
        if (health.isDead()) {
            gameOverTriggered = true;
        }
    }

    @Override
    public void onStay(Entity self, Entity other) { }

    @Override
    public void onExit(Entity self, Entity other) { }

    // ─── State checks ───

    public boolean isLevelUpTriggered()      { return levelUpTriggered; }
    public boolean isGameOverTriggered()     { return gameOverTriggered; }
    public boolean wasLastCollectHealthy()   { return lastCollectHealthy; }
    public boolean wasLastCollectUnhealthy() { return lastCollectUnhealthy; }
    public boolean wasLastCollectMedicine()  { return lastCollectMedicine; }
    public boolean wasLastCollectOlder()     { return lastCollectOlder; }

    public void clearLevelUp()     { levelUpTriggered = false; }
    public void clearGameOver()    { gameOverTriggered = false; }
    public void clearLastCollect() {
        lastCollectHealthy = false;
        lastCollectUnhealthy = false;
        lastCollectMedicine = false;
        lastCollectOlder = false;
    }
}
