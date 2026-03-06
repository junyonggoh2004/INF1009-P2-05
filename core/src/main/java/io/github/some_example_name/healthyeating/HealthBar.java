package io.github.some_example_name.healthyeating;

import io.github.some_example_name.entity.Component;

/**
 * Component that tracks player health as hearts.
 * When hearts reach 0, game over.
 */
public class HealthBar implements Component {

    private int hearts;
    private final int maxHearts;

    public HealthBar(int maxHearts) {
        this.maxHearts = maxHearts;
        this.hearts = maxHearts;
    }

    public int getHearts()    { return hearts; }
    public int getMaxHearts() { return maxHearts; }

    public void loseHearts(int amount) {
        hearts = Math.max(0, hearts - amount);
    }

    public void gainHearts(int amount) {
        hearts = Math.min(maxHearts, hearts + amount);
    }

    public boolean isDead() {
        return hearts <= 0;
    }

    public void reset() {
        hearts = maxHearts;
    }
}