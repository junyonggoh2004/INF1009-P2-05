package io.github.some_example_name.healthyeating;

import io.github.some_example_name.scene.Scene;

/**
 * Menu screen for the Healthy Eating game.
 */
public class MenuScene extends Scene {

    public MenuScene() {
        super("Menu");
    }

    @Override
    public void update(float dt) { }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0.2f, 0.6f, 0.3f, 1f);
    }
}
