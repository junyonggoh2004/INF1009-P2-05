package io.github.some_example_name.healthyeating;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HealthBarTest {

    @Test
    void startsAtMaxHearts() {
        HealthBar bar = new HealthBar(5);
        assertEquals(5, bar.getHearts());
        assertEquals(5, bar.getMaxHearts());
    }

    @Test
    void loseHeartsReducesHealth() {
        HealthBar bar = new HealthBar(5);
        bar.loseHearts(2);
        assertEquals(3, bar.getHearts());
    }

    @Test
    void loseHeartsDoesNotGoBelowZero() {
        HealthBar bar = new HealthBar(3);
        bar.loseHearts(10);
        assertEquals(0, bar.getHearts());
    }

    @Test
    void gainHeartsRestoresHealth() {
        HealthBar bar = new HealthBar(5);
        bar.loseHearts(3);
        bar.gainHearts(2);
        assertEquals(4, bar.getHearts());
    }

    @Test
    void gainHeartsDoesNotExceedMax() {
        HealthBar bar = new HealthBar(5);
        bar.gainHearts(10);
        assertEquals(5, bar.getHearts());
    }

    @Test
    void isDeadWhenHeartsReachZero() {
        HealthBar bar = new HealthBar(2);
        assertFalse(bar.isDead());
        bar.loseHearts(2);
        assertTrue(bar.isDead());
    }

    @Test
    void resetRestoresToMax() {
        HealthBar bar = new HealthBar(5);
        bar.loseHearts(4);
        bar.reset();
        assertEquals(5, bar.getHearts());
    }
}
