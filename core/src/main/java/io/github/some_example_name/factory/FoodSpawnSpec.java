package io.github.some_example_name.factory;

import io.github.some_example_name.healthyeating.FoodTag.FoodType;

/**
 * Value type describing how to spawn a single food entity.
 * Reduces constructor argument sprawl in FoodEntityFactory.
 */
public class FoodSpawnSpec {

    private final FoodType type;
    private final String texturePath;
    private final float size;
    private final float respawnDelay;
    private final float x;
    private final float y;
    private final float vx;
    private final float vy;

    public FoodSpawnSpec(FoodType type, String texturePath, float size,
                         float respawnDelay, float x, float y, float vx, float vy) {
        this.type = type;
        this.texturePath = texturePath;
        this.size = size;
        this.respawnDelay = respawnDelay;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public FoodType getType()       { return type; }
    public String getTexturePath()  { return texturePath; }
    public float getSize()          { return size; }
    public float getRespawnDelay()  { return respawnDelay; }
    public float getX()             { return x; }
    public float getY()             { return y; }
    public float getVx()            { return vx; }
    public float getVy()            { return vy; }
}
