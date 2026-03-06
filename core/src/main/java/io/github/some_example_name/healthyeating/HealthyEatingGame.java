package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.CollisionManager;
import io.github.some_example_name.core.EngineCore;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.scene.EndScene;
import io.github.some_example_name.scene.MenuScene;
import io.github.some_example_name.scene.Scene;
import io.github.some_example_name.scene.SceneManager;
import io.github.some_example_name.scene.Stage1Scene;
import io.github.some_example_name.scene.Stage2Scene;
import io.github.some_example_name.scene.Stage3Scene;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Healthy Eating Game — built on the abstract engine.
 *
 * Toddler (Stage 1) → Teen (Stage 2) → Adult (Stage 3) → Congratulations
 * Collect healthy food (+1 score), avoid unhealthy food (-1 heart),
 * avoid cigarettes in Stage 3 (-3 hearts).
 */
public class HealthyEatingGame extends ApplicationAdapter {

    // engine
    private EngineCore engine;
    private EntityManager em;
    private MovementManager mm;
    private CollisionManager cm;
    private SceneManager sm;

    // rendering
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont fontLarge;

    // textures
    private Map<String, Texture> textureCache;

    // player textures
    private Texture toddlerTex;
    private Texture teenTex;
    private Texture adultTex;

    // background textures
    private Texture menuBg;
    private Texture stage1Bg;
    private Texture stage2Bg;
    private Texture stage3Bg;
    private Texture endBg;

    // user guide textures
    private Texture guideTexture1;
    private Texture guideTexture2;
    private Texture guideTexture3;

    // audio
    private Music bgm;
    private Sound sfxBite;
    private Sound sfxSlurp;
    private Sound sfxBubble;

    // config
    private static final float PLAYER_SIZE = 70f;
    private static final float PLAYER_SPEED = 300f;
    private static final int MAX_HEARTS = 5;
    private static final int STAGE1_THRESHOLD = 10;
    private static final int STAGE2_THRESHOLD = 20;
    private static final int STAGE3_THRESHOLD = 30;

    // state
    private Entity player;
    private FoodCollectHandler foodHandler;
    private final Random rng = new Random();
    private float worldW, worldH;

    // scenes
    private MenuScene menuScene;
    private Stage1Scene stage1Scene;
    private Stage2Scene stage2Scene;
    private Stage3Scene stage3Scene;
    private EndScene endScene;

    // transition overlay
    private boolean showingTransition = false;
    private float transitionTimer = 0f;
    private String transitionMessage = "";
    private static final float TRANSITION_DURATION = 2.0f;

    // menu buttons
    private Button startBtn;
    private Button guideBtn;
    private Button exitBtn;

    // end screen buttons
    private Button restartBtn;
    private Button menuBtn;

    // guide state
    private boolean showingGuide = false;
    private int guidePage = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        fontLarge = new BitmapFont();
        fontLarge.setColor(Color.WHITE);
        fontLarge.getData().setScale(2f);

        worldW = Gdx.graphics.getWidth();
        worldH = Gdx.graphics.getHeight();

        // texture cache for food images
        textureCache = new HashMap<>();

        // Player textures
        toddlerTex = new Texture(Gdx.files.internal("characters/child.png"));
        teenTex    = new Texture(Gdx.files.internal("characters/teen.png"));
        adultTex   = new Texture(Gdx.files.internal("characters/adult.png"));

        // Background textures
        menuBg   = new Texture(Gdx.files.internal("backgrounds/menu.png"));
        stage1Bg = new Texture(Gdx.files.internal("backgrounds/child_stage.jpg"));
        stage2Bg = new Texture(Gdx.files.internal("backgrounds/teen_stage.png"));
        stage3Bg = new Texture(Gdx.files.internal("backgrounds/adult_stage.png"));
        endBg = new Texture(Gdx.files.internal("game_over.png"));

        // User guide textures
        guideTexture1 = new Texture(Gdx.files.internal("backgrounds/user_guide1.png"));
        guideTexture2 = new Texture(Gdx.files.internal("backgrounds/user_guide2.png"));
        guideTexture3 = new Texture(Gdx.files.internal("backgrounds/user_guide3.png"));

        // Audio
        bgm       = Gdx.audio.newMusic(Gdx.files.internal(FoodAssets.BGM_1));
        sfxBite   = Gdx.audio.newSound(Gdx.files.internal(FoodAssets.SFX_BITE));
        sfxSlurp  = Gdx.audio.newSound(Gdx.files.internal(FoodAssets.SFX_SLURP));
        sfxBubble = Gdx.audio.newSound(Gdx.files.internal(FoodAssets.SFX_BUBBLE));

        bgm.setLooping(true);
        bgm.setVolume(0.4f);
        bgm.play();

        // ─── Menu buttons ───
        float btnW = worldW * 0.28f;
        float btnH = worldH * 0.12f;

        startBtn = new Button(
                worldW * 0.12f, worldH * 0.14f,
                worldW * 0.27f, worldH * 0.13f,
                "", new Color(0, 0, 0, 0.0f), new Color(1f, 1f, 1f, 0.15f));

        guideBtn = new Button(
                worldW * 0.33f, worldH * 0.5f,
                worldW * 0.34f, worldH * 0.15f,
                "", new Color(0, 0, 0, 0.0f), new Color(1f, 1f, 1f, 0.15f));

        exitBtn = new Button(
                worldW * 0.61f, worldH * 0.14f,
                worldW * 0.27f, worldH * 0.13f,
                "", new Color(0, 0, 0, 0.0f), new Color(1f, 1f, 1f, 0.15f));

        // End screen buttons
        float endBtnW = 200f;
        float endBtnH = 50f;
        float endBtnY = worldH * 0.25f;

        restartBtn = new Button(
                worldW / 2f - endBtnW - 20f, endBtnY,
                endBtnW, endBtnH,
                "Play Again", new Color(0.2f, 0.75f, 0.3f, 0.9f), new Color(0.3f, 0.9f, 0.4f, 1f));

        menuBtn = new Button(
                worldW / 2f + 20f, endBtnY,
                endBtnW, endBtnH,
                "Main Menu", new Color(0.2f, 0.5f, 0.9f, 0.9f), new Color(0.3f, 0.6f, 1f, 1f));

        // initiate engine
        engine = new EngineCore();
        engine.init();

        em = engine.getEntityManager();
        mm = engine.getMovementManager();
        cm = engine.getCollisionManager();
        sm = engine.getSceneManager();

        // create the scenes at start
        menuScene   = new MenuScene();
        stage1Scene = new Stage1Scene(em, mm, worldW, worldH);
        stage2Scene = new Stage2Scene(em, mm, worldW, worldH);
        stage3Scene = new Stage3Scene(em, mm, worldW, worldH);
        endScene    = new EndScene();

        sm.setScene(menuScene);
    }

    //  Texture Helper (lazy loading + caching)

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

    //  Scene Transitions
    private void startGame() {
        clearAllEntities();
        createPlayer(Stage.TODDLER, STAGE1_THRESHOLD);
        sm.setScene(stage1Scene);
    }

    private void advanceToStage2() {
        clearFoodEntities();

        PlayerTag tag = player.getComponent(PlayerTag.class);
        if (tag != null) tag.setCurrentStage(Stage.TEEN);

        ScoreTracker tracker = player.getComponent(ScoreTracker.class);
        if (tracker != null) {
            tracker.reset();
            tracker.setThreshold(STAGE2_THRESHOLD);
        }

        if (foodHandler != null) foodHandler.clearLevelUp();

        transitionMessage = "You grew up to a Teen!";
        showingTransition = true;
        transitionTimer = TRANSITION_DURATION;

        sm.setScene(stage2Scene);
        sfxBubble.play();
    }

    private void advanceToStage3() {
        clearFoodEntities();

        PlayerTag tag = player.getComponent(PlayerTag.class);
        if (tag != null) tag.setCurrentStage(Stage.ADULT);

        ScoreTracker tracker = player.getComponent(ScoreTracker.class);
        if (tracker != null) {
            tracker.reset();
            tracker.setThreshold(STAGE3_THRESHOLD);
        }

        if (foodHandler != null) foodHandler.clearLevelUp();

        transitionMessage = "You grew up to an Adult!";
        showingTransition = true;
        transitionTimer = TRANSITION_DURATION;

        sm.setScene(stage3Scene);
        sfxBubble.play();
    }

    private void triggerVictory() {
        endScene.setVictory(true);
        sm.setScene(endScene);
        sfxBubble.play();
    }

    private void triggerGameOver() {
        endScene.setVictory(false);
        sm.setScene(endScene);
    }

    //  Entity Management
    private void createPlayer(Stage stage, int threshold) {
        player = em.createEntity();

        PlayerTag tag = new PlayerTag();
        tag.setCurrentStage(stage);
        player.add(tag);

        Sprite sprite = new Sprite("player", PLAYER_SIZE, PLAYER_SIZE);
        sprite.setColor(0.3f, 0.5f, 1f, 1f);
        player.add(sprite);

        player.add(new Collider(PLAYER_SIZE, PLAYER_SIZE, 0, 0, 0, false));
        player.add(new HealthBar(MAX_HEARTS));
        player.add(new ScoreTracker(threshold));

        foodHandler = new FoodCollectHandler();
        player.add(foodHandler);

        mm.register(player.getId(),
                new Transform(worldW / 2f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f),
                new Motion());
    }

    private void clearFoodEntities() {
        for (Entity e : em.getActiveEntities()) {
            if (e.hasComponent(FoodTag.class)) {
                mm.unregister(e.getId());
                em.destroyEntity(e.getId());
            }
        }
    }

    private void clearAllEntities() {
        for (Entity e : em.getActiveEntities()) {
            mm.unregister(e.getId());
        }
        em.clear();
        player = null;
    }

    //  Game Loop
    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        if (dt <= 0) return;

        Scene current = sm.getCurrentScene();

        // Menu screen
        if (current == menuScene) {
            if (showingGuide) {
                drawGuideScreen();
                handleGuideInput();
            } else {
                // Update button hover states
                startBtn.update();
                guideBtn.update();
                exitBtn.update();

                drawMenuScreen();

                // Handle button clicks
                if (startBtn.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    startGame();
                }
                if (guideBtn.isClicked()) {
                    showingGuide = true;
                    guidePage = 0;
                }
                if (exitBtn.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    Gdx.app.exit();
                }
            }
            return;
        }

        // End screen
        if (current == endScene) {
            restartBtn.update();
            menuBtn.update();

            drawEndScreen();

            if (restartBtn.isClicked()) {
                startGame();
            }
            if (menuBtn.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                sm.setScene(menuScene);
            }
            return;
        }

        // ESC during gameplay = back to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            clearAllEntities();
            sm.setScene(menuScene);
            return;
        }

        // Transition overlay (pause gameplay briefly)
        if (showingTransition) {
            transitionTimer -= dt;
            if (transitionTimer <= 0) {
                showingTransition = false;
            }
            drawGameplay();
            drawTransitionOverlay();
            return;
        }

        // Gameplay
        handleMovement();
        engine.update(dt);

        handleRespawns(dt);
        clampPlayerToScreen();
        wrapFood();

        checkGameState();
        drawGameplay();
    }

    //  Menu + Guide Input
    private void handleGuideInput() {
        // ENTER or click = next page
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            if (guidePage < 2) {
                guidePage++;
            } else {
                showingGuide = false;
            }
            return;
        }

        // ESC or Z = back
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (guidePage > 0) {
                guidePage--;
            } else {
                showingGuide = false;
            }
        }
    }

    //  Player Input
    private void handleMovement() {
        if (player == null) return;
        Motion pm = mm.getMotion(player.getId());
        if (pm == null) return;

        float vx = 0f, vy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    vy += PLAYER_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  vy -= PLAYER_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  vx -= PLAYER_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) vx += PLAYER_SPEED;

        pm.setVx(vx);
        pm.setVy(vy);
    }

    //  Game State
    private void checkGameState() {
        if (foodHandler == null) return;

        if (foodHandler.isGameOverTriggered()) {
            foodHandler.clearGameOver();
            triggerGameOver();
            return;
        }

        // Play sound based on last collision type
        if (foodHandler.wasLastCollectHealthy()) {
            sfxBite.play(0.5f);
            foodHandler.clearLastCollect();
        } else if (foodHandler.wasLastCollectUnhealthy()) {
            sfxSlurp.play(0.5f);
            foodHandler.clearLastCollect();
        }

        if (foodHandler.isLevelUpTriggered()) {
            PlayerTag tag = player.getComponent(PlayerTag.class);
            if (tag == null) return;

            switch (tag.getCurrentStage()) {
                case TODDLER: advanceToStage2(); break;
                case TEEN:    advanceToStage3(); break;
                case ADULT:   triggerVictory();  break;
            }
        }
    }

    //  Respawn + Boundary
    private void handleRespawns(float dt) {
        for (Entity e : em.getActiveEntities()) {
            FoodTag food = e.getComponent(FoodTag.class);
            if (food == null) continue;

            food.tick(dt);

            if (food.isReadyToRespawn()) {
                food.respawn();
                Sprite sprite = e.getComponent(Sprite.class);
                if (sprite != null) sprite.setVisible(true);

                Transform t = mm.getTransform(e.getId());
                if (t != null) {
                    float margin = 60f;
                    float size = 40f;
                    t.setPosition(
                            margin + rng.nextFloat() * (worldW - size - margin * 2),
                            margin + rng.nextFloat() * (worldH - size - margin * 2));
                }
            }
        }
    }

    private void clampPlayerToScreen() {
        if (player == null) return;
        Transform t = mm.getTransform(player.getId());
        if (t == null) return;
        if (t.getX() < 0) t.setX(0);
        if (t.getY() < 0) t.setY(0);
        if (t.getX() + PLAYER_SIZE > worldW) t.setX(worldW - PLAYER_SIZE);
        if (t.getY() + PLAYER_SIZE > worldH) t.setY(worldH - PLAYER_SIZE);
    }

    private void wrapFood() {
        float size = 40f;
        for (Entity e : em.getActiveEntities()) {
            if (!e.hasComponent(FoodTag.class)) continue;
            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            if (t.getX() < -size) t.setX(worldW);
            if (t.getX() > worldW) t.setX(-size);
            if (t.getY() < -size) t.setY(worldH);
            if (t.getY() > worldH) t.setY(-size);
        }
    }

    //  Rendering
    private void drawBackground(Scene current) {
        if (current != null) {
            current.render((r, g, b, a) -> Gdx.gl.glClearColor(r, g, b, a));
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background image on top of solid color
        Texture bg = null;
        if (current == stage1Scene && stage1Bg != null) bg = stage1Bg;
        else if (current == stage2Scene && stage2Bg != null) bg = stage2Bg;
        else if (current == stage3Scene && stage3Bg != null) bg = stage3Bg;
        else if (current == menuScene && menuBg != null) bg = menuBg;
        else if (current == endScene && endBg != null) bg = endBg;

        if (bg != null) {
            batch.begin();
            batch.draw(bg, 0, 0, worldW, worldH);
            batch.end();
        }
    }

    // Menu Screen

    private void drawMenuScreen() {
        drawBackground(menuScene);

        // menu buttons are invisible overlays on top of menu.png
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        startBtn.drawBackground(shapeRenderer);
        guideBtn.drawBackground(shapeRenderer);
        exitBtn.drawBackground(shapeRenderer);
        shapeRenderer.end();

        // to show debug button positions (red outlines):
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // shapeRenderer.setColor(Color.RED);
        // shapeRenderer.rect(startBtn.getX(), startBtn.getY(), startBtn.getWidth(), startBtn.getHeight());
        // shapeRenderer.rect(guideBtn.getX(), guideBtn.getY(), guideBtn.getWidth(), guideBtn.getHeight());
        // shapeRenderer.rect(exitBtn.getX(), exitBtn.getY(), exitBtn.getWidth(), exitBtn.getHeight());
        // shapeRenderer.end();
    }

    // Guide Screen

    private void drawGuideScreen() {
        drawBackground(menuScene);

        batch.begin();
        switch (guidePage) {
            case 0: if (guideTexture1 != null) batch.draw(guideTexture1, 0, 0, worldW, worldH); break;
            case 1: if (guideTexture2 != null) batch.draw(guideTexture2, 0, 0, worldW, worldH); break;
            case 2: if (guideTexture3 != null) batch.draw(guideTexture3, 0, 0, worldW, worldH); break;
        }
        batch.end();
    }

    // End Screen

    private void drawEndScreen() {
        drawBackground(endScene);

        batch.begin();
        if (endScene.isVictory()) {
            fontLarge.setColor(Color.GREEN);
            fontLarge.draw(batch, "Congratulations!", worldW / 2f - 150, worldH / 2f + 80);
            fontLarge.setColor(Color.WHITE);
            font.draw(batch, "You grew up healthy and strong!", worldW / 2f - 120, worldH / 2f + 30);
        } else {
            fontLarge.setColor(Color.RED);
            fontLarge.draw(batch, "Game Over!", worldW / 2f - 100, worldH / 2f + 80);
            fontLarge.setColor(Color.WHITE);
            font.draw(batch, "Too much unhealthy food...", worldW / 2f - 100, worldH / 2f + 30);
        }
        batch.end();

        // Draw end screen buttons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        restartBtn.drawBackground(shapeRenderer);
        menuBtn.drawBackground(shapeRenderer);
        shapeRenderer.end();

        batch.begin();
        restartBtn.drawWithBatch(batch, fontLarge);
        menuBtn.drawWithBatch(batch, fontLarge);
        batch.end();
    }

    // Transition Overlay

    private void drawTransitionOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.6f);
        shapeRenderer.rect(0, 0, worldW, worldH);
        shapeRenderer.end();

        batch.begin();
        fontLarge.setColor(Color.YELLOW);
        fontLarge.draw(batch, transitionMessage, worldW / 2f - 180, worldH / 2f + 20);
        fontLarge.setColor(Color.WHITE);
        batch.end();
    }

    // Gameplay

    private void drawGameplay() {
        Scene current = sm.getCurrentScene();
        drawBackground(current);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Player glow highlight
        if (player != null) {
            Transform pt = mm.getTransform(player.getId());
            if (pt != null) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(1f, 1f, 0.4f, 0.25f);
                float cx = pt.getX() + PLAYER_SIZE / 2f;
                float cy = pt.getY() + PLAYER_SIZE / 2f;
                shapeRenderer.circle(cx, cy, PLAYER_SIZE * 0.75f, 32);
                shapeRenderer.end();
            }
        }

        // Draw the entities with textures
        batch.begin();

        for (Entity e : em.getActiveEntities()) {
            Sprite sprite = e.getComponent(Sprite.class);
            if (sprite == null || !sprite.isVisible()) continue;

            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            // Player uses pre-loaded stage-specific texture
            PlayerTag tag = e.getComponent(PlayerTag.class);
            Texture tex = null;
            if (tag != null) {
                switch (tag.getCurrentStage()) {
                    case TODDLER: tex = toddlerTex; break;
                    case TEEN:    tex = teenTex;     break;
                    case ADULT:   tex = adultTex;    break;
                }
            } else {
                // Food uses lazy-loaded cached texture from file path
                tex = getTexture(sprite.getTextureName());
            }

            if (tex != null) {
                batch.draw(tex, t.getX(), t.getY(), sprite.getWidth(), sprite.getHeight());
            }
        }

        batch.end();

        // Fallback: draw shapes for entities without textures
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity e : em.getActiveEntities()) {
            Sprite sprite = e.getComponent(Sprite.class);
            if (sprite == null || !sprite.isVisible()) continue;

            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            // Check if texture exists — skip if already drawn above
            PlayerTag tag = e.getComponent(PlayerTag.class);
            Texture tex = null;
            if (tag != null) {
                switch (tag.getCurrentStage()) {
                    case TODDLER: tex = toddlerTex; break;
                    case TEEN:    tex = teenTex;     break;
                    case ADULT:   tex = adultTex;    break;
                }
            } else {
                tex = getTexture(sprite.getTextureName());
            }
            if (tex != null) continue;

            // Shape fallback
            shapeRenderer.setColor(sprite.getR(), sprite.getG(), sprite.getB(), sprite.getA());

            if (e.hasComponent(PlayerTag.class)) {
                float w = sprite.getWidth();
                float h = sprite.getHeight();
                shapeRenderer.triangle(t.getX(), t.getY(), t.getX() + w, t.getY(), t.getX() + w / 2f, t.getY() + h);
            } else if (e.hasComponent(FoodTag.class)) {
                float r = sprite.getWidth() / 2f;
                shapeRenderer.circle(t.getX() + r, t.getY() + r, r, 32);
            }
        }

        shapeRenderer.end();

        drawHUD();
    }

    // HUD

    private void drawHUD() {
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

    //  Cleanup
    @Override
    public void dispose() {
        if (engine != null) engine.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (fontLarge != null) fontLarge.dispose();

        // Food textures
        if (textureCache != null) {
            for (Texture t : textureCache.values()) {
                if (t != null) t.dispose();
            }
            textureCache.clear();
        }

        // Player textures
        if (toddlerTex != null) toddlerTex.dispose();
        if (teenTex != null) teenTex.dispose();
        if (adultTex != null) adultTex.dispose();

        // Background textures
        if (menuBg != null) menuBg.dispose();
        if (stage1Bg != null) stage1Bg.dispose();
        if (stage2Bg != null) stage2Bg.dispose();
        if (stage3Bg != null) stage3Bg.dispose();
        if (endBg != null) endBg.dispose();

        // Guide textures
        if (guideTexture1 != null) guideTexture1.dispose();
        if (guideTexture2 != null) guideTexture2.dispose();
        if (guideTexture3 != null) guideTexture3.dispose();

        // Audio
        if (bgm != null) bgm.dispose();
        if (sfxBite != null) sfxBite.dispose();
        if (sfxSlurp != null) sfxSlurp.dispose();
        if (sfxBubble != null) sfxBubble.dispose();
    }
}