package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

import io.github.some_example_name.entity.Entity;

/**
 * Renders the in-game HUD: progress bar, hearts, and stage/score text.
 */
public class HUDRenderer {

    /** Draws the HUD for the given player entity. */
    public void draw(Entity player, ShapeRenderer shapeRenderer, SpriteBatch batch,
                     BitmapFont font, Matrix4 projection, float worldH) {
        if (player == null) return;

        ScoreTracker tracker = player.getComponent(ScoreTracker.class);
        HealthBar health = player.getComponent(HealthBar.class);
        PlayerTag tag = player.getComponent(PlayerTag.class);
        if (tracker == null || health == null || tag == null) return;

        float barX = 10f;
        float barY = worldH - 25f;
        float barW = 200f;
        float barH = 18f;
        float progress = tracker.getProgress();

        shapeRenderer.setProjectionMatrix(projection);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Progress bar background
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(barX, barY, barW, barH);

        // Progress bar fill
        shapeRenderer.setColor(0.2f, 0.8f, 0.3f, 1f);
        shapeRenderer.rect(barX, barY, barW * progress, barH);

        // Hearts
        float heartX = barX + barW + 20f;
        float heartSize = 18f;
        for (int i = 0; i < health.getMaxHearts(); i++) {
            if (i < health.getHearts()) {
                shapeRenderer.setColor(1f, 0.2f, 0.2f, 1f);
            } else {
                shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);
            }
            shapeRenderer.rect(heartX + i * (heartSize + 5), barY, heartSize, heartSize);
        }

        shapeRenderer.end();

        batch.setProjectionMatrix(projection);
        batch.begin();

        String stageName;
        switch (tag.getCurrentStage()) {
            case TODDLER: stageName = "Toddler"; break;
            case TEEN:    stageName = "Teen";     break;
            case ADULT:   stageName = "Adult";    break;
            default:      stageName = "Unknown";  break;
        }

        font.setColor(Color.WHITE);
        font.draw(batch, stageName + "  |  Score: " + tracker.getScore() + " / " + tracker.getThreshold(),
                barX, barY - 5);
        batch.end();
    }
}
