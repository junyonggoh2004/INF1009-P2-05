package io.github.some_example_name.healthyeating;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.github.some_example_name.healthyeating.FoodTag.EffectType;
import io.github.some_example_name.healthyeating.FoodTag.FlashType;
import io.github.some_example_name.healthyeating.FoodTag.FoodType;

class FoodTagTest {

    @Test
    void healthyFoodGivesPositivePoints() {
        FoodTag tag = new FoodTag(FoodType.HEALTHY, 2f);
        assertEquals(1, tag.getPoints());
        assertEquals(FoodType.HEALTHY, tag.getType());
    }

    @Test
    void unhealthyFoodGivesMinusOne() {
        FoodTag tag = new FoodTag(FoodType.UNHEALTHY, 2f);
        assertEquals(-1, tag.getPoints());
    }

    @Test
    void olderFoodGivesMinusTwo() {
        FoodTag tag = new FoodTag(FoodType.OLDER, 2f);
        assertEquals(-2, tag.getPoints());
    }

    @Test
    void cigaretteGivesMinusTwo() {
        FoodTag tag = new FoodTag(FoodType.CIGARETTE, 2f);
        assertEquals(-2, tag.getPoints());
    }

    @Test
    void medicineGivesPlusTwo() {
        FoodTag tag = new FoodTag(FoodType.MEDICINE, 2f);
        assertEquals(2, tag.getPoints());
    }

    @Test
    void effectTypesAreCorrect() {
        assertEquals(EffectType.SCORE,  new FoodTag(FoodType.HEALTHY, 1f).getEffectType());
        assertEquals(EffectType.DAMAGE, new FoodTag(FoodType.UNHEALTHY, 1f).getEffectType());
        assertEquals(EffectType.DAMAGE, new FoodTag(FoodType.CIGARETTE, 1f).getEffectType());
        assertEquals(EffectType.DAMAGE, new FoodTag(FoodType.OLDER, 1f).getEffectType());
        assertEquals(EffectType.HEAL,   new FoodTag(FoodType.MEDICINE, 1f).getEffectType());
    }

    @Test
    void flashTypesAreCorrect() {
        assertEquals(FlashType.NONE,   new FoodTag(FoodType.HEALTHY, 1f).getFlashType());
        assertEquals(FlashType.RED,    new FoodTag(FoodType.UNHEALTHY, 1f).getFlashType());
        assertEquals(FlashType.RED,    new FoodTag(FoodType.CIGARETTE, 1f).getFlashType());
        assertEquals(FlashType.PURPLE, new FoodTag(FoodType.OLDER, 1f).getFlashType());
        assertEquals(FlashType.GREEN,  new FoodTag(FoodType.MEDICINE, 1f).getFlashType());
    }

    @Test
    void collectAndRespawnCycle() {
        FoodTag tag = new FoodTag(FoodType.HEALTHY, 1.0f);
        assertFalse(tag.isCollected());

        tag.collect();
        assertTrue(tag.isCollected());
        assertFalse(tag.isReadyToRespawn());

        tag.tick(0.5f);
        assertFalse(tag.isReadyToRespawn());

        tag.tick(0.6f);
        assertTrue(tag.isReadyToRespawn());

        tag.respawn();
        assertFalse(tag.isCollected());
    }

    @Test
    void tickDoesNothingWhenNotCollected() {
        FoodTag tag = new FoodTag(FoodType.HEALTHY, 1.0f);
        tag.tick(10f);
        assertFalse(tag.isCollected());
        assertFalse(tag.isReadyToRespawn());
    }
}
