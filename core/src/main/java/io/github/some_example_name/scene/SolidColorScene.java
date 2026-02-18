package io.github.some_example_name.scene;

/**
 * Generic scene implementation that renders a solid background color.
 */
public class SolidColorScene extends Scene {
    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public SolidColorScene(String sceneName, float r, float g, float b, float a) {
        super(sceneName);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public void update(float dt) {
        // No-op: color scene has no per-frame state.
    }

    @Override
    public void render(Renderer renderer) {
        if (renderer != null) {
            renderer.clear(r, g, b, a);
        }
    }
}
