package io.github.some_example_name.movement;

/**
 * Represents movement-related properties of an entity.
 * Used by MovementManager to update Transform.
 */

public class Motion {

    // Velocity components
    private float vx;
    private float vy;

    // Acceleration components
    private float ax;
    private float ay;

    // Maximum allowed speed
    private float maxSpeed;

    // Enables or disables gravity effects
    private boolean gravityEnabled;
    
   // Default constructor - stationary entity
    public Motion() {
        this.vx = 0f;
        this.vy = 0f;
        this.ax = 0f;
        this.ay = 0f;
        this.maxSpeed = Float.MAX_VALUE;
        this.gravityEnabled = false;
    }
    
    // Constructor with velocity only - common case
    public Motion(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
        this.ax = 0f;
        this.ay = 0f;
        this.maxSpeed = Float.MAX_VALUE;
        this.gravityEnabled = false;
    }
    
    // Constructor with full control - advanced case
    public Motion(float vx, float vy, float ax, float ay, float maxSpeed, boolean gravityEnabled) {
        this.vx = vx;
        this.vy = vy;
        this.ax = ax;
        this.ay = ay;
        this.maxSpeed = maxSpeed;
        this.gravityEnabled = gravityEnabled;
    }

    // getters
    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public float getAx() { return ax; }
    public float getAy() { return ay; }
    public float getMaxSpeed() { return maxSpeed; }
    public boolean isGravityEnabled() { return gravityEnabled; }

    // setters
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void setAx(float ax) { this.ax = ax; }
    public void setAy(float ay) { this.ay = ay; }
    public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }
    public void setGravityEnabled(boolean gravityEnabled) { this.gravityEnabled = gravityEnabled; }
}
