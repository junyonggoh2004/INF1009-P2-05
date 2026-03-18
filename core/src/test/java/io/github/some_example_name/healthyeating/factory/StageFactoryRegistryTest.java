package io.github.some_example_name.healthyeating.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.github.some_example_name.healthyeating.PlayerTag.Stage;

class StageFactoryRegistryTest {

    @Test
    void allStagesAreRegistered() {
        StageFactoryRegistry registry = new StageFactoryRegistry();
        assertNotNull(registry.get(Stage.TODDLER));
        assertNotNull(registry.get(Stage.TEEN));
        assertNotNull(registry.get(Stage.ADULT));
    }

    @Test
    void toddlerReturnsCorrectFactory() {
        StageFactoryRegistry registry = new StageFactoryRegistry();
        StageContentFactory factory = registry.get(Stage.TODDLER);
        assertTrue(factory instanceof ToddlerStageFactory);
        assertEquals(Stage.TODDLER, factory.getProfile().getStage());
    }

    @Test
    void teenReturnsCorrectFactory() {
        StageFactoryRegistry registry = new StageFactoryRegistry();
        StageContentFactory factory = registry.get(Stage.TEEN);
        assertTrue(factory instanceof TeenStageFactory);
        assertEquals(Stage.TEEN, factory.getProfile().getStage());
    }

    @Test
    void adultReturnsCorrectFactory() {
        StageFactoryRegistry registry = new StageFactoryRegistry();
        StageContentFactory factory = registry.get(Stage.ADULT);
        assertTrue(factory instanceof AdultStageFactory);
        assertEquals(Stage.ADULT, factory.getProfile().getStage());
    }

    @Test
    void allReturnsThreeFactories() {
        StageFactoryRegistry registry = new StageFactoryRegistry();
        assertEquals(3, registry.all().size());
    }

    @Test
    void eachFactoryResolvesTexturesWithoutError() {
        StageFactoryRegistry registry = new StageFactoryRegistry();

        for (StageContentFactory factory : registry.all()) {
            assertNotNull(factory.resolveTexturePath(
                    io.github.some_example_name.healthyeating.FoodTag.FoodType.HEALTHY));
            assertNotNull(factory.resolveTexturePath(
                    io.github.some_example_name.healthyeating.FoodTag.FoodType.UNHEALTHY));
        }
    }
}
