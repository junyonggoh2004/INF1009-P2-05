package io.github.some_example_name.scene;

/**
 * Generic data object used when entering a new scene.
 * Keeps transition payload minimal and engine-agnostic.
 */
public class TransitionData {
    public float spawnX;
    public float spawnY;
    public float cameraX;
    public float cameraY;

    public TransitionData() {
        this(0f, 0f, 0f, 0f);
    }

    public TransitionData(float spawnX, float spawnY, float cameraX, float cameraY) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
    }
}
