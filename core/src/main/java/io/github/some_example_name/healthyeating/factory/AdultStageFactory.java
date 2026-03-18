package io.github.some_example_name.healthyeating.factory;

import io.github.some_example_name.healthyeating.FoodAssets;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Concrete factory for Stage 3 (Adult).
 * 4 healthy, 4 unhealthy, 3 older, 2 cigarette, 2 medicine.
 * Speed 150, 1.5s respawn.
 */
public class AdultStageFactory implements StageContentFactory {

    private static final StageProfile PROFILE = new StageProfile(
            Stage.ADULT, "Stage 3 - Adult", 30,
            4, 4, 3, 2, 2,
            35f, 1.5f, 150f, 60f,
            0.3f, 0.15f, 0.15f, 1f,
            null, null);

    @Override
    public StageProfile getProfile() {
        return PROFILE;
    }

    @Override
    public String resolveTexturePath(FoodType type) {
        switch (type) {
            case HEALTHY:   return FoodAssets.randomHealthy();
            case UNHEALTHY: return FoodAssets.randomUnhealthy();
            case OLDER:     return FoodAssets.randomOlderUnhealthy();
            case CIGARETTE: return FoodAssets.CIGARETTE;
            case MEDICINE:  return FoodAssets.randomMedicine();
            default:        return FoodAssets.randomUnhealthy();
        }
    }
}
