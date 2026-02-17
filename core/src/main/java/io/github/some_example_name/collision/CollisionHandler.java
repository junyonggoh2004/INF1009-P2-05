package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Entity;

public interface CollisionHandler   {
 
    /** Fired once when self and other start colliding */
    void onEnter(Entity a, Entity b);

    /** Fired every frame while self and other remain colliding */
    void onStay(Entity a, Entity b);

    /** Fired once when self and other stop colliding */
    void onExit(Entity a, Entity b);
}

