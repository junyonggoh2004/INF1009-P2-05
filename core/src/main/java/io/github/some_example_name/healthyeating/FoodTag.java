package io.github.some_example_name.healthyeating;

import io.github.some_example_name.entity.Component;

/**
 * Component that marks an entity as food and stores its point value and effect.
 * Each FoodType maps to an EffectType (what happens on collect) and a FlashType (visual feedback).
 * Adding a new FoodType only requires defining its effect/flash here — no changes to collision logic.
 */
public class FoodTag implements Component {

    public enum FoodType {
        HEALTHY,
        UNHEALTHY,
        CIGARETTE,
        MEDICINE,
        OLDER
    }

    /** What happens to the player when this food is collected. */
    public enum EffectType {
        SCORE,   // adds points to ScoreTracker
        HEAL,    // restores hearts on HealthBar
        DAMAGE   // removes hearts from HealthBar
    }

    /** What screen flash to trigger on collect. */
    public enum FlashType {
        NONE,    // no flash (healthy food)
        RED,     // regular unhealthy
        GREEN,   // medicine / healing
        PURPLE   // older / substance items
    }

    private final FoodType type;
    private final EffectType effectType;
    private final FlashType flashType;
    private final int points;
    private boolean collected;
    private float respawnTimer;
    private final float respawnDelay;

    public FoodTag(FoodType type, float respawnDelay) {
        this.type = type;
        this.respawnDelay = respawnDelay;
        this.collected = false;
        this.respawnTimer = 0f;

        switch (type) {
            case HEALTHY:
                this.points = 1;
                this.effectType = EffectType.SCORE;
                this.flashType = FlashType.NONE;
                break;
            case UNHEALTHY:
                this.points = -1;
                this.effectType = EffectType.DAMAGE;
                this.flashType = FlashType.RED;
                break;
            case CIGARETTE:
                this.points = -2;
                this.effectType = EffectType.DAMAGE;
                this.flashType = FlashType.RED;
                break;
            case OLDER:
                this.points = -2;
                this.effectType = EffectType.DAMAGE;
                this.flashType = FlashType.PURPLE;
                break;
            case MEDICINE:
                this.points = 2;
                this.effectType = EffectType.HEAL;
                this.flashType = FlashType.GREEN;
                break;
            default:
                this.points = 0;
                this.effectType = EffectType.SCORE;
                this.flashType = FlashType.NONE;
                break;
        }
    }

    public FoodType getType()        { return type; }
    public EffectType getEffectType(){ return effectType; }
    public FlashType getFlashType()  { return flashType; }
    public int getPoints()           { return points; }
    public boolean isCollected()     { return collected; }

    public void collect() {
        collected = true;
        respawnTimer = respawnDelay;
    }

    public void tick(float dt) {
        if (collected) {
            respawnTimer -= dt;
        }
    }

    public boolean isReadyToRespawn() {
        return collected && respawnTimer <= 0f;
    }

    public void respawn() {
        collected = false;
        respawnTimer = 0f;
    }
}
