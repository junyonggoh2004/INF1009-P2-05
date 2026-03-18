package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.EnumMap;
import java.util.Map;

import io.github.some_example_name.core.EngineCore;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.inputoutput.input.InputHandler;
import io.github.some_example_name.inputoutput.output.AudioManager;
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

    // Factories
    private PlayerEntityFactory playerFactory;
    private FoodEntityFactory foodFactory;
    private StageFactoryRegistry stageRegistry;

    // Buttons
    private Button singlePlayerBtn, multiPlayerBtn, guideBtn, exitBtn;
    private Button restartBtn, menuBtn;

    // Scenes
    private MenuScene menuScene;
    private EndScene endScene;
    private Map<Stage, FoodStageScene> stageScenes;

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
        endScene  = new EndScene();
        sm.setScene(menuScene);

        // Input: bind game actions to the engine's input system
        engineInput = engine.getIOManager().input();
        GameInputHandler.bindActions(engineInput);

        // Audio: load through engine's AudioManager
        audioManager = engine.getIOManager().output().getAudioManager();
        audioManager.loadMusic("bgm", FoodAssets.BGM_1);
        audioManager.loadSound(GameStateManager.SFX_BITE, FoodAssets.SFX_BITE);
        audioManager.loadSound(GameStateManager.SFX_SLURP, FoodAssets.SFX_SLURP);
        audioManager.loadSound(GameStateManager.SFX_BUBBLE, FoodAssets.SFX_BUBBLE);

        // Start background music
        audioManager.getAudio("bgm").setVolume(0.4f);
        audioManager.getAudio("bgm").setLooping(true);
        audioManager.playAudio("bgm");

        // Managers
        renderer = new GameRenderer(menuScene, endScene, stageScenes);
        gameInput = new GameInputHandler();
        worldManager = new GameWorldManager();
        stateManager = new GameStateManager(sm, em, mm, playerFactory, stageRegistry,
                renderer, audioManager, menuScene, endScene, stageScenes);

        // Menu buttons — centered vertically below the title
        float menuBtnW = 200f;
        float menuBtnH = 45f;
        float menuBtnX = worldW / 2f - menuBtnW / 2f;
        float menuBtnSpacing = 12f;
        float menuTopY = worldH * 0.48f;

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

        exitBtn = new Button(
                menuBtnX, menuTopY - 3 * (menuBtnH + menuBtnSpacing),
                menuBtnW, menuBtnH,
                "Exit", new Color(0.7f, 0.2f, 0.2f, 0.9f), new Color(0.9f, 0.3f, 0.3f, 1f));

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
                exitBtn.update(viewport);
                renderer.drawMenuScreen(singlePlayerBtn, multiPlayerBtn, guideBtn, exitBtn);

                if (singlePlayerBtn.isClicked()) {
                    stateManager.startGame(false);
                }
                if (multiPlayerBtn.isClicked()) {
                    stateManager.startGame(true);
                }
                if (guideBtn.isClicked()) {
                    stateManager.openGuide();
                }
                if (exitBtn.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    Gdx.app.exit();
                }
            }
            return;
        }

        // End screen
        if (current == endScene) {
            restartBtn.update(viewport);
            menuBtn.update(viewport);
            renderer.drawEndScreen(endScene, restartBtn, menuBtn);

            if (restartBtn.isClicked()) {
                stateManager.startGame(stateManager.isMultiplayer());
            }
            if (menuBtn.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                stateManager.returnToMenu();
            }
            return;
        }

        // ESC during gameplay
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stateManager.returnToMenu();
            return;
        }

        // Transition overlay
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
