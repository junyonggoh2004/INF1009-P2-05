package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Component;

/**
 * Collider component.
 * 
 */public class Collider implements Component {
	    private float width, height;
	    private float offsetX, offsetY;
	    private int layer;
	    private boolean trigger;

	    //shape for Collision
	    public static Collider rect(float w, float h, float offsetX, float offsetY, int layer, boolean trigger) {
	        Collider c = new Collider();
	        c.width = w; c.height = h;
	        c.offsetX = offsetX; c.offsetY = offsetY;
	        c.layer = layer; c.trigger = trigger;
	        return c;
	    }

	    public float getWidth() { return width; }
	    public float getHeight() { return height; }
	    public float getOffsetX() { return offsetX; }
	    public float getOffsetY() { return offsetY; }
	    public int getLayer() { return layer; }
	    public boolean isTrigger() { return trigger; }
	}
