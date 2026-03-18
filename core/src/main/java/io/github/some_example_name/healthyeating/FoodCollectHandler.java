package io.github.some_example_name.healthyeating;

import io.github.some_example_name.collision.CollisionHandler;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.FoodTag.EffectType;
import io.github.some_example_name.healthyeating.FoodTag.FlashType;

/**
 * Collision handler attached to the player entity.
 * Switches on EffectType (SCORE/HEAL/DAMAGE) — stable categories that don't grow
 * when new FoodTypes are added. Visual feedback is driven by FlashType from FoodTag.
 *
 * Implements CollisionHandler (Observer pattern).
 */
public class FoodCollectHandler implements CollisionHandler {

    private boolean levelUpTriggered = false;
    private boolean gameOverTriggered = false;
    private FlashType lastFlash = FlashType.NONE;
    private boolean lastCollected = false;

    @Override
    public void onEnter(Entity self, Entity other) {
        FoodTag food = other.getComponent(FoodTag.class);
        if (food == null || food.isCollected()) return;

        ScoreTracker tracker = self.getComponent(ScoreTracker.class);
        HealthBar health = self.getComponent(HealthBar.class);
        if (tracker == null || health == null) return;

        clearLastCollect();

        PlayerTag tag = self.getComponent(PlayerTag.class);

        // Switch on effect type (3 stable cases) — not food type (5+ growing cases)
        switch (food.getEffectType()) {
            case SCORE:
                tracker.addScore(food.getPoints());
                if (tag != null) tag.setExpression(PlayerTag.Expression.EATING);
                break;
            case HEAL:
                health.gainHearts(food.getPoints());
                if (tag != null) tag.setExpression(PlayerTag.Expression.EATING);
                break;
            case DAMAGE:
                health.loseHearts(Math.abs(food.getPoints()));
                if (tag != null) tag.setExpression(PlayerTag.Expression.SAD);
                break;
        }

        // Store flash type for GameStateManager to read
        lastFlash = food.getFlashType();
        lastCollected = true;

        // Mark food as collected and hide it
        food.collect();
        Sprite sprite = other.getComponent(Sprite.class);
        if (sprite != null) sprite.setVisible(false);

        if (tracker.hasReachedThreshold()) {
            levelUpTriggered = true;
        }
        if (health.isDead()) {
            gameOverTriggered = true;
        }
    }

    // ─── State checks ───

    public boolean isLevelUpTriggered()   { return levelUpTriggered; }
    public boolean isGameOverTriggered()  { return gameOverTriggered; }
    public boolean hasCollected()         { return lastCollected; }
    public FlashType getLastFlashType()   { return lastFlash; }

    public void clearLevelUp()     { levelUpTriggered = false; }
    public void clearGameOver()    { gameOverTriggered = false; }
    public void clearLastCollect() {
        lastCollected = false;
        lastFlash = FlashType.NONE;
    }
}
