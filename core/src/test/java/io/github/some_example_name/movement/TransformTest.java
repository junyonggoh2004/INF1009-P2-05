package io.github.some_example_name.movement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransformTest {

    @Test
    void defaultConstructorSetsZero() {
        Transform t = new Transform();
        assertEquals(0f, t.getX());
        assertEquals(0f, t.getY());
        assertEquals(0f, t.getRotation());
    }

    @Test
    void positionConstructor() {
        Transform t = new Transform(10f, 20f);
        assertEquals(10f, t.getX());
        assertEquals(20f, t.getY());
    }

    @Test
    void setPositionUpdatesBoth() {
        Transform t = new Transform();
        t.setPosition(5f, 15f);
        assertEquals(5f, t.getX());
        assertEquals(15f, t.getY());
    }

    @Test
    void setIndividualAxes() {
        Transform t = new Transform();
        t.setX(3f);
        t.setY(7f);
        assertEquals(3f, t.getX());
        assertEquals(7f, t.getY());
    }

    @Test
    void rotationConstructor() {
        Transform t = new Transform(0f, 0f, 45f);
        assertEquals(45f, t.getRotation());
    }
}
