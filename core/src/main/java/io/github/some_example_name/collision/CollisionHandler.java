package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Component;
import io.github.some_example_name.entity.Entity;

public interface CollisionHandler extends Component {
 
    /** Fired once when self and other start colliding */
    void onEnter(Entity self, Entity other);

    /** Fired every frame while self and other remain colliding */
    void onStay(Entity self, Entity other);

    /** Fired once when self and other stop colliding */
    void onExit(Entity self, Entity other);
}

