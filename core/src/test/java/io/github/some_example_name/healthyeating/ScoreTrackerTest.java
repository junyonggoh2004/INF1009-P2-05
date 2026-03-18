package io.github.some_example_name.healthyeating;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTrackerTest {

    @Test
    void startsAtZero() {
        ScoreTracker tracker = new ScoreTracker(10);
        assertEquals(0, tracker.getScore());
        assertEquals(10, tracker.getThreshold());
    }

    @Test
    void addScoreIncrementsCorrectly() {
        ScoreTracker tracker = new ScoreTracker(10);
        tracker.addScore(3);
        assertEquals(3, tracker.getScore());
        tracker.addScore(2);
        assertEquals(5, tracker.getScore());
    }

    @Test
    void scoreDoesNotGoNegative() {
        ScoreTracker tracker = new ScoreTracker(10);
        tracker.addScore(-5);
        assertEquals(0, tracker.getScore());
    }

    @Test
    void thresholdReachedTriggersLevelUp() {
        ScoreTracker tracker = new ScoreTracker(5);
        tracker.addScore(4);
        assertFalse(tracker.hasReachedThreshold());
        tracker.addScore(1);
        assertTrue(tracker.hasReachedThreshold());
    }

    @Test
    void progressReturnsCorrectRatio() {
        ScoreTracker tracker = new ScoreTracker(10);
        assertEquals(0f, tracker.getProgress(), 0.001f);
        tracker.addScore(5);
        assertEquals(0.5f, tracker.getProgress(), 0.001f);
        tracker.addScore(10);
        assertEquals(1.0f, tracker.getProgress(), 0.001f);
    }

    @Test
    void resetClearsScore() {
        ScoreTracker tracker = new ScoreTracker(10);
        tracker.addScore(7);
        tracker.reset();
        assertEquals(0, tracker.getScore());
    }

    @Test
    void setThresholdUpdatesThreshold() {
        ScoreTracker tracker = new ScoreTracker(10);
        tracker.setThreshold(20);
        assertEquals(20, tracker.getThreshold());
    }
}
