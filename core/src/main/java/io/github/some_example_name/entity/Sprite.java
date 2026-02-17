package io.github.some_example_name.entity;

public class Sprite implements Component {

    /** Name/path of the texture to draw */
    private String textureName;

    /** RGBA color tint (default white = no tint) */
    private float r, g, b, a;

    /** Draw dimensions in world units */
    private float width;
    private float height;

    /** Whether this entity should be rendered */
    private boolean visible;

    /**
     Creates a Sprite with the given texture and size.
     Color defaults to white (no tint), visible by default.
     @param textureName Path to the texture file (e.g., "triangle.png")
     @param width       Draw width in world units
     @param height      Draw height in world units
     */
    public Sprite(String textureName, float width, float height) {
        this.textureName = textureName;
        this.width = width;
        this.height = height;
        this.r = 1f;
        this.g = 1f;
        this.b = 1f;
        this.a = 1f;
        this.visible = true;
    }


    /**
     Sets the RGBA color tint.
     (1,1,1,1) = white/no tint. (1,0,0,1) = red tint.
     */
    public void setColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
    public float getA() { return a; }


    public String getTextureName() { return textureName; }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    // ==================== Size ====================

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    // ==================== Visibility ====================

    public boolean isVisible() { return visible; }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "Sprite{texture=" + textureName
                + ", size=" + width + "x" + height
                + ", color=(" + r + "," + g + "," + b + "," + a + ")"
                + ", visible=" + visible + "}";
    }
}