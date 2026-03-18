package io.github.some_example_name.factory;

import java.util.Random;

import io.github.some_example_name.healthyeating.FoodAssets;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Concrete factory for Stage 2 (Teen).
 * 5 healthy, 5 unhealthy, speed 80, 2.0s respawn.
 */
public class TeenStageFactory implements StageContentFactory {

    private static final StageProfile PROFILE = new StageProfile(
            Stage.TEEN, "Stage 2 - Teen", 20,
            5, 5, 0, 0, 0,
            35f, 2.0f, 80f, 60f,
            0.95f, 0.9f, 0.8f, 1f);

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
