
package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Component;
import io.github.some_example_name.entity.Entity;


/**
 * CollisionHandler = contract for any component
 * that wants to react when a collision happens.
 *
 * CollisionManager detects the collision.
 * This handler defines what to do after detection.
 */
public interface CollisionHandler extends Component {
    void onCollision(Entity self, Entity other);
}


