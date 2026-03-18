package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Manages player character textures for both genders across all stages and expressions.
 * Textures are organized as [stage][expression] arrays for guy (P1) and girl (P2).
 */
public class PlayerTextureManager {

    // [stage: 0=toddler, 1=teen, 2=adult][expression: 0=default, 1=eating, 2=sad]
    private final Texture[][] guyTextures;
    private final Texture[][] girlTextures;

    public PlayerTextureManager() {
        guyTextures = new Texture[][] {
            { // Toddler
                new Texture(Gdx.files.internal("characters/default_guy_child.png")),
                new Texture(Gdx.files.internal("characters/toddler_guy_eating.png")),
                new Texture(Gdx.files.internal("characters/toddler_guy_sad.png")),
            },
            { // Teen
                new Texture(Gdx.files.internal("characters/default_guy_teen.png")),
                new Texture(Gdx.files.internal("characters/teen_guy_eating.png")),
                new Texture(Gdx.files.internal("characters/teen_guy_sad.png")),
            },
            { // Adult
                new Texture(Gdx.files.internal("characters/default_guy_adult.png")),
                new Texture(Gdx.files.internal("characters/adult_guy_eating.png")),
                new Texture(Gdx.files.internal("characters/adult_guy_sad.png")),
            },
        };
        girlTextures = new Texture[][] {
            { // Toddler
                new Texture(Gdx.files.internal("characters/default_girl_child.png")),
                new Texture(Gdx.files.internal("characters/toddler_girl_eating.png")),
                new Texture(Gdx.files.internal("characters/toddler_girl_sad.png")),
            },
            { // Teen
                new Texture(Gdx.files.internal("characters/default_girl_teen.png")),
                new Texture(Gdx.files.internal("characters/teen_girl_eating.png")),
                new Texture(Gdx.files.internal("characters/teen_girl_sad.png")),
            },
            { // Adult
                new Texture(Gdx.files.internal("characters/default_girl_adult.png")),
                new Texture(Gdx.files.internal("characters/adult_girl_eating.png")),
                new Texture(Gdx.files.internal("characters/adult_girl_sad.png")),
            },
        };
    }

    /** Returns the correct texture for a player based on their stage, expression, and player number. */
    public Texture getTexture(PlayerTag tag) {
        int stageIdx;
        switch (tag.getCurrentStage()) {
            case TODDLER: stageIdx = 0; break;
            case TEEN:    stageIdx = 1; break;
            case ADULT:   stageIdx = 2; break;
            default:      stageIdx = 0; break;
        }

        int exprIdx;
        switch (tag.getExpression()) {
            case EATING: exprIdx = 1; break;
            case SAD:    exprIdx = 2; break;
            default:     exprIdx = 0; break;
        }

        if (tag.getPlayerNumber() == 2) {
            return girlTextures[stageIdx][exprIdx];
        } else {
            return guyTextures[stageIdx][exprIdx];
        }
    }

    public void dispose() {
        for (Texture[] row : guyTextures)
            for (Texture tex : row) if (tex != null) tex.dispose();
        for (Texture[] row : girlTextures)
            for (Texture tex : row) if (tex != null) tex.dispose();
    }
}
