package io.github.some_example_name.core;

import io.github.some_example_name.collision.CollisionManager;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.input.InputManager;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.output.OutputManager;
import io.github.some_example_name.scene.SceneManager;

public class EngineCore {

    // All managers (composition - EngineCore owns these)
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private OutputManager outputManager;
    private InputManager inputManager;

    // Game loop control
    private boolean running;
    private static final double TARGET_FPS = 60.0;
    private static final double TARGET_FRAME_TIME = 1.0 / TARGET_FPS; // in seconds

    public EngineCore() {
        // Initialize all managers
        entityManager = new EntityManager();
        sceneManager = new SceneManager();
        inputManager = new InputManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        outputManager = new OutputManager();

        running = false;
    }

    /**
     * Main game loop
     */
    public void run() {
        running = true;

        long lastTime = System.nanoTime();
        double deltaTime = 0;

        while (running) {
            // Calculate delta time
            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert to seconds
            lastTime = currentTime;

            // Cap delta time to avoid spiral of death
            if (deltaTime > 0.25) {
                deltaTime = 0.25;
            }

            // Core loop
            update((float) deltaTime);
            render();

            // Frame rate limiting (optional)
            sleep(currentTime);
        }

        shutdown();
    }

    /**
     * Update all game systems
     */
    public void update(float dt) {
//        // 1. Poll input
//        inputManager.poll();
//
//        // 2. Update current scene (handles scene-specific logic)
//        sceneManager.update(dt);
//
//        // 3. Update all entities
//        entityManager.updateAll(dt);
//
//        // 4. Apply movement/physics
//        movementManager.apply(dt, entityManager);
//
//        // 5. Detect and resolve collisions
//        collisionManager.detectAll(entityManager);
//        collisionManager.resolveAll(entityManager);
    }

    /**
     * Render the game
     */
    private void render() {
        // outputManager.display(entityManager);

        // Optional: debug visuals
        // outputManager.drawDebug(entityManager);
    }

    /**
     * Frame rate limiter
     */
    private void sleep(long frameStartTime) {
        long frameTime = System.nanoTime() - frameStartTime;
        long targetNanos = (long) (TARGET_FRAME_TIME * 1_000_000_000);

        if (frameTime < targetNanos) {
            try {
                Thread.sleep((targetNanos - frameTime) / 1_000_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Clean shutdown
     */
    public void shutdown() {
        running = false;

        // Cleanup in reverse order of dependency
//        sceneManager.getCurrentScene().unload();
//        outputManager.getSoundManager().stopSound();

        // Nullify references (helps GC)
        entityManager = null;
        sceneManager = null;
        inputManager = null;
        movementManager = null;
        collisionManager = null;
        outputManager = null;
    }

    /**
     * Stop the game loop
     */
    public void stop() {
        running = false;
    }

    // ============ Getters for managers ============
    // (So scenes/entities can access them if needed)

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public OutputManager getOutputManager() {
        return outputManager;
    }

    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public MovementManager getMovementManager() {
        return movementManager;
    }
}
