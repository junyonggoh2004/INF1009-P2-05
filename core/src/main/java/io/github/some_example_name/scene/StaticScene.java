package io.github.some_example_name.prototype;

import io.github.some_example_name.scene.Scene;
import io.github.some_example_name.scene.TransitionData;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.collision.Collider;

import java.util.Random;

/**
 * Scene 1: Static circles scattered randomly.
 * Demonstrates basic entity/component setup with no movement.
 */
public class StaticScene extends Scene {

    private final EntityManager em;
    private final MovementManager mm;
    private final Random rng = new Random();
    private final float worldW, worldH;

    private static final int CIRCLE_COUNT = 8;
    private static final float CIRCLE_DIAMETER = 40f;
    private static final float RESPAWN_DELAY = 3.0f;

    public StaticScene(EntityManager em, MovementManager mm, float worldW, float worldH) {
        super("Static Scene");
        this.em = em;
        this.mm = mm;
        this.worldW = worldW;
        this.worldH = worldH;
    }

    @Override
    public void load() {
        float margin = 50f;

        for (int i = 0; i < CIRCLE_COUNT; i++) {
            Entity circle = em.createEntity();

            Sprite sprite = new Sprite("circle", CIRCLE_DIAMETER, CIRCLE_DIAMETER);
            sprite.setColor(
                    0.4f + rng.nextFloat() * 0.6f,
                    0.4f + rng.nextFloat() * 0.6f,
                    0.4f + rng.nextFloat() * 0.6f, 1f);
            circle.add(sprite);
            circle.add(new Collectible(RESPAWN_DELAY));
            circle.add(new Collider(CIRCLE_DIAMETER, CIRCLE_DIAMETER, 0, 0, 0, false));

            float x = margin + rng.nextFloat() * (worldW - CIRCLE_DIAMETER - margin * 2);
            float y = margin + rng.nextFloat() * (worldH - CIRCLE_DIAMETER - margin * 2);
            mm.register(circle.getId(), new Transform(x, y), new Motion());
        }
    }

    @Override
    public void update(float dt) {
        // Static circles don't move
    }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0.08f, 0.08f, 0.15f, 1f);
    }
}