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
            "healthy/fruits/apple.png",
            "healthy/fruits/banana.png",
            "healthy/fruits/orange.png",
            "healthy/fruits/watermelon.png",
            "healthy/vegetables/004-broccoli.png",
            "healthy/vegetables/007-carrot.png",
            "healthy/protein/chicken-leg.png",
            "healthy/protein/salmon.png",
            "healthy/dairy/milk.png",
            "healthy/dairy/cheese.png",
            "healthy/dairy/yoghurt.png",
            "healthy/dairy/yogurt.png",
            "healthy/carb/bread.png",
            "healthy/carb/rice.png",
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
    };
    public static final String CIGARETTE = "unhealthy/older/cigarette.png";

    // audio files
    public static final String BGM_1         = "audio/BGM_1.mp3";
    public static final String BGM_2         = "audio/BGM_2.mp3";
    public static final String SFX_BITE      = "audio/Bite.mp3";
    public static final String SFX_SLURP     = "audio/Slurp.mp3";
    public static final String SFX_BUBBLE    = "audio/Bubble-Click.ogg";
    public static final String SFX_WALKING   = "audio/Walking.mp3";

    /** Returns a random healthy food image path */
    public static String randomHealthy() {
        return HEALTHY[rng.nextInt(HEALTHY.length)];
    }

    /** Returns a random unhealthy food image path */
    public static String randomUnhealthy() {
        return UNHEALTHY[rng.nextInt(UNHEALTHY.length)];
    }
}