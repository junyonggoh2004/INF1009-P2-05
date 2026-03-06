package io.github.some_example_name.healthyeating;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;

/**
 * Collision handler attached to the player entity.
 * When the player collides with a food entity:
 * - Healthy food: adds score
 * - Unhealthy food / cigarette: loses hearts
 *
 * Implements CollisionHandler (Observer pattern).
 */
public class FoodCollectHandler implements CollisionHandler {

    private boolean levelUpTriggered = false;
    private boolean gameOverTriggered = false;
    private boolean lastCollectHealthy = false;
    private boolean lastCollectUnhealthy = false;

    @Override
    public void onEnter(Entity self, Entity other) {
        FoodTag food = other.getComponent(FoodTag.class);
        if (food == null || food.isCollected()) return;

        ScoreTracker tracker = self.getComponent(ScoreTracker.class);
        HealthBar health = self.getComponent(HealthBar.class);
        if (tracker == null || health == null) return;

        int points = food.getPoints();

        if (points > 0) {
            // Healthy food — add to score
            tracker.addScore(points);
            lastCollectHealthy = true;
            lastCollectUnhealthy = false;
        } else {
            // Unhealthy food or cigarette — lose hearts
            health.loseHearts(Math.abs(points));
            lastCollectHealthy = false;
            lastCollectUnhealthy = true;
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

    public boolean isLevelUpTriggered()     { return levelUpTriggered; }
    public boolean isGameOverTriggered()    { return gameOverTriggered; }
    public boolean wasLastCollectHealthy()  { return lastCollectHealthy; }
    public boolean wasLastCollectUnhealthy(){ return lastCollectUnhealthy; }

    public void clearLevelUp()     { levelUpTriggered = false; }
    public void clearGameOver()    { gameOverTriggered = false; }
    public void clearLastCollect() { lastCollectHealthy = false; lastCollectUnhealthy = false; }
}