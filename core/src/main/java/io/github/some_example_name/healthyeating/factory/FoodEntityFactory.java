package io.github.some_example_name.healthyeating.factory;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.healthyeating.FoodTag;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;

/**
 * Factory Pattern — creates food entities from a FoodSpawnSpec.
 * Single place for all food entity creation logic.
 */
public class FoodEntityFactory {

    /** Creates a food entity with Sprite, FoodTag, Collider, and registers its Transform/Motion. */
    public Entity createFood(FoodSpawnSpec spec, EntityManager em, MovementManager mm) {
        Entity food = em.createEntity();

        Sprite sprite = new Sprite(spec.getTexturePath(), spec.getSize(), spec.getSize());
        food.add(sprite);
        food.add(new FoodTag(spec.getType(), spec.getRespawnDelay()));
        food.add(new Collider(spec.getSize(), spec.getSize(), 0, 0, 0, false));

        mm.register(food.getId(),
                new Transform(spec.getX(), spec.getY()),
                new Motion(spec.getVx(), spec.getVy()));

        return food;
    }
}
