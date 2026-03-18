package io.github.some_example_name.scene;

import java.util.Random;

import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.factory.FoodEntityFactory;
import io.github.some_example_name.factory.FoodSpawnSpec;
import io.github.some_example_name.factory.SpawnHelper;
import io.github.some_example_name.factory.StageContentFactory;
import io.github.some_example_name.factory.StageProfile;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;
import io.github.some_example_name.movement.MovementManager;

/**
 * Reusable scene that spawns food entities based on a StageContentFactory.
 * Replaces Stage1Scene, Stage2Scene, and Stage3Scene with a single class
 * driven by factory configuration (Open/Closed Principle).
 */
public class FoodStageScene extends Scene {

    private final EntityManager em;
    private final MovementManager mm;
    private final float worldW, worldH;
    private final StageContentFactory contentFactory;
    private final FoodEntityFactory foodFactory;
    private final SpawnHelper spawnHelper;
    private final Random rng;

    public FoodStageScene(StageContentFactory contentFactory, FoodEntityFactory foodFactory,
                          EntityManager em, MovementManager mm, float worldW, float worldH) {
        super(contentFactory.getProfile().getSceneName());
        this.contentFactory = contentFactory;
        this.foodFactory = foodFactory;
        this.em = em;
        this.mm = mm;
        this.worldW = worldW;
        this.worldH = worldH;
        this.rng = new Random();
        this.spawnHelper = new SpawnHelper(rng);
    }

    @Override
    public void load() {
        StageProfile profile = contentFactory.getProfile();

        spawnFoodBatch(FoodType.HEALTHY,   profile.getHealthyCount(),   profile);
        spawnFoodBatch(FoodType.UNHEALTHY, profile.getUnhealthyCount(), profile);
        spawnFoodBatch(FoodType.OLDER,     profile.getOlderCount(),     profile);
        spawnFoodBatch(FoodType.CIGARETTE, profile.getCigaretteCount(), profile);
        spawnFoodBatch(FoodType.MEDICINE,  profile.getMedicineCount(),   profile);
    }

    private void spawnFoodBatch(FoodType type, int count, StageProfile profile) {
        for (int i = 0; i < count; i++) {
            String texturePath = contentFactory.resolveTexturePath(type, rng);

            FoodSpawnSpec spec = new FoodSpawnSpec(
                    type,
                    texturePath,
                    profile.getFoodSize(),
                    profile.getRespawnDelay(),
                    spawnHelper.randomPosition(worldW, profile.getFoodSize(), profile.getSpawnMargin()),
                    spawnHelper.randomPosition(worldH, profile.getFoodSize(), profile.getSpawnMargin()),
                    spawnHelper.randomVelocity(profile.getMaxFoodSpeed()),
                    spawnHelper.randomVelocity(profile.getMaxFoodSpeed()));

            foodFactory.createFood(spec, em, mm);
        }
    }

    @Override
    public void update(float dt) { }

    @Override
    public void render(Renderer renderer) {
        StageProfile profile = contentFactory.getProfile();
        renderer.clear(profile.getBgR(), profile.getBgG(), profile.getBgB(), profile.getBgA());
    }

    /** Returns the factory that created this scene's content. */
    public StageContentFactory getContentFactory() {
        return contentFactory;
    }
}
