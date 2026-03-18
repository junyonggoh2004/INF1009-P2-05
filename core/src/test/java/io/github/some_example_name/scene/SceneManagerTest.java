package io.github.some_example_name.scene;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SceneManagerTest {

    /** Minimal concrete scene for testing */
    static class TestScene extends Scene {
        boolean loaded = false;
        boolean exited = false;

        TestScene(String name) { super(name); }

        @Override public void load() { loaded = true; }
        @Override public void update(float dt) { }
        @Override public void render(Renderer renderer) { }
        @Override public void exit() { exited = true; }
    }

    @Test
    void initialSceneIsNull() {
        SceneManager sm = new SceneManager();
        assertNull(sm.getCurrentScene());
    }

    @Test
    void setSceneChangesCurrentScene() {
        SceneManager sm = new SceneManager();
        TestScene scene = new TestScene("Test");
        sm.setScene(scene);
        assertSame(scene, sm.getCurrentScene());
    }

    @Test
    void setSceneCallsLoadOnNewScene() {
        SceneManager sm = new SceneManager();
        TestScene scene = new TestScene("Test");
        sm.setScene(scene);
        assertTrue(scene.loaded);
    }

    @Test
    void setSceneCallsExitOnPreviousScene() {
        SceneManager sm = new SceneManager();
        TestScene first = new TestScene("First");
        TestScene second = new TestScene("Second");
        sm.setScene(first);
        sm.setScene(second);
        assertTrue(first.exited);
        assertSame(second, sm.getCurrentScene());
    }

    @Test
    void sceneNameIsStored() {
        TestScene scene = new TestScene("MyScene");
        assertEquals("MyScene", scene.getSceneName());
    }
}
