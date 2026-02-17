package io.github.some_example_name.collision;

import io.github.some_example_name.entity.Component;
import io.github.some_example_name.entity.Entity;

public interface CollisionHandler extends Component {
    void onCollision(Entity self, Entity other);
}