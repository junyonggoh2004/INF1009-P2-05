package io.github.some_example_name.core;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main {
    public static void main(String[] args) {
        EngineCore engine = new EngineCore();

        // Load initial scene
//        engine.getSceneManager().setScene(new MainMenuScene());

        // Start the game
        engine.run();
    }
}
