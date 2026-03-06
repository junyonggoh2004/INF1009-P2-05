package io.github.some_example_name.healthyeating;

import io.github.some_example_name.entity.Component;

/**
 * Component that tracks the player's score and level-up threshold.
 * When score reaches the threshold, player advances to next stage.
 */
public class ScoreTracker implements Component {

    private int score;
    private int threshold;

    public ScoreTracker(int threshold) {
        this.score = 0;
        this.threshold = threshold;
    }

    public int getScore()        { return score; }
    public int getThreshold()    { return threshold; }

    public void addScore(int points) {
        score += points;
        if (score < 0) score = 0;  // Don't go negative
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean hasReachedThreshold() {
        return score >= threshold;
    }

    /** Returns progress as 0.0 to 1.0 for the progress bar */
    public float getProgress() {
        if (threshold <= 0) return 1f;
        return Math.min(1f, (float) score / threshold);
    }

    public void reset() {
        score = 0;
    }
}