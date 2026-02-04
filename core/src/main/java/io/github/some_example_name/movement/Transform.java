package io.github.some_example_name.movement;

/**
 * Stores spatial data for an entity.
 * Position and rotation are updated by systems such as MovementManager.
 */

public class Transform {

    // World position (in units, e.g. pixels or meters)
    private float x;
    private float y;

    // Orientation of the entity
    private float rotation;
    
    // Constructor with default rotation --> Common case: just position
    public Transform(float x, float y) {
        this.x = x;
        this.y = y;
        this.rotation = 0f;
    }
    
    // Constructor with explicit rotation --> Advanced case: full control
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
}

