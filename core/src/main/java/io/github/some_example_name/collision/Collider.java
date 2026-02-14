package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Component;

/**
 * Collider component.
 * 
 * This class ONLY stores collision data.
 * 
 */
public class Collider implements Component {

    // Supported collision shapes (can extend later)
    public enum Shape { RECT }

    // Shape type of this collider
    private Shape shape;

    private float width, height;

    private float radius;

    // Offset from entity's Transform position
    // Allows hitbox not centered on entity
    private float offsetX, offsetY;

    // Collision layer (used by collision matrix)
    private int layer;

    // If true = collision event only (no physical resolution)
    // If false = physical collision (can resolve later)
    private boolean trigger;

  
    public static Collider rect(float w, float h,
                                float offsetX, float offsetY,
                                int layer, boolean trigger) {

        Collider c = new Collider();
        c.shape = Shape.RECT;
        c.width = w;
        c.height = h;
        c.offsetX = offsetX;
        c.offsetY = offsetY;
        c.layer = layer;
        c.trigger = trigger;
        return c;
    }



    // Getters only
    // No setters as collider configuration should be stable once created

    public Shape getShape() { return shape; }

    public float getWidth() { return width; }

    public float getHeight() { return height; }

    public float getRadius() { return radius; }

    public float getOffsetX() { return offsetX; }

    public float getOffsetY() { return offsetY; }

    public int getLayer() { return layer; }

    public boolean isTrigger() { return trigger; }
}
