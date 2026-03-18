package io.github.some_example_name.factory;

import java.util.Random;

import io.github.some_example_name.healthyeating.FoodAssets;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Concrete factory for Stage 1 (Toddler).
 * 6 healthy, 3 unhealthy, no movement, 2.5s respawn.
 */
public class ToddlerStageFactory implements StageContentFactory {

    private static final StageProfile PROFILE = new StageProfile(
            Stage.TODDLER, "Stage 1 - Toddler", 10,
            6, 3, 0, 0, 0,
            35f, 2.5f, 0f, 60f,
            0.85f, 0.95f, 0.85f, 1f);

    @Override
    public StageProfile getProfile() {
        return PROFILE;
    }

    @Override
    public String resolveTexturePath(FoodType type, Random rng) {
        switch (type) {
            case HEALTHY:   return FoodAssets.randomHealthy();
            case UNHEALTHY: return FoodAssets.randomUnhealthy();
            default:        return FoodAssets.randomUnhealthy();
        }
    }
}
