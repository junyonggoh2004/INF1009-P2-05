package io.github.some_example_name.healthyeating;

import io.github.some_example_name.entity.Component;

/**
 * Component that marks an entity as food and stores its point value.
 * Healthy food gives positive points, unhealthy food gives negative.
 */
public class FoodTag implements Component {

    public enum FoodType {
        HEALTHY,     // +1 score
        UNHEALTHY,   // -1 HP
        CIGARETTE,   // -2 HP (stage 3 only)
        MEDICINE,    // +2 HP heal (stage 3 only)
        OLDER        // -2 HP (stage 3 only — alcohol, vape, etc.)
    }

    private final FoodType type;
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
            case HEALTHY:    this.points = 1;  break;
            case UNHEALTHY:  this.points = -1; break;
            case OLDER:      this.points = -2; break;
            case CIGARETTE:  this.points = -2; break;
            case MEDICINE:   this.points = 2;  break;
            default:         this.points = 0;  break;
        }
    }

    public FoodType getType()    { return type; }
    public int getPoints()       { return points; }
    public boolean isCollected() { return collected; }

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