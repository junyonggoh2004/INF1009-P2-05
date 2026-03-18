package io.github.some_example_name.healthyeating.factory;

import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Value type describing how to spawn a player entity.
 * Reduces constructor argument sprawl in PlayerEntityFactory.
 */
public class PlayerSpawnSpec {

    private final Stage stage;
    private final int playerNumber;
    private final float spawnX;
    private final float spawnY;
    private final float size;

    public PlayerSpawnSpec(Stage stage, int playerNumber,
                           float spawnX, float spawnY, float size) {
        this.stage = stage;
        this.playerNumber = playerNumber;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.size = size;
    }

    public Stage getStage()         { return stage; }
    public int getPlayerNumber()    { return playerNumber; }
    public float getSpawnX()        { return spawnX; }
    public float getSpawnY()        { return spawnY; }
    public float getSize()          { return size; }
}
