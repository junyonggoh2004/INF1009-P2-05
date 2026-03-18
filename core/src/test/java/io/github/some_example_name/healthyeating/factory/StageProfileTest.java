package io.github.some_example_name.healthyeating.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.github.some_example_name.healthyeating.PlayerTag.Stage;

class StageProfileTest {

    @Test
    void toddlerFactoryHasCorrectProfile() {
        StageProfile p = new ToddlerStageFactory().getProfile();
        assertEquals(Stage.TODDLER, p.getStage());
        assertEquals(10, p.getScoreThreshold());
        assertEquals(6, p.getHealthyCount());
        assertEquals(3, p.getUnhealthyCount());
        assertEquals(0, p.getOlderCount());
        assertEquals(0, p.getCigaretteCount());
        assertEquals(0, p.getMedicineCount());
        assertEquals(0f, p.getMaxFoodSpeed());
        assertEquals(2.5f, p.getRespawnDelay());
    }

    @Test
    void teenFactoryHasCorrectProfile() {
        StageProfile p = new TeenStageFactory().getProfile();
        assertEquals(Stage.TEEN, p.getStage());
        assertEquals(20, p.getScoreThreshold());
        assertEquals(5, p.getHealthyCount());
        assertEquals(5, p.getUnhealthyCount());
        assertEquals(0, p.getOlderCount());
        assertEquals(0, p.getCigaretteCount());
        assertEquals(0, p.getMedicineCount());
        assertEquals(80f, p.getMaxFoodSpeed());
        assertEquals(2.0f, p.getRespawnDelay());
    }

    @Test
    void adultFactoryHasCorrectProfile() {
        StageProfile p = new AdultStageFactory().getProfile();
        assertEquals(Stage.ADULT, p.getStage());
        assertEquals(30, p.getScoreThreshold());
        assertEquals(4, p.getHealthyCount());
        assertEquals(4, p.getUnhealthyCount());
        assertEquals(3, p.getOlderCount());
        assertEquals(2, p.getCigaretteCount());
        assertEquals(2, p.getMedicineCount());
        assertEquals(150f, p.getMaxFoodSpeed());
        assertEquals(1.5f, p.getRespawnDelay());
    }

    @Test
    void profileFoodSizeAndMarginAreConsistent() {
        for (StageContentFactory factory : new StageFactoryRegistry().all()) {
            StageProfile p = factory.getProfile();
            assertEquals(35f, p.getFoodSize());
            assertEquals(60f, p.getSpawnMargin());
        }
    }

    @Test
    void stageProgressionChainIsCorrect() {
        StageProfile toddler = new ToddlerStageFactory().getProfile();
        StageProfile teen = new TeenStageFactory().getProfile();
        StageProfile adult = new AdultStageFactory().getProfile();

        assertEquals(Stage.TEEN, toddler.getNextStage());
        assertNotNull(toddler.getTransitionMessage());
        assertFalse(toddler.isFinalStage());

        assertEquals(Stage.ADULT, teen.getNextStage());
        assertNotNull(teen.getTransitionMessage());
        assertFalse(teen.isFinalStage());

        assertNull(adult.getNextStage());
        assertTrue(adult.isFinalStage());
    }
}
