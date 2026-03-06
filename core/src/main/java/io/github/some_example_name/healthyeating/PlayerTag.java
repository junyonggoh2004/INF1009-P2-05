package io.github.some_example_name.healthyeating;

import io.github.some_example_name.entity.Component;

/**
 * Component that marks an entity as the player and tracks life stage.
 */
public class PlayerTag implements Component {

    public enum Stage {
        TODDLER,  // Stage 1
        TEEN,     // Stage 2
        ADULT     // Stage 3
    }

    private Stage currentStage;

    public PlayerTag() {
        this.currentStage = Stage.TODDLER;
    }

    public Stage getCurrentStage()         { return currentStage; }
    public void setCurrentStage(Stage s)   { currentStage = s; }
}