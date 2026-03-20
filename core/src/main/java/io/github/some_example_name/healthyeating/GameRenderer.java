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
import io.github.some_example_name.scene.Scene;

/**
 * Coordinates all rendering: delegates to PlayerTextureManager and HUDRenderer
 * for player textures and HUD drawing respectively.
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

    // Delegates
    private final PlayerTextureManager playerTextures;
    private final HUDRenderer hudRenderer;

    // Texture cache for food sprites
    private final Map<String, Texture> textureCache;

    // Background textures
    private final Texture menuBg;
    private final Map<PlayerTag.Stage, Texture> stageBgs;
    private final Texture victoryBg;
    private final Texture defeatBg;

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

        camera = new OrthographicCamera();
        camera.position.set(WORLD_W / 2f, WORLD_H / 2f, 0);
        camera.update();
        viewport = new FitViewport(WORLD_W, WORLD_H, camera);

        textureCache = new HashMap<>();
        playerTextures = new PlayerTextureManager();
        hudRenderer = new HUDRenderer();

        // Background textures
        menuBg = new Texture(Gdx.files.internal("backgrounds/menu.png"));
        victoryBg = new Texture(Gdx.files.internal("backgrounds/congratulations.png"));
        defeatBg = new Texture(Gdx.files.internal("backgrounds/game_over.png"));
        stageBgs = new HashMap<>();
        stageBgs.put(PlayerTag.Stage.TODDLER, new Texture(Gdx.files.internal("backgrounds/child_stage.png")));
        stageBgs.put(PlayerTag.Stage.TEEN, new Texture(Gdx.files.internal("backgrounds/teen_stage.png")));
        stageBgs.put(PlayerTag.Stage.ADULT, new Texture(Gdx.files.internal("backgrounds/adult_stage.png")));

        // Guide textures
        guideTexture1 = new Texture(Gdx.files.internal("backgrounds/user_guide1.png"));
        guideTexture2 = new Texture(Gdx.files.internal("backgrounds/user_guide2.png"));
        guideTexture3 = new Texture(Gdx.files.internal("backgrounds/user_guide3.png"));
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Viewport getViewport() {
        return viewport;
    }

    // ─── Screen Flash Effects ───

    public void triggerRedFlash()    { redFlashAlpha = FLASH_MAX_ALPHA; }
    public void triggerGreenFlash()  { greenFlashAlpha = FLASH_MAX_ALPHA; }
    public void triggerPurpleFlash() { purpleFlashAlpha = FLASH_MAX_ALPHA; }

    public void updateFlash(float dt) {
        float decay = (FLASH_MAX_ALPHA / FLASH_DURATION) * dt;
        if (redFlashAlpha > 0)    redFlashAlpha = Math.max(0, redFlashAlpha - decay);
        if (greenFlashAlpha > 0)  greenFlashAlpha = Math.max(0, greenFlashAlpha - decay);
        if (purpleFlashAlpha > 0) purpleFlashAlpha = Math.max(0, purpleFlashAlpha - decay);
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
            bg = endScene.isVictory() ? victoryBg : defeatBg;
        } else {
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
                               Button guideBtn, Button settingsBtn, Button exitBtn) {
        drawBackground(menuScene);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        singlePlayerBtn.drawBackground(shapeRenderer);
        multiPlayerBtn.drawBackground(shapeRenderer);
        guideBtn.drawBackground(shapeRenderer);
        settingsBtn.drawBackground(shapeRenderer);
        exitBtn.drawBackground(shapeRenderer);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        singlePlayerBtn.drawWithBatch(batch, fontLarge);
        multiPlayerBtn.drawWithBatch(batch, fontLarge);
        guideBtn.drawWithBatch(batch, fontLarge);
        settingsBtn.drawWithBatch(batch, fontLarge);
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

        // Draw entities
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity e : em.getActiveEntities()) {
            Sprite sprite = e.getComponent(Sprite.class);
            if (sprite == null || !sprite.isVisible()) continue;

            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            PlayerTag tag = e.getComponent(PlayerTag.class);
            Texture tex = (tag != null)
                    ? playerTextures.getTexture(tag)
                    : getTexture(sprite.getTextureName());

            if (tex != null) {
                batch.draw(tex, t.getX(), t.getY(), sprite.getWidth(), sprite.getHeight());
            }
        }

        batch.end();

        // HUD
        hudRenderer.draw(player1, shapeRenderer, batch, font, camera.combined, WORLD_H);

        // Screen flash overlays
        drawFlashOverlays();
    }

    private void drawFlashOverlays() {
        if (redFlashAlpha <= 0 && greenFlashAlpha <= 0 && purpleFlashAlpha <= 0) return;

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

    // ─── Settings Screen ───

    public void drawSettingsScreen(Button backBtn,
                                   float bgmVolume, float sfxVolume,
                                   float sliderX, float sliderW,
                                   float bgmSliderY, float sfxSliderY) {
        drawBackground(menuScene);
        drawSettingsPanel(backBtn, bgmVolume, sfxVolume, sliderX, sliderW, bgmSliderY, sfxSliderY, "Settings");
    }

    private void drawSettingsPanel(Button backBtn,
                                   float bgmVolume, float sfxVolume,
                                   float sliderX, float sliderW,
                                   float bgmSliderY, float sfxSliderY,
                                   String title) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        shapeRenderer.rect(70f, 85f, WORLD_W - 140f, WORLD_H - 170f);

        float trackH = 10f;
        float knobW = 16f;
        float knobH = 28f;

        shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 1f);
        shapeRenderer.rect(sliderX, bgmSliderY, sliderW, trackH);
        shapeRenderer.rect(sliderX, sfxSliderY, sliderW, trackH);

        shapeRenderer.setColor(0.3f, 0.85f, 0.4f, 1f);
        shapeRenderer.rect(sliderX, bgmSliderY, sliderW * bgmVolume, trackH);
        shapeRenderer.rect(sliderX, sfxSliderY, sliderW * sfxVolume, trackH);

        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(sliderX + sliderW * bgmVolume - knobW / 2f, bgmSliderY - 9f, knobW, knobH);
        shapeRenderer.rect(sliderX + sliderW * sfxVolume - knobW / 2f, sfxSliderY - 9f, knobW, knobH);

        backBtn.drawBackground(shapeRenderer);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        fontLarge.setColor(Color.WHITE);
        fontLarge.draw(batch, title, WORLD_W / 2f - 65f, WORLD_H - 120f);

        font.draw(batch, "Background Music", sliderX, bgmSliderY + 42f);
        font.draw(batch, "Sound Effects", sliderX, sfxSliderY + 42f);
        font.draw(batch, (int) (bgmVolume * 100f) + "%", sliderX + sliderW + 12f, bgmSliderY + 12f);
        font.draw(batch, (int) (sfxVolume * 100f) + "%", sliderX + sliderW + 12f, sfxSliderY + 12f);

        backBtn.drawWithBatch(batch, fontLarge);
        batch.end();
    }

    // ─── Pause Menu Overlay ───

    public void drawPauseMenuOverlay(Button resumeBtn, Button settingsBtn, Button quitBtn) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
        shapeRenderer.rect(0, 0, WORLD_W, WORLD_H);
        shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        shapeRenderer.rect(170f, 110f, 300f, 260f);

        resumeBtn.drawBackground(shapeRenderer);
        settingsBtn.drawBackground(shapeRenderer);
        quitBtn.drawBackground(shapeRenderer);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        fontLarge.setColor(Color.WHITE);
        fontLarge.draw(batch, "Paused", WORLD_W / 2f - 55f, WORLD_H - 145f);
        resumeBtn.drawWithBatch(batch, fontLarge);
        settingsBtn.drawWithBatch(batch, fontLarge);
        quitBtn.drawWithBatch(batch, fontLarge);
        batch.end();
    }

    // ─── Gameplay Settings Overlay ───

    public void drawSettingsOverlayOnGameplay(Button backBtn,
                                              float bgmVolume, float sfxVolume,
                                              float sliderX, float sliderW,
                                              float bgmSliderY, float sfxSliderY) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
        shapeRenderer.rect(0, 0, WORLD_W, WORLD_H);
        shapeRenderer.end();

        drawSettingsPanel(backBtn, bgmVolume, sfxVolume, sliderX, sliderW, bgmSliderY, sfxSliderY, "Settings");
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

        playerTextures.dispose();

        if (menuBg != null) menuBg.dispose();
        if (victoryBg != null) victoryBg.dispose();
        if (defeatBg != null) defeatBg.dispose();
        for (Texture tex : stageBgs.values()) if (tex != null) tex.dispose();

        if (guideTexture1 != null) guideTexture1.dispose();
        if (guideTexture2 != null) guideTexture2.dispose();
        if (guideTexture3 != null) guideTexture3.dispose();
    }
}
