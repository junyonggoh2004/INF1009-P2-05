package io.github.some_example_name.factory;

import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Immutable value type holding all configuration for a game stage.
 * Used by StageContentFactory implementations to define stage rules.
 */
public class StageProfile {

    private final Stage stage;
    private final String sceneName;
    private final int scoreThreshold;
    private final int healthyCount;
    private final int unhealthyCount;
    private final int olderCount;
    private final int cigaretteCount;
    private final int medicineCount;
    private final float foodSize;
    private final float respawnDelay;
    private final float maxFoodSpeed;
    private final float spawnMargin;
    private final float bgR, bgG, bgB, bgA;

    public StageProfile(Stage stage, String sceneName, int scoreThreshold,
                        int healthyCount, int unhealthyCount,
                        int olderCount, int cigaretteCount, int medicineCount,
                        float foodSize, float respawnDelay, float maxFoodSpeed,
                        float spawnMargin,
                        float bgR, float bgG, float bgB, float bgA) {
        this.stage = stage;
        this.sceneName = sceneName;
        this.scoreThreshold = scoreThreshold;
        this.healthyCount = healthyCount;
        this.unhealthyCount = unhealthyCount;
        this.olderCount = olderCount;
        this.cigaretteCount = cigaretteCount;
        this.medicineCount = medicineCount;
        this.foodSize = foodSize;
        this.respawnDelay = respawnDelay;
        this.maxFoodSpeed = maxFoodSpeed;
        this.spawnMargin = spawnMargin;
        this.bgR = bgR;
        this.bgG = bgG;
        this.bgB = bgB;
        this.bgA = bgA;
    }

    public Stage getStage()          { return stage; }
    public String getSceneName()     { return sceneName; }
    public int getScoreThreshold()   { return scoreThreshold; }
    public int getHealthyCount()     { return healthyCount; }
    public int getUnhealthyCount()   { return unhealthyCount; }
    public int getOlderCount()       { return olderCount; }
    public int getCigaretteCount()   { return cigaretteCount; }
    public int getMedicineCount()    { return medicineCount; }
    public float getFoodSize()       { return foodSize; }
    public float getRespawnDelay()   { return respawnDelay; }
    public float getMaxFoodSpeed()   { return maxFoodSpeed; }
    public float getSpawnMargin()    { return spawnMargin; }
    public float getBgR()            { return bgR; }
    public float getBgG()            { return bgG; }
    public float getBgB()            { return bgB; }
    public float getBgA()            { return bgA; }
}
