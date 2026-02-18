package io.github.some_example_name.scene;

public class SceneManager {
    private Scene currentScene;
    private Scene pendingScene;

    public void setScene(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("scene cannot be null");
        }
        pendingScene = null;
        applySceneChange(scene, null);
    }

    public void queueScene(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("scene cannot be null");
        }
        pendingScene = scene;
    }

    public void switchScene() {
        if (pendingScene == null) {
            return;
        }
        Scene nextScene = pendingScene;
        pendingScene = null;
        applySceneChange(nextScene, new TransitionData());
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void update(float dt) {
        if (currentScene != null) {
            currentScene.update(dt);
        }
    }

    public void dispose() {
        pendingScene = null;
        if (currentScene != null) {
            currentScene.exit();
            currentScene.unload();
            currentScene = null;
        }
    }

    private void applySceneChange(Scene nextScene, TransitionData transitionData) {
        if (nextScene == currentScene) {
            return;
        }

        if (currentScene != null) {
            currentScene.exit();
            currentScene.unload();
        }

        currentScene = nextScene;
        currentScene.load();
        currentScene.enter(transitionData);
    }
}
