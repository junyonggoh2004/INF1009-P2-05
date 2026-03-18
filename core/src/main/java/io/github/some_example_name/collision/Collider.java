package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Component;

/**
 * Collider component.
 * Stores collision data only.
 */
public class Collider implements Component {

    private float width, height;
    private float offsetX, offsetY;
    private int layer;
    private boolean trigger;

    public Collider(float w, float h,
                    float offsetX, float offsetY,
                    int layer, boolean trigger) {
        this.width = w;
        this.height = h;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.layer = layer;
        this.trigger = trigger;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getOffsetX() { return offsetX; }
    public float getOffsetY() { return offsetY; }
    public int getLayer() { return layer; }
    public boolean isTrigger() { return trigger; }
}
