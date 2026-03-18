package io.github.some_example_name.movement;

/**
 * PhysicsCalculation: Performs all physics calculations for movement.
 * Handles acceleration, gravity, speed clamping, and position updates.
 */
public class PhysicsCalculation {

	// gravity acceleration (pixels/sec^2). Negative because libGDX Y-axis points up.
    private static final float GRAVITY = -980f;

    // Physics Calculation 1: Acceleration → Velocity
    // Using: v_new = v_old + a * dt
    public void applyAcceleration(Motion m, float dt) {
        // vx = horizontal velocity, getAx() = horizontal acceleration
        float vx = m.getVx() + m.getAx() * dt;

        // vy = vertical velocity, getAy() = vertical acceleration
        float vy = m.getVy() + m.getAy() * dt;

        // Update velocity in Motion component
        m.setVx(vx);
        m.setVy(vy);
    }

    // Physics Calculation 2: Gravity
    // Only affects vertical velocity (vy) if enabled
    public void applyGravity(Motion m, float dt) {
        if (m.isGravityEnabled()) {
            // GRAVITY is constant (980 pixels/sec²)
            float vy = m.getVy() + GRAVITY * dt;

            // Update vertical velocity
            m.setVy(vy);
        }
    }

    // Physics Calculation 3: Clamp to Max Speed
    // Scales velocity when √(vx² + vy²) > maxSpeed
    public void clampSpeed(Motion m) {
        // Get the maximum allowed speed for this entity
        float max = m.getMaxSpeed();

        // Only clamp if maxSpeed is set (> 0)
        if (max > 0f) {
            // Get current velocities
            float vx = m.getVx();
            float vy = m.getVy();

            // Calculate speed squared (faster than sqrt)
            // speed² = vx² + vy² (Pythagorean theorem)
            float speedSq = vx * vx + vy * vy;
            
            // Calculate maxSpeed squared
            float maxSq = max * max;

            // Check if current speed exceeds maximum speed
            if (speedSq > maxSq) {
                // Calculate actual speed using square root
                // speed = √(vx² + vy²)
                float speed = (float) Math.sqrt(speedSq);

                // Avoid division by zero
                if (speed > 0f) {
                    // Calculate scaling factor to reduce speed to max
                    // scale = maxSpeed / currentSpeed
                    float scale = max / speed;

                    // Update velocities with scaled values
                    m.setVx(vx * scale);
                    m.setVy(vy * scale);
                }
            }
        }
    }

    // Physics Calculation 4: Velocity → Position
    // Using: p_new = p_old + v * dt
    public void updatePosition(Transform t, Motion m, float dt) {
        // Update x position using: x_new = x_old + vx * dt
        t.setX(t.getX() + m.getVx() * dt);

        // Update y position using: y_new = y_old + vy * dt
        t.setY(t.getY() + m.getVy() * dt);
    }

    // All-in-one method - executes all 4 physics calculations in order
    public void calculate(Transform t, Motion m, float dt) {
        applyAcceleration(m, dt);
        applyGravity(m, dt);
        clampSpeed(m);
        updatePosition(t, m, dt);
    }
}
