package io.github.some_example_name.factory;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Registry mapping PlayerTag.Stage to StageContentFactory instances.
 * Centralizes stage lookup so adding a new stage is a one-line registration.
 */
public class StageFactoryRegistry {

    private final Map<Stage, StageContentFactory> factories;

    public StageFactoryRegistry() {
        factories = new EnumMap<>(Stage.class);
        factories.put(Stage.TODDLER, new ToddlerStageFactory());
        factories.put(Stage.TEEN, new TeenStageFactory());
        factories.put(Stage.ADULT, new AdultStageFactory());
    }

    /** Returns the factory for the given stage. */
    public StageContentFactory get(Stage stage) {
        return factories.get(stage);
    }

    /** Returns all registered factories. */
    public Collection<StageContentFactory> all() {
        return factories.values();
    }
}
