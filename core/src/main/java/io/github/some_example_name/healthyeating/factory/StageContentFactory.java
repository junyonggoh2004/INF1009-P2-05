package io.github.some_example_name.healthyeating.factory;

import io.github.some_example_name.healthyeating.FoodTag.FoodType;

/**
 * Abstract Factory interface for stage content families.
 * Each concrete implementation defines a stage's complete configuration:
 * profile data, and texture selection policy for each food type.
 */
public interface StageContentFactory {

    /** Returns the immutable profile for this stage. */
    StageProfile getProfile();

    /** Resolves the texture path for a given food type. */
    String resolveTexturePath(FoodType type);
}
