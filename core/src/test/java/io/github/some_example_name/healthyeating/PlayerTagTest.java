package io.github.some_example_name.healthyeating;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.github.some_example_name.healthyeating.PlayerTag.Expression;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

class PlayerTagTest {

    @Test
    void defaultConstructorSetsToddlerAndPlayer1() {
        PlayerTag tag = new PlayerTag();
        assertEquals(Stage.TODDLER, tag.getCurrentStage());
        assertEquals(1, tag.getPlayerNumber());
    }

    @Test
    void parameterizedConstructorSetsPlayerNumber() {
        PlayerTag tag = new PlayerTag(2);
        assertEquals(2, tag.getPlayerNumber());
        assertEquals(Stage.TODDLER, tag.getCurrentStage());
    }

    @Test
    void setCurrentStageUpdatesStage() {
        PlayerTag tag = new PlayerTag();
        tag.setCurrentStage(Stage.TEEN);
        assertEquals(Stage.TEEN, tag.getCurrentStage());
        tag.setCurrentStage(Stage.ADULT);
        assertEquals(Stage.ADULT, tag.getCurrentStage());
    }

    @Test
    void expressionDefaultsToDefault() {
        PlayerTag tag = new PlayerTag();
        assertEquals(Expression.DEFAULT, tag.getExpression());
    }

    @Test
    void setExpressionChangesState() {
        PlayerTag tag = new PlayerTag();
        tag.setExpression(Expression.EATING);
        assertEquals(Expression.EATING, tag.getExpression());
        tag.setExpression(Expression.SAD);
        assertEquals(Expression.SAD, tag.getExpression());
    }

    @Test
    void expressionAutoResetsAfterDuration() {
        PlayerTag tag = new PlayerTag();
        tag.setExpression(Expression.EATING);

        // Tick partially — should still be EATING
        tag.updateExpression(0.3f);
        assertEquals(Expression.EATING, tag.getExpression());

        // Tick past duration (0.5s total) — should reset to DEFAULT
        tag.updateExpression(0.3f);
        assertEquals(Expression.DEFAULT, tag.getExpression());
    }

    @Test
    void updateExpressionDoesNothingWhenDefault() {
        PlayerTag tag = new PlayerTag();
        tag.updateExpression(10f);
        assertEquals(Expression.DEFAULT, tag.getExpression());
    }
}
