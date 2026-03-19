package io.github.some_example_name.integration;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.healthyeating.FoodTag;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;
import io.github.some_example_name.healthyeating.factory.FoodEntityFactory;
import io.github.some_example_name.healthyeating.factory.StageContentFactory;
import io.github.some_example_name.healthyeating.factory.StageFactoryRegistry;
import io.github.some_example_name.healthyeating.factory.StageProfile;
import io.github.some_example_name.healthyeating.FoodStageScene;
import io.github.some_example_name.movement.MovementManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: verifies the full pipeline from StageFactoryRegistry → StageContentFactory
 * → FoodStageScene.load() → correct entities spawned with right FoodTypes.
 */
class StageFactoryPipelineTest {

    private EntityManager em;
    private MovementManager mm;
    private FoodEntityFactory foodFactory;
    private StageFactoryRegistry registry;

    @BeforeEach
    void setUp() {
        em = new EntityManager();
        mm = new MovementManager();
        mm.init();
        foodFactory = new FoodEntityFactory();
        registry = new StageFactoryRegistry();
    }

    private int countFoodByType(FoodType type) {
        int count = 0;
        for (Entity e : em.getActiveEntities()) {
            FoodTag tag = e.getComponent(FoodTag.class);
            if (tag != null && tag.getType() == type) count++;
        }
        return count;
    }

    @Test
    void toddlerStageSpawnsCorrectFoodCounts() {
        StageContentFactory factory = registry.get(Stage.TODDLER);
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, 640, 480);
        scene.load();

        assertEquals(6, countFoodByType(FoodType.HEALTHY));
        assertEquals(3, countFoodByType(FoodType.UNHEALTHY));
        assertEquals(0, countFoodByType(FoodType.OLDER));
        assertEquals(0, countFoodByType(FoodType.CIGARETTE));
        assertEquals(0, countFoodByType(FoodType.MEDICINE));
        assertEquals(9, em.getActiveEntities().size());
    }

    @Test
    void teenStageSpawnsCorrectFoodCounts() {
        StageContentFactory factory = registry.get(Stage.TEEN);
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, 640, 480);
        scene.load();

        assertEquals(5, countFoodByType(FoodType.HEALTHY));
        assertEquals(5, countFoodByType(FoodType.UNHEALTHY));
        assertEquals(0, countFoodByType(FoodType.OLDER));
        assertEquals(0, countFoodByType(FoodType.CIGARETTE));
        assertEquals(0, countFoodByType(FoodType.MEDICINE));
        assertEquals(10, em.getActiveEntities().size());
    }

    @Test
    void adultStageSpawnsCorrectFoodCounts() {
        StageContentFactory factory = registry.get(Stage.ADULT);
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, 640, 480);
        scene.load();

        assertEquals(4, countFoodByType(FoodType.HEALTHY));
        assertEquals(4, countFoodByType(FoodType.UNHEALTHY));
        assertEquals(3, countFoodByType(FoodType.OLDER));
        assertEquals(2, countFoodByType(FoodType.CIGARETTE));
        assertEquals(2, countFoodByType(FoodType.MEDICINE));
        assertEquals(15, em.getActiveEntities().size());
    }

    @Test
    void toddlerFoodHasZeroVelocity() {
        StageContentFactory factory = registry.get(Stage.TODDLER);
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, 640, 480);
        scene.load();

        for (Entity e : em.getActiveEntities()) {
            assertEquals(0f, mm.getMotion(e.getId()).getVx());
            assertEquals(0f, mm.getMotion(e.getId()).getVy());
        }
    }

    @Test
    void teenFoodHasVelocityWithinBounds() {
        StageContentFactory factory = registry.get(Stage.TEEN);
        StageProfile profile = factory.getProfile();
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, 640, 480);
        scene.load();

        float maxSpeed = profile.getMaxFoodSpeed();
        for (Entity e : em.getActiveEntities()) {
            float vx = mm.getMotion(e.getId()).getVx();
            float vy = mm.getMotion(e.getId()).getVy();
            assertTrue(Math.abs(vx) <= maxSpeed, "vx=" + vx + " exceeds maxSpeed=" + maxSpeed);
            assertTrue(Math.abs(vy) <= maxSpeed, "vy=" + vy + " exceeds maxSpeed=" + maxSpeed);
        }
    }

    @Test
    void adultFoodHasVelocityWithinBounds() {
        StageContentFactory factory = registry.get(Stage.ADULT);
        StageProfile profile = factory.getProfile();
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, 640, 480);
        scene.load();

        float maxSpeed = profile.getMaxFoodSpeed();
        for (Entity e : em.getActiveEntities()) {
            float vx = mm.getMotion(e.getId()).getVx();
            float vy = mm.getMotion(e.getId()).getVy();
            assertTrue(Math.abs(vx) <= maxSpeed, "vx=" + vx + " exceeds maxSpeed=" + maxSpeed);
            assertTrue(Math.abs(vy) <= maxSpeed, "vy=" + vy + " exceeds maxSpeed=" + maxSpeed);
        }
    }

    @Test
    void allFoodEntitiesHaveRequiredComponents() {
        StageContentFactory factory = registry.get(Stage.ADULT);
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, 640, 480);
        scene.load();

        for (Entity e : em.getActiveEntities()) {
            assertNotNull(e.getComponent(FoodTag.class), "Missing FoodTag");
            assertNotNull(e.getComponent(io.github.some_example_name.entity.Sprite.class), "Missing Sprite");
            assertNotNull(e.getComponent(io.github.some_example_name.collision.Collider.class), "Missing Collider");
            assertNotNull(mm.getTransform(e.getId()), "Missing Transform");
            assertNotNull(mm.getMotion(e.getId()), "Missing Motion");
        }
    }

    @Test
    void stageProgressionChainCoversAllStages() {
        StageProfile current = registry.get(Stage.TODDLER).getProfile();
        int stageCount = 1;
        while (current.getNextStage() != null) {
            current = registry.get(current.getNextStage()).getProfile();
            stageCount++;
        }
        assertEquals(3, stageCount);
        assertTrue(current.isFinalStage());
    }

    @Test
    void foodSpawnsWithinWorldBounds() {
        float worldW = 640f, worldH = 480f;
        StageContentFactory factory = registry.get(Stage.ADULT);
        FoodStageScene scene = new FoodStageScene(factory, foodFactory, em, mm, worldW, worldH);
        scene.load();

        for (Entity e : em.getActiveEntities()) {
            float x = mm.getTransform(e.getId()).getX();
            float y = mm.getTransform(e.getId()).getY();
            assertTrue(x >= 0 && x <= worldW, "Food x=" + x + " out of bounds");
            assertTrue(y >= 0 && y <= worldH, "Food y=" + y + " out of bounds");
        }
    }
}
