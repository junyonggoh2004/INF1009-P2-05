package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.EnumMap;
import java.util.Map;

import io.github.some_example_name.core.EngineCore;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.inputoutput.input.InputHandler;
import io.github.some_example_name.inputoutput.output.AudioManager;
import io.github.some_example_name.inputoutput.output.ConsoleLogger;
import io.github.some_example_name.healthyeating.factory.FoodEntityFactory;
import io.github.some_example_name.healthyeating.factory.PlayerEntityFactory;
import io.github.some_example_name.healthyeating.factory.StageContentFactory;
import io.github.some_example_name.healthyeating.factory.StageFactoryRegistry;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.scene.Scene;
import io.github.some_example_name.scene.SceneManager;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

/**
 * Healthy Eating Game — thin coordinator (Facade pattern).
 * Delegates to GameRenderer, GameStateManager, GameInputHandler,
 * GameWorldManager, and factory classes.
 */
public class HealthyEatingGame extends ApplicationAdapter {

    // Engine
    private EngineCore engine;
    private EntityManager em;
    private MovementManager mm;
    private SceneManager sm;

    // Managers
    private GameRenderer renderer;
    private GameStateManager stateManager;
    private GameInputHandler gameInput;
    private GameWorldManager worldManager;
    private InputHandler engineInput;
    private AudioManager audioManager;
    private ConsoleLogger logger;

    // Factories
    private PlayerEntityFactory playerFactory;
    private FoodEntityFactory foodFactory;
    private StageFactoryRegistry stageRegistry;

    // Buttons
    private Button singlePlayerBtn, multiPlayerBtn, guideBtn, exitBtn;
    private Button settingsBtn, restartBtn, menuBtn, settingsBackBtn;
    private Button pauseResumeBtn, pauseSettingsBtn, pauseQuitBtn;

    // Scenes
    private MenuScene menuScene;
    private SettingsScene settingsScene;
    private EndScene endScene;
    private Map<Stage, FoodStageScene> stageScenes;

    // Settings values
    private float bgmVolume = 0.4f;
    private float sfxVolume = 1.0f;
    private float sliderX, sliderW, bgmSliderY, sfxSliderY;
    private int activeSlider = SLIDER_NONE;
    private final Vector2 pointerWorld = new Vector2();

    private static final String[] SFX_IDS = {
            GameStateManager.SFX_BITE,
            GameStateManager.SFX_SLURP,
            GameStateManager.SFX_BUBBLE,
            GameStateManager.SFX_LEVEL_UP,
            GameStateManager.SFX_GAME_OVER,
            GameStateManager.SFX_GAME_COMPLETE
    };

    private static final int SLIDER_NONE = 0;
    private static final int SLIDER_BGM = 1;
    private static final int SLIDER_SFX = 2;
    private static final int PAUSE_NONE = 0;
    private static final int PAUSE_MENU = 1;
    private static final int PAUSE_SETTINGS = 2;

    private int pauseState = PAUSE_NONE;

    @Override
    public void create() {
        // Engine
        engine = new EngineCore();
        engine.init();
        em = engine.getEntityManager();
        mm = engine.getMovementManager();
        sm = engine.getSceneManager();

        float worldW = GameRenderer.WORLD_W;
        float worldH = GameRenderer.WORLD_H;

        // Factories
        playerFactory = new PlayerEntityFactory();
        foodFactory = new FoodEntityFactory();
        stageRegistry = new StageFactoryRegistry();

        // Build stage scenes from registry (Abstract Factory -> reusable scene)
        stageScenes = new EnumMap<>(Stage.class);
        for (StageContentFactory factory : stageRegistry.all()) {
            Stage stage = factory.getProfile().getStage();
            stageScenes.put(stage, new FoodStageScene(factory, foodFactory, em, mm, worldW, worldH));
        }

        // Non-gameplay scenes
        menuScene = new MenuScene();
        settingsScene = new SettingsScene();
        endScene  = new EndScene();
        sm.setScene(menuScene);

        // Input: bind game actions to the engine's input system
        engineInput = engine.getIOManager().input();
        GameInputHandler.bindActions(engineInput);

        // Audio: load through engine's AudioManager
        audioManager = engine.getIOManager().output().getAudioManager();
        logger = engine.getIOManager().output().getLogger();
        audioManager.loadMusic("bgm", FoodAssets.BGM_1);
        audioManager.loadSound(GameStateManager.SFX_BITE, FoodAssets.SFX_BITE);
        audioManager.loadSound(GameStateManager.SFX_SLURP, FoodAssets.SFX_SLURP);
        audioManager.loadSound(GameStateManager.SFX_BUBBLE, FoodAssets.SFX_BUBBLE);
        audioManager.loadSound(GameStateManager.SFX_LEVEL_UP, FoodAssets.SFX_LEVEL_UP);
        audioManager.loadSound(GameStateManager.SFX_GAME_OVER, FoodAssets.SFX_GAME_OVER);
        audioManager.loadSound(GameStateManager.SFX_GAME_COMPLETE, FoodAssets.SFX_GAME_COMPLETE);

        // Start background music
        applyBgmVolume();
        audioManager.getAudio("bgm").setLooping(true);
        audioManager.playAudio("bgm");
        applySfxVolume();

        // Managers
        renderer = new GameRenderer(menuScene, endScene, stageScenes);
        gameInput = new GameInputHandler();
        worldManager = new GameWorldManager();
        stateManager = new GameStateManager(sm, em, mm, playerFactory, stageRegistry,
                renderer, audioManager, logger, menuScene, endScene, stageScenes);

        // Menu buttons — centered vertically below the title
        float menuBtnW = 200f;
        float menuBtnH = 45f;
        float menuBtnX = worldW / 2f - menuBtnW / 2f;
        float menuBtnSpacing = 12f;
        float menuTopY = worldH * 0.54f;

        singlePlayerBtn = new Button(
                menuBtnX, menuTopY,
                menuBtnW, menuBtnH,
                "1 Player", new Color(0.2f, 0.6f, 0.3f, 0.9f), new Color(0.3f, 0.8f, 0.4f, 1f));

        multiPlayerBtn = new Button(
                menuBtnX, menuTopY - (menuBtnH + menuBtnSpacing),
                menuBtnW, menuBtnH,
                "2 Players", new Color(0.2f, 0.4f, 0.8f, 0.9f), new Color(0.3f, 0.5f, 1f, 1f));

        guideBtn = new Button(
                menuBtnX, menuTopY - 2 * (menuBtnH + menuBtnSpacing),
                menuBtnW, menuBtnH,
                "Guide", new Color(0.7f, 0.5f, 0.2f, 0.9f), new Color(0.9f, 0.65f, 0.3f, 1f));

        settingsBtn = new Button(
                menuBtnX, menuTopY - 3 * (menuBtnH + menuBtnSpacing),
                menuBtnW, menuBtnH,
                "Settings", new Color(0.8f, 0.7f, 0.15f, 0.95f), new Color(0.95f, 0.85f, 0.25f, 1f));

        exitBtn = new Button(
                menuBtnX, menuTopY - 4 * (menuBtnH + menuBtnSpacing),
                menuBtnW, menuBtnH,
                "Exit", new Color(0.7f, 0.2f, 0.2f, 0.9f), new Color(0.9f, 0.3f, 0.3f, 1f));

        sliderW = 300f;
        sliderX = worldW / 2f - sliderW / 2f;
        bgmSliderY = worldH * 0.55f;
        sfxSliderY = worldH * 0.40f;

        settingsBackBtn = new Button(
                worldW / 2f - 90f, worldH * 0.2f,
                180f, 45f,
                "Back", new Color(0.2f, 0.5f, 0.9f, 0.9f), new Color(0.3f, 0.6f, 1f, 1f));

        float pauseBtnW = 200f;
        float pauseBtnH = 45f;
        float pauseBtnX = worldW / 2f - pauseBtnW / 2f;
        float pauseTopY = worldH * 0.53f;
        float pauseGap = 12f;

        pauseResumeBtn = new Button(
                pauseBtnX, pauseTopY,
                pauseBtnW, pauseBtnH,
                "Resume", new Color(0.2f, 0.65f, 0.3f, 0.95f), new Color(0.3f, 0.85f, 0.4f, 1f));

        pauseSettingsBtn = new Button(
                pauseBtnX, pauseTopY - (pauseBtnH + pauseGap),
                pauseBtnW, pauseBtnH,
                "Settings", new Color(0.8f, 0.7f, 0.15f, 0.95f), new Color(0.95f, 0.85f, 0.25f, 1f));

        pauseQuitBtn = new Button(
                pauseBtnX, pauseTopY - 2 * (pauseBtnH + pauseGap),
                pauseBtnW, pauseBtnH,
                "Quit", new Color(0.7f, 0.2f, 0.2f, 0.95f), new Color(0.9f, 0.3f, 0.3f, 1f));

        float endBtnW = 200f;
        float endBtnH = 50f;
        float endBtnY = worldH * 0.12f;

        restartBtn = new Button(
                worldW / 2f - endBtnW - 20f, endBtnY,
                endBtnW, endBtnH,
                "Play Again", new Color(0.2f, 0.75f, 0.3f, 0.9f), new Color(0.3f, 0.9f, 0.4f, 1f));

        menuBtn = new Button(
                worldW / 2f + 20f, endBtnY,
                endBtnW, endBtnH,
                "Main Menu", new Color(0.2f, 0.5f, 0.9f, 0.9f), new Color(0.3f, 0.6f, 1f, 1f));
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        if (dt <= 0) return;

        Viewport viewport = renderer.getViewport();
        Scene current = sm.getCurrentScene();
        float worldW = GameRenderer.WORLD_W;
        float worldH = GameRenderer.WORLD_H;

        // Menu screen
        if (current == menuScene) {
            if (stateManager.isShowingGuide()) {
                renderer.drawGuideScreen(stateManager.getGuidePage());
                stateManager.handleGuideInput();
            } else {
                singlePlayerBtn.update(viewport);
                multiPlayerBtn.update(viewport);
                guideBtn.update(viewport);
                settingsBtn.update(viewport);
                exitBtn.update(viewport);
                renderer.drawMenuScreen(singlePlayerBtn, multiPlayerBtn, guideBtn, settingsBtn, exitBtn);

                if (singlePlayerBtn.isClicked()) {
                    if (logger != null) logger.log("[HealthyEating] Start game: 1 PLAYER");
                    pauseState = PAUSE_NONE;
                    stateManager.startGame(false);
                }
                if (multiPlayerBtn.isClicked()) {
                    if (logger != null) logger.log("[HealthyEating] Start game: 2 PLAYERS");
                    pauseState = PAUSE_NONE;
                    stateManager.startGame(true);
                }
                if (guideBtn.isClicked()) {
                    stateManager.openGuide();
                }
                if (settingsBtn.isClicked()) {
                    sm.setScene(settingsScene);
                }
                if (exitBtn.isClicked()) {
                    if (logger != null) logger.log("[HealthyEating] Closing the game");
                    Gdx.app.exit();
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    Gdx.app.exit();
                }
            }
            return;
        }

        if (current == settingsScene) {
            settingsBackBtn.update(viewport);
            updateSettingsSliders(viewport);
            renderer.drawSettingsScreen(settingsBackBtn, bgmVolume, sfxVolume, sliderX, sliderW, bgmSliderY, sfxSliderY);

            if (settingsBackBtn.isClicked()) {
                activeSlider = SLIDER_NONE;
                sm.setScene(menuScene);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                activeSlider = SLIDER_NONE;
                sm.setScene(menuScene);
            }
            return;
        }

        // End screen
        if (current == endScene) {
            restartBtn.update(viewport);
            menuBtn.update(viewport);
            renderer.drawEndScreen(endScene, restartBtn, menuBtn);

            if (restartBtn.isClicked()) {
                if (logger != null) {
                    if (endScene.isVictory()) {
                        logger.log("[HealthyEating] Play again clicked after WIN");
                    } else {
                        logger.log("[HealthyEating] Play again clicked after LOSE");
                    }
                }
                pauseState = PAUSE_NONE;
                stateManager.startGame(stateManager.isMultiplayer());
            }
            if (menuBtn.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                pauseState = PAUSE_NONE;
                stateManager.returnToMenu();
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !stateManager.isShowingTransition()) {
            if (pauseState == PAUSE_NONE) {
                pauseState = PAUSE_MENU;
                activeSlider = SLIDER_NONE;
            } else if (pauseState == PAUSE_MENU) {
                pauseState = PAUSE_NONE;
            } else {
                pauseState = PAUSE_MENU;
                activeSlider = SLIDER_NONE;
            }
        }

        if (pauseState != PAUSE_NONE) {
            renderer.drawGameplay(current, em, mm, stateManager.getPlayer1(), stateManager.getPlayer2());

            if (pauseState == PAUSE_MENU) {
                pauseResumeBtn.update(viewport);
                pauseSettingsBtn.update(viewport);
                pauseQuitBtn.update(viewport);
                renderer.drawPauseMenuOverlay(pauseResumeBtn, pauseSettingsBtn, pauseQuitBtn);

                if (pauseResumeBtn.isClicked()) {
                    pauseState = PAUSE_NONE;
                }
                if (pauseSettingsBtn.isClicked()) {
                    pauseState = PAUSE_SETTINGS;
                    activeSlider = SLIDER_NONE;
                }
                if (pauseQuitBtn.isClicked()) {
                    if (logger != null) logger.log("[HealthyEating] Quitting to Main Menu");
                    pauseState = PAUSE_NONE;
                    activeSlider = SLIDER_NONE;
                    stateManager.returnToMenu();
                }
            } else {
                settingsBackBtn.update(viewport);
                updateSettingsSliders(viewport);
                renderer.drawSettingsOverlayOnGameplay(settingsBackBtn, bgmVolume, sfxVolume,
                        sliderX, sliderW, bgmSliderY, sfxSliderY);

                if (settingsBackBtn.isClicked()) {
                    pauseState = PAUSE_MENU;
                    activeSlider = SLIDER_NONE;
                }
            }
            return;
        }

        if (stateManager.isShowingTransition()) {
            stateManager.updateTransition(dt);
            renderer.drawGameplay(current, em, mm, stateManager.getPlayer1(), stateManager.getPlayer2());
            renderer.drawTransitionOverlay(stateManager.getTransitionMessage(), stateManager.getCountdown());
            return;
        }

        // Gameplay
        updatePlayerExpressions(dt);
        gameInput.handleMovement(stateManager.getPlayer1(), stateManager.getPlayer2(), mm, engineInput);
        engine.update(dt);

        worldManager.handleRespawns(dt, em, mm, worldW, worldH);
        worldManager.clampPlayerToScreen(stateManager.getPlayer1(), mm, worldW, worldH);
        worldManager.clampPlayerToScreen(stateManager.getPlayer2(), mm, worldW, worldH);
        worldManager.wrapFood(em, mm, worldW, worldH);

        stateManager.checkGameState();
        renderer.updateFlash(dt);
        renderer.drawGameplay(current, em, mm, stateManager.getPlayer1(), stateManager.getPlayer2());
    }

    private void updatePlayerExpressions(float dt) {
        Entity p1 = stateManager.getPlayer1();
        Entity p2 = stateManager.getPlayer2();
        if (p1 != null) {
            PlayerTag t1 = p1.getComponent(PlayerTag.class);
            if (t1 != null) t1.updateExpression(dt);
        }
        if (p2 != null) {
            PlayerTag t2 = p2.getComponent(PlayerTag.class);
            if (t2 != null) t2.updateExpression(dt);
        }
    }

    private void updateSettingsSliders(Viewport viewport) {
        if (!Gdx.input.isTouched()) {
            activeSlider = SLIDER_NONE;
            return;
        }

        pointerWorld.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(pointerWorld);

        if (Gdx.input.justTouched()) {
            if (isOnSlider(pointerWorld, bgmSliderY)) {
                activeSlider = SLIDER_BGM;
            } else if (isOnSlider(pointerWorld, sfxSliderY)) {
                activeSlider = SLIDER_SFX;
            } else {
                activeSlider = SLIDER_NONE;
            }
        }

        if (activeSlider == SLIDER_NONE) return;

        float normalized = Math.max(0f, Math.min(1f, (pointerWorld.x - sliderX) / sliderW));
        if (activeSlider == SLIDER_BGM) {
            bgmVolume = normalized;
            applyBgmVolume();
        } else if (activeSlider == SLIDER_SFX) {
            sfxVolume = normalized;
            applySfxVolume();
        }
    }

    private boolean isOnSlider(Vector2 point, float sliderY) {
        float hitPadding = 20f;
        return point.x >= sliderX && point.x <= sliderX + sliderW
                && point.y >= sliderY - hitPadding && point.y <= sliderY + 10f + hitPadding;
    }

    private void applyBgmVolume() {
        if (audioManager == null) return;
        if (audioManager.getAudio("bgm") != null) {
            audioManager.getAudio("bgm").setVolume(bgmVolume);
        }
    }

    private void applySfxVolume() {
        if (audioManager == null) return;
        for (String sfxId : SFX_IDS) {
            if (audioManager.getAudio(sfxId) != null) {
                audioManager.getAudio(sfxId).setVolume(sfxVolume);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void dispose() {
        if (engine != null) engine.dispose();
        if (renderer != null) renderer.dispose();
    }
}
