package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Component;
import io.github.some_example_name.entity.Entity;

/**
 * Callback interface for collision events.
 * Implementors only need to override the events they care about.
 */
public interface CollisionHandler extends Component {

    /** Fired once when two entities first start colliding. */
    void onEnter(Entity self, Entity other);

    /** Fired every frame while two entities remain colliding. */
    default void onStay(Entity self, Entity other) { }

    /** Fired once when two entities stop colliding. */
    default void onExit(Entity self, Entity other) { }
}
