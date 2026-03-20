package io.github.some_example_name.healthyeating;

import io.github.some_example_name.scene.Scene;

/**
 * Settings screen scene.
 */
public class SettingsScene extends Scene {

    public SettingsScene() {
        super("Settings");
    }

    @Override
    public void update(float dt) { }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0.15f, 0.2f, 0.2f, 1f);
    }
}

