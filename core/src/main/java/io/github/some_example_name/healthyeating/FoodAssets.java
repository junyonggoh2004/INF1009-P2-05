package io.github.some_example_name.healthyeating;

import java.util.Random;

/**
 * Centralized list of all food image and audio file paths.
 * Update these arrays when you add/remove assets.
 */
public class FoodAssets {

    private static final Random rng = new Random();

    // images for healthy food
    public static final String[] HEALTHY = {
            // fruits
            "healthy/fruits/apple.png",
            "healthy/fruits/banana.png",
            "healthy/fruits/orange.png",
            "healthy/fruits/watermelon.png",
            "healthy/fruits/005-blueberries.png",
            "healthy/fruits/006-mango.png",
            "healthy/fruits/strawberry.png",
            "healthy/fruits/tannat.png",
            // vegetables
            "healthy/vegetables/001-lettuce.png",
            "healthy/vegetables/002-cucumber.png",
            "healthy/vegetables/003-spinach.png",
            "healthy/vegetables/004-broccoli.png",
            "healthy/vegetables/007-carrot.png",
            "healthy/vegetables/008-tomato.png",
            "healthy/vegetables/009-corn.png",
            // protein
            "healthy/protein/chicken-leg.png",
            "healthy/protein/salmon.png",
            "healthy/protein/chop.png",
            // dairy
            "healthy/dairy/milk.png",
            "healthy/dairy/yoghurt.png",
            "healthy/dairy/yogurt.png",
            // carbs
            "healthy/carb/bread.png",
            "healthy/carb/rice.png",
            // others
            "healthy/others/fried-egg.png",
            "healthy/others/glass-of-water.png",
    };

    // medicine items (stage 3 only — heals HP)
    public static final String[] MEDICINE = {
            "healthy/medicine/first-aid-kit.png",
            "healthy/medicine/medicine.png",
            "healthy/medicine/vaccine.png",
    };

    // images for unhealthy food
    public static final String[] UNHEALTHY = {
            "unhealthy/burger.png",
            "unhealthy/chocolate.png",
            "unhealthy/cookie.png",
            "unhealthy/donut.png",
            "unhealthy/french-fries.png",
            "unhealthy/fried-chicken.png",
            "unhealthy/hotdog.png",
            "unhealthy/ice-cream.png",
            "unhealthy/noodles.png",
            "unhealthy/pizza.png",
            "unhealthy/potato-chips.png",
            "unhealthy/soft-drink.png",
            "unhealthy/sweets.png",
            "unhealthy/taco.png",
    };

    // adult-stage unhealthy items
    public static final String CIGARETTE = "unhealthy/older/cigarette.png";
    public static final String[] OLDER_UNHEALTHY = {
            "unhealthy/older/beer.png",
            "unhealthy/older/cigarette.png",
            "unhealthy/older/vape.png",
            "unhealthy/older/vodka.png",
            "unhealthy/older/wine-bottle.png",
    };

    // audio files
    public static final String BGM_1         = "audio/BGM_1.mp3";
    public static final String BGM_2         = "audio/BGM_2.mp3";
    public static final String SFX_BITE      = "audio/Bite.mp3";
    public static final String SFX_SLURP     = "audio/Slurp.mp3";
    public static final String SFX_BUBBLE    = "audio/Bubble-Click.ogg";

    /** Returns a random healthy food image path */
    public static String randomHealthy() {
        return HEALTHY[rng.nextInt(HEALTHY.length)];
    }

    /** Returns a random unhealthy food image path */
    public static String randomUnhealthy() {
        return UNHEALTHY[rng.nextInt(UNHEALTHY.length)];
    }

    /** Returns a random adult-stage unhealthy item (alcohol, cigarettes, vape) */
    public static String randomOlderUnhealthy() {
        return OLDER_UNHEALTHY[rng.nextInt(OLDER_UNHEALTHY.length)];
    }

    /** Returns a random medicine item */
    public static String randomMedicine() {
        return MEDICINE[rng.nextInt(MEDICINE.length)];
    }
}