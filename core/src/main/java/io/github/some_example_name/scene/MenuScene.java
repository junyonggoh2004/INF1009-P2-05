package io.github.some_example_name.scene;

/**
 * Menu screen — press ENTER to start the game.
 * No entities needed, just a title display.
 */
public class MenuScene extends Scene {

    public MenuScene() {
        super("Menu");
    }

    @Override
    public void update(float dt) {
        // handled by HealthyEatingGame
    }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0.2f, 0.6f, 0.3f, 1f);
    }
}