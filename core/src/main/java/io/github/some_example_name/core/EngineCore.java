package io.github.some_example_name.core;

import io.github.some_example_name.collision.CollisionManager;
import io.github.some_example_name.collision.RectCollisionDetector;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.input.InputManager;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.output.OutputManager;
import io.github.some_example_name.scene.SceneManager;
import io.github.some_example_name.inputoutput.IOManager;
public class EngineCore {

    // All managers (composition - EngineCore owns these)
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private SceneManager sceneManager;
    private OutputManager outputManager;
    private InputManager inputManager;
    private IOManager ioManager;

    /** Number of collision layers **/
    private static final int COLLISION_LAYERS = 3;

    /**
     * Initializes all managers and wires them together.
     * Call this once from ApplicationAdapter.create().
     */
    public void init() {
        // Create managers
        entityManager = new EntityManager();
        sceneManager = new SceneManager();
        inputManager = new InputManager();
        outputManager = new OutputManager();
        ioManager = new IOManager();

        // Movement must init before collision (collision depends on it)
        movementManager = new MovementManager();
        movementManager.init();

        // Collision: pass detector, resolver is null (game sets it later)
        collisionManager = new CollisionManager();
        collisionManager.init(movementManager, new RectCollisionDetector(), null, COLLISION_LAYERS);
    }

    /**
     * Updates all game systems in the correct order.
     * Call this every frame from ApplicationAdapter.render().
     *
     * @param dt delta time in seconds
     */
    public void update(float dt) {
        if (dt <= 0) return;

        // 1. Process input/output
        ioManager.update(dt);

        // 2. Entity-level updates (currently a hook)
        entityManager.updateAll(dt);

        // 3. Apply movement / physics
        movementManager.update(dt, entityManager);

        // 4. Detect and resolve collisions
        collisionManager.update(dt, entityManager);
    }

    /**
     * Cleans up all managers in reverse order of dependency.
     * Call this from ApplicationAdapter.dispose().
     */
    public void dispose() {
        if (collisionManager != null) collisionManager.dispose();
        if (movementManager != null) movementManager.dispose();
        if (ioManager != null) ioManager.dispose();

        entityManager = null;
        movementManager = null;
        collisionManager = null;
        sceneManager = null;
        outputManager = null;
        inputManager = null;
        ioManager = null;
    }

    // ============ Getters for managers ============

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public MovementManager getMovementManager() {
        return movementManager;
    }

    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public OutputManager getOutputManager() {
        return outputManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public IOManager getIOManager() {
        return ioManager;
    }
}