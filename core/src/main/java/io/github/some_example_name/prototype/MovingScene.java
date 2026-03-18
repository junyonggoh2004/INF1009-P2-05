package io.github.some_example_name.prototype;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scene 2: Moving circles that drift around the screen.
 * Demonstrates entities with velocity using MovementManager.
 */
public class MovingScene extends Scene {

    private final EntityManager em;
    private final MovementManager mm;
    private final Random rng = new Random();
    private final float worldW, worldH;
    private final List<Integer> sceneEntityIds = new ArrayList<>();

    private static final int CIRCLE_COUNT = 6;
    private static final float CIRCLE_DIAMETER = 40f;
    private static final float RESPAWN_DELAY = 3.0f;

    public MovingScene(EntityManager em, MovementManager mm, float worldW, float worldH) {
        super("Moving Scene");
        this.em = em;
        this.mm = mm;
        this.worldW = worldW;
        this.worldH = worldH;
    }

    @Override
    public void load() {
        sceneEntityIds.clear();
        float margin = 50f;

        for (int i = 0; i < CIRCLE_COUNT; i++) {
            Entity circle = em.createEntity();
            sceneEntityIds.add(circle.getId());

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
            float vx = -80f + rng.nextFloat() * 160f;
            float vy = -80f + rng.nextFloat() * 160f;
            mm.register(circle.getId(), new Transform(x, y), new Motion(vx, vy));
        }
    }

    public void spawnCircle() {
        float margin = 50f;
        Entity circle = em.createEntity();
        sceneEntityIds.add(circle.getId());

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
        float vx = -80f + rng.nextFloat() * 160f;
        float vy = -80f + rng.nextFloat() * 160f;
        mm.register(circle.getId(), new Transform(x, y), new Motion(vx, vy));
    }

    @Override
    public void update(float dt) {
        float diameter = CIRCLE_DIAMETER;
        for (int entityId : sceneEntityIds) {
            Transform t = mm.getTransform(entityId);
            if (t == null) {
                continue;
            }

            if (t.getX() < -diameter) t.setX(worldW);
            if (t.getX() > worldW) t.setX(-diameter);
            if (t.getY() < -diameter) t.setY(worldH);
            if (t.getY() > worldH) t.setY(-diameter);
        }
    }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0.12f, 0.06f, 0.12f, 1f);
    }

    @Override
    public void unload() {
        for (int entityId : sceneEntityIds) {
            mm.unregister(entityId);
            if (em.exists(entityId)) {
                em.destroyEntity(entityId);
            }
        }
        sceneEntityIds.clear();
    }
}
