package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.factory.PlayerEntityFactory;
import io.github.some_example_name.scene.EndScene;
import io.github.some_example_name.scene.FoodStageScene;
import io.github.some_example_name.scene.MenuScene;
import io.github.some_example_name.scene.Scene;

/**
 * Handles all rendering: backgrounds, entities, HUD, menus, transitions,
 * red flash effect, and viewport management.
 */
public class GameRenderer {

    // Virtual resolution
    public static final float WORLD_W = 640f;
    public static final float WORLD_H = 480f;

    // Rendering
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final BitmapFont fontLarge;

    // Camera + Viewport
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    // Texture cache
    private final Map<String, Texture> textureCache;

    // Player textures: [stage][expression] — guy (P1) and girl (P2)
    // Stage: 0=toddler, 1=teen, 2=adult
    // Expression: 0=default, 1=eating, 2=sad
    private final Texture[][] guyTextures;
    private final Texture[][] girlTextures;

    // Background textures
    private final Texture menuBg;
    private final Map<PlayerTag.Stage, Texture> stageBgs;
    private final Texture endBg;

    // Guide textures
    private final Texture guideTexture1;
    private final Texture guideTexture2;
    private final Texture guideTexture3;

    // Scene references (for background selection)
    private final MenuScene menuScene;
    private final Map<PlayerTag.Stage, FoodStageScene> stageScenes;
    private final EndScene endScene;

    // Screen flash effects
    private float redFlashAlpha = 0f;
    private float greenFlashAlpha = 0f;
    private float purpleFlashAlpha = 0f;
    private static final float FLASH_DURATION = 0.4f;
    private static final float FLASH_MAX_ALPHA = 0.35f;

    public GameRenderer(MenuScene menuScene, EndScene endScene,
                        Map<PlayerTag.Stage, FoodStageScene> stageScenes) {
        this.menuScene = menuScene;
        this.endScene = endScene;
        this.stageScenes = stageScenes;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        fontLarge = new BitmapFont();
        fontLarge.setColor(Color.WHITE);
        fontLarge.getData().setScale(2f);

        // Camera + viewport
        camera = new OrthographicCamera();
        camera.position.set(WORLD_W / 2f, WORLD_H / 2f, 0);
        camera.update();
        viewport = new FitViewport(WORLD_W, WORLD_H, camera);

        // Texture cache
        textureCache = new HashMap<>();

        // Player textures — guy[stage][expr], girl[stage][expr]
        // Player textures: [stage][expression] — guy (P1) and girl (P2)
        // Expression: 0=default, 1=eating, 2=sad
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

        // Background textures
        menuBg = new Texture(Gdx.files.internal("backgrounds/menu.png"));
        endBg  = new Texture(Gdx.files.internal("backgrounds/game_over.png"));
        stageBgs = new HashMap<>();
        stageBgs.put(PlayerTag.Stage.TODDLER, new Texture(Gdx.files.internal("backgrounds/child_stage.png")));
        stageBgs.put(PlayerTag.Stage.TEEN, new Texture(Gdx.files.internal("backgrounds/teen_stage.png")));
        stageBgs.put(PlayerTag.Stage.ADULT, new Texture(Gdx.files.internal("backgrounds/adult_stage.png")));

        // Guide textures
        guideTexture1 = new Texture(Gdx.files.internal("backgrounds/user_guide1.png"));
        guideTexture2 = new Texture(Gdx.files.internal("backgrounds/user_guide2.png"));
        guideTexture3 = new Texture(Gdx.files.internal("backgrounds/user_guide3.png"));
    }

    /** Must be called when window is resized. */
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Viewport getViewport() {
        return viewport;
    }

    // ─── Screen Flash Effects ───

    /** Red flash — regular unhealthy food / cigarette (-1 or -3 HP). */
    public void triggerRedFlash() {
        redFlashAlpha = FLASH_MAX_ALPHA;
    }

    /** Green flash — medicine collected (+1 HP heal). */
    public void triggerGreenFlash() {
        greenFlashAlpha = FLASH_MAX_ALPHA;
    }

    /** Purple flash — older unhealthy items like alcohol/vape (-2 HP). */
    public void triggerPurpleFlash() {
        purpleFlashAlpha = FLASH_MAX_ALPHA;
    }

    public void updateFlash(float dt) {
        float decay = (FLASH_MAX_ALPHA / FLASH_DURATION) * dt;
        if (redFlashAlpha > 0) {
            redFlashAlpha -= decay;
            if (redFlashAlpha < 0) redFlashAlpha = 0;
        }
        if (greenFlashAlpha > 0) {
            greenFlashAlpha -= decay;
            if (greenFlashAlpha < 0) greenFlashAlpha = 0;
        }
        if (purpleFlashAlpha > 0) {
            purpleFlashAlpha -= decay;
            if (purpleFlashAlpha < 0) purpleFlashAlpha = 0;
        }
    }

    // ─── Texture Helper ───

    private Texture getTexture(String path) {
        if (!textureCache.containsKey(path)) {
            try {
                textureCache.put(path, new Texture(Gdx.files.internal(path)));
            } catch (Exception e) {
                return null;
            }
        }
        return textureCache.get(path);
    }

    // ─── Background ───

    private void drawBackground(Scene current) {
        if (current != null) {
            current.render((r, g, b, a) -> Gdx.gl.glClearColor(r, g, b, a));
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Texture bg = null;
        if (current == menuScene) {
            bg = menuBg;
        } else if (current == endScene) {
            bg = endBg;
        } else {
            // Check if it's a stage scene — match by identity
            for (Map.Entry<PlayerTag.Stage, FoodStageScene> entry : stageScenes.entrySet()) {
                if (current == entry.getValue()) {
                    bg = stageBgs.get(entry.getKey());
                    break;
                }
            }
        }

        if (bg != null) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.draw(bg, 0, 0, WORLD_W, WORLD_H);
            batch.end();
        }
    }

    // ─── Menu Screen ───

    public void drawMenuScreen(Button singlePlayerBtn, Button multiPlayerBtn,
                               Button guideBtn, Button exitBtn) {
        drawBackground(menuScene);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        singlePlayerBtn.drawBackground(shapeRenderer);
        multiPlayerBtn.drawBackground(shapeRenderer);
        guideBtn.drawBackground(shapeRenderer);
        exitBtn.drawBackground(shapeRenderer);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        singlePlayerBtn.drawWithBatch(batch, fontLarge);
        multiPlayerBtn.drawWithBatch(batch, fontLarge);
        guideBtn.drawWithBatch(batch, fontLarge);
        exitBtn.drawWithBatch(batch, fontLarge);
        batch.end();
    }

    // ─── Guide Screen ───

    public void drawGuideScreen(int guidePage) {
        drawBackground(menuScene);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        switch (guidePage) {
            case 0: if (guideTexture1 != null) batch.draw(guideTexture1, 0, 0, WORLD_W, WORLD_H); break;
            case 1: if (guideTexture2 != null) batch.draw(guideTexture2, 0, 0, WORLD_W, WORLD_H); break;
            case 2: if (guideTexture3 != null) batch.draw(guideTexture3, 0, 0, WORLD_W, WORLD_H); break;
        }
        batch.end();
    }

    // ─── End Screen ───

    public void drawEndScreen(EndScene endSceneRef, Button restartBtn, Button menuBtn) {
        drawBackground(endSceneRef);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (endSceneRef.isVictory()) {
            fontLarge.setColor(Color.GREEN);
            fontLarge.draw(batch, "Congratulations!", WORLD_W / 2f - 150, WORLD_H / 2f + 80);
            fontLarge.setColor(Color.WHITE);
            font.draw(batch, "You grew up healthy and strong!", WORLD_W / 2f - 120, WORLD_H / 2f + 30);
        } else {
            fontLarge.setColor(Color.RED);
            fontLarge.draw(batch, "Game Over!", WORLD_W / 2f - 100, WORLD_H / 2f + 80);
            fontLarge.setColor(Color.WHITE);
            font.draw(batch, "Too much unhealthy food...", WORLD_W / 2f - 100, WORLD_H / 2f + 30);
        }
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        restartBtn.drawBackground(shapeRenderer);
        menuBtn.drawBackground(shapeRenderer);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        restartBtn.drawWithBatch(batch, fontLarge);
        menuBtn.drawWithBatch(batch, fontLarge);
        batch.end();
    }

    // ─── Transition Overlay ───

    public void drawTransitionOverlay(String message, int countdown) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.6f);
        shapeRenderer.rect(0, 0, WORLD_W, WORLD_H);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        fontLarge.setColor(Color.YELLOW);
        fontLarge.draw(batch, message, WORLD_W / 2f - 180, WORLD_H / 2f + 40);
        fontLarge.setColor(Color.WHITE);
        fontLarge.draw(batch, String.valueOf(countdown), WORLD_W / 2f - 10, WORLD_H / 2f - 20);
        batch.end();
    }

    // ─── Gameplay ───

    public void drawGameplay(Scene current, EntityManager em, MovementManager mm,
                             Entity player1, Entity player2) {
        drawBackground(current);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw entities with textures
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity e : em.getActiveEntities()) {
            Sprite sprite = e.getComponent(Sprite.class);
            if (sprite == null || !sprite.isVisible()) continue;

            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            PlayerTag tag = e.getComponent(PlayerTag.class);
            Texture tex = null;
            if (tag != null) {
                tex = getPlayerTexture(tag);
            } else {
                tex = getTexture(sprite.getTextureName());
            }

            if (tex != null) {
                batch.draw(tex, t.getX(), t.getY(), sprite.getWidth(), sprite.getHeight());
            }
        }

        batch.end();

        // HUD (uses player1's shared components)
        drawHUD(player1);

        // Screen flash overlays
        if (redFlashAlpha > 0 || greenFlashAlpha > 0 || purpleFlashAlpha > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (redFlashAlpha > 0) {
                shapeRenderer.setColor(1f, 0f, 0f, redFlashAlpha);
                shapeRenderer.rect(0, 0, WORLD_W, WORLD_H);
            }
            if (greenFlashAlpha > 0) {
                shapeRenderer.setColor(0f, 1f, 0.3f, greenFlashAlpha);
                shapeRenderer.rect(0, 0, WORLD_W, WORLD_H);
            }
            if (purpleFlashAlpha > 0) {
                shapeRenderer.setColor(0.6f, 0f, 0.8f, purpleFlashAlpha);
                shapeRenderer.rect(0, 0, WORLD_W, WORLD_H);
            }
            shapeRenderer.end();
        }
    }

    // ─── Player Texture Selection ───

    private Texture getPlayerTexture(PlayerTag tag) {
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

        // P1 = guy, P2 = girl
        if (tag.getPlayerNumber() == 2) {
            return girlTextures[stageIdx][exprIdx];
        } else {
            return guyTextures[stageIdx][exprIdx];
        }
    }

    // ─── HUD ───

    private void drawHUD(Entity player) {
        if (player == null) return;

        ScoreTracker tracker = player.getComponent(ScoreTracker.class);
        HealthBar health = player.getComponent(HealthBar.class);
        PlayerTag tag = player.getComponent(PlayerTag.class);
        if (tracker == null || health == null || tag == null) return;

        float barX = 10f;
        float barY = WORLD_H - 25f;
        float barW = 200f;
        float barH = 18f;
        float progress = tracker.getProgress();

        shapeRenderer.setProjectionMatrix(camera.combined);
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

        batch.setProjectionMatrix(camera.combined);
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

    // ─── Dispose ───

    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (fontLarge != null) fontLarge.dispose();

        if (textureCache != null) {
            for (Texture t : textureCache.values()) {
                if (t != null) t.dispose();
            }
            textureCache.clear();
        }

        for (Texture[] row : guyTextures)
            for (Texture tex : row) if (tex != null) tex.dispose();
        for (Texture[] row : girlTextures)
            for (Texture tex : row) if (tex != null) tex.dispose();

        if (menuBg != null) menuBg.dispose();
        if (endBg != null) endBg.dispose();
        for (Texture tex : stageBgs.values()) if (tex != null) tex.dispose();

        if (guideTexture1 != null) guideTexture1.dispose();
        if (guideTexture2 != null) guideTexture2.dispose();
        if (guideTexture3 != null) guideTexture3.dispose();
    }
}
