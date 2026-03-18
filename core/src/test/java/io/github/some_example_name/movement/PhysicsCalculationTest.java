package io.github.some_example_name.movement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhysicsCalculationTest {

    private final PhysicsCalculation calc = new PhysicsCalculation();

    @Test
    void applyAccelerationUpdatesVelocity() {
        Motion m = new Motion(0f, 0f, 10f, 20f, 0f, false);
        calc.applyAcceleration(m, 1.0f);
        assertEquals(10f, m.getVx(), 0.01f);
        assertEquals(20f, m.getVy(), 0.01f);
    }

    @Test
    void applyAccelerationScalesWithDeltaTime() {
        Motion m = new Motion(0f, 0f, 100f, 0f, 0f, false);
        calc.applyAcceleration(m, 0.5f);
        assertEquals(50f, m.getVx(), 0.01f);
    }

    @Test
    void applyGravityWhenEnabled() {
        Motion m = new Motion(0f, 0f, 0f, 0f, 0f, true);
        calc.applyGravity(m, 1.0f);
        // Gravity is -980, so vy should decrease
        assertTrue(m.getVy() < 0, "Gravity should pull downward (negative vy)");
        assertEquals(-980f, m.getVy(), 0.01f);
    }

    @Test
    void applyGravityDoesNothingWhenDisabled() {
        Motion m = new Motion(0f, 100f, 0f, 0f, 0f, false);
        calc.applyGravity(m, 1.0f);
        assertEquals(100f, m.getVy(), 0.01f);
    }

    @Test
    void clampSpeedReducesExcessiveVelocity() {
        Motion m = new Motion(300f, 400f, 0f, 0f, 100f, false);
        // Speed = 500, maxSpeed = 100
        calc.clampSpeed(m);
        float speed = (float) Math.sqrt(m.getVx() * m.getVx() + m.getVy() * m.getVy());
        assertEquals(100f, speed, 0.1f);
    }

    @Test
    void clampSpeedDoesNothingUnderMax() {
        Motion m = new Motion(30f, 40f, 0f, 0f, 100f, false);
        calc.clampSpeed(m);
        assertEquals(30f, m.getVx(), 0.01f);
        assertEquals(40f, m.getVy(), 0.01f);
    }

    @Test
    void clampSpeedIgnoredWhenMaxSpeedIsZero() {
        Motion m = new Motion(1000f, 1000f, 0f, 0f, 0f, false);
        calc.clampSpeed(m);
        assertEquals(1000f, m.getVx(), 0.01f);
    }

    @Test
    void updatePositionMovesTransform() {
        Transform t = new Transform(10f, 20f);
        Motion m = new Motion(100f, -50f);
        calc.updatePosition(t, m, 0.5f);
        assertEquals(60f, t.getX(), 0.01f);  // 10 + 100*0.5
        assertEquals(-5f, t.getY(), 0.01f);  // 20 + (-50)*0.5
    }

    @Test
    void calculateRunsAllSteps() {
        Transform t = new Transform(0f, 0f);
        Motion m = new Motion(0f, 0f, 100f, 0f, 0f, false);
        calc.calculate(t, m, 1.0f);
        // After acceleration: vx=100, after gravity: vy unchanged (disabled)
        // After position update: x = 0 + 100*1 = 100
        assertEquals(100f, t.getX(), 0.01f);
    }
}
