package io.github.some_example_name.scene;

/**
 * Abstract scene lifecycle contract.
 * Concrete scenes can be reused across different games by providing
 * their own update/render behavior while sharing this lifecycle shape.
 */
public abstract class Scene {
    /**
     * Renderer abstraction passed to scenes.
     * Concrete engines/projects can adapt this to their rendering backend.
     */
    public interface Renderer {
        void clear(float r, float g, float b, float a);
    }

    protected String sceneName;

    protected Scene(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void load() {
        // Optional hook for asset/resource setup.
    }

    public void enter(TransitionData data) {
        // Optional hook for transition-in state.
    }

    public abstract void update(float dt);

    public abstract void render(Renderer renderer);

    public void exit() {
        // Optional hook for transition-out state.
    }

    public void unload() {
        // Optional hook for resource cleanup.
    }
}
