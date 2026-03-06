package io.github.some_example_name.scene;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.healthyeating.FoodAssets;
import io.github.some_example_name.healthyeating.FoodTag;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;

import java.util.Random;

public class Stage3Scene extends Scene {

    private final EntityManager em;
    private final MovementManager mm;
    private final Random rng = new Random();
    private final float worldW, worldH;

    private static final int HEALTHY_COUNT = 4;
    private static final int UNHEALTHY_COUNT = 5;
    private static final int CIGARETTE_COUNT = 3;
    private static final float FOOD_SIZE = 35f;
    private static final float RESPAWN_DELAY = 1.5f;
    private static final float SPEED = 150f;

    public Stage3Scene(EntityManager em, MovementManager mm, float worldW, float worldH) {
        super("Stage 3 - Adult");
        this.em = em;
        this.mm = mm;
        this.worldW = worldW;
        this.worldH = worldH;
    }

    @Override
    public void load() {
        float margin = 60f;
        for (int i = 0; i < HEALTHY_COUNT; i++) spawnFood(FoodType.HEALTHY, margin);
        for (int i = 0; i < UNHEALTHY_COUNT; i++) spawnFood(FoodType.UNHEALTHY, margin);
        for (int i = 0; i < CIGARETTE_COUNT; i++) spawnFood(FoodType.CIGARETTE, margin);
    }

    private void spawnFood(FoodType type, float margin) {
        Entity food = em.createEntity();

        String texturePath;
        switch (type) {
            case HEALTHY:   texturePath = FoodAssets.randomHealthy();   break;
            case UNHEALTHY: texturePath = FoodAssets.randomUnhealthy(); break;
            case CIGARETTE: texturePath = FoodAssets.CIGARETTE;         break;
            default:        texturePath = FoodAssets.randomUnhealthy(); break;
        }

        Sprite sprite = new Sprite(texturePath, FOOD_SIZE, FOOD_SIZE);
        food.add(sprite);
        food.add(new FoodTag(type, RESPAWN_DELAY));
        food.add(new Collider(FOOD_SIZE, FOOD_SIZE, 0, 0, 0, false));

        float x = margin + rng.nextFloat() * (worldW - FOOD_SIZE - margin * 2);
        float y = margin + rng.nextFloat() * (worldH - FOOD_SIZE - margin * 2);
        float vx = -SPEED + rng.nextFloat() * (SPEED * 2);
        float vy = -SPEED + rng.nextFloat() * (SPEED * 2);
        mm.register(food.getId(), new Transform(x, y), new Motion(vx, vy));
    }

    @Override public void update(float dt) { }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0.3f, 0.15f, 0.15f, 1f);
    }
}