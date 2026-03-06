package io.github.some_example_name.scene;

/**
 * End screen — shows congratulations or game over.
 * Press ENTER to return to menu.
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
    public void update(float dt) {
        // handled by HealthyEatingGame
    }

    @Override
    public void render(Renderer renderer) {
        if (isVictory) {
            // Gold/green for victory
            renderer.clear(0.2f, 0.7f, 0.3f, 1f);
        } else {
            // Dark red for game over
            renderer.clear(0.5f, 0.1f, 0.1f, 1f);
        }
    }
}