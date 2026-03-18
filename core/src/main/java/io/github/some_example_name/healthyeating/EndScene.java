package io.github.some_example_name.healthyeating;

import io.github.some_example_name.scene.Scene;

/**
 * End screen — shows congratulations or game over.
 */
public class EndScene extends Scene {

    private boolean isVictory;

    public EndScene() {
        super("End");
        this.isVictory = false;
    }

    public void setVictory(boolean victory) {
        this.isVictory = victory;
    }

    public boolean isVictory() {
        return isVictory;
    }

    @Override
    public void update(float dt) { }

    @Override
    public void render(Renderer renderer) {
        if (isVictory) {
            renderer.clear(0.2f, 0.7f, 0.3f, 1f);
        } else {
            renderer.clear(0.5f, 0.1f, 0.1f, 1f);
        }
    }
}
