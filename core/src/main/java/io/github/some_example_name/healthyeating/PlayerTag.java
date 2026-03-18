package io.github.some_example_name.healthyeating;

import io.github.some_example_name.entity.Component;

/**
 * Component that marks an entity as the player and tracks life stage and expression.
 */
public class PlayerTag implements Component {

    public enum Stage {
        TODDLER,  // Stage 1
        TEEN,     // Stage 2
        ADULT     // Stage 3
    }

    public enum Expression {
        DEFAULT,
        EATING,
        SAD
    }

    private static final float EXPRESSION_DURATION = 0.5f;

    private Stage currentStage;
    private int playerNumber;
    private Expression expression = Expression.DEFAULT;
    private float expressionTimer = 0f;

    public PlayerTag() {
        this.currentStage = Stage.TODDLER;
        this.playerNumber = 1;
    }

    public PlayerTag(int playerNumber) {
        this.currentStage = Stage.TODDLER;
        this.playerNumber = playerNumber;
    }

    public Stage getCurrentStage()         { return currentStage; }
    public void setCurrentStage(Stage s)   { currentStage = s; }
    public int getPlayerNumber()           { return playerNumber; }

    public Expression getExpression()      { return expression; }

    public void setExpression(Expression expr) {
        this.expression = expr;
        this.expressionTimer = EXPRESSION_DURATION;
    }

    /** Call each frame to auto-reset expression back to DEFAULT. */
    public void updateExpression(float dt) {
        if (expression != Expression.DEFAULT) {
            expressionTimer -= dt;
            if (expressionTimer <= 0) {
                expression = Expression.DEFAULT;
            }
        }
    }
}
