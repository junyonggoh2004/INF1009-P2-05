package io.github.some_example_name.movement;

import io.github.some_example_name.entity.Component;

/**
 * Stores spatial data for an entity.
 * Position and rotation are updated by systems such as MovementManager.
 */
public class Transform implements Component {

    // World position (in units, e.g. pixels or meters)
    private float x;
    private float y;

    // Orientation of the entity
    private float rotation;
    
    // Default constructor - everything at origin
    public Transform() {
        this.x = 0f;
        this.y = 0f;
        this.rotation = 0f;
    }
    
    // Constructor with position only - rotation defaults to 0
    public Transform(float x, float y) {
        this.x = x;
        this.y = y;
        this.rotation = 0f;
    }
    
    // Constructor with full control
    public Transform(float x, float y, float rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
    
    // getters 
    public float getX() { return x; }
    public float getY() { return y; }
    public float getRotation() { return rotation; }
    
    // setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setRotation(float rotation) { this.rotation = rotation; }
    
    // Convenience method to set both x and y at once
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}

