package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.healthyeating.factory.PlayerEntityFactory;
import io.github.some_example_name.healthyeating.factory.StageContentFactory;
import io.github.some_example_name.healthyeating.factory.StageFactoryRegistry;
import io.github.some_example_name.healthyeating.factory.StageProfile;
import io.github.some_example_name.inputoutput.output.AudioManager;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.scene.SceneManager;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

import java.util.Map;

/**
 * Manages game state, scene transitions, guide state, and transition overlays.
 * Uses StageFactoryRegistry for data-driven stage progression.
 * Uses engine's AudioManager for all sound playback.
 */
public class GameStateManager {

    // Audio IDs (registered with engine AudioManager)
    public static final String SFX_BITE   = "bite";
    public static final String SFX_SLURP  = "slurp";
    public static final String SFX_BUBBLE = "bubble";

    private static final float TRANSITION_DURATION = 3.0f;

    // Scene references
    private final MenuScene menuScene;
    private final EndScene endScene;
    private final Map<Stage, FoodStageScene> stageScenes;
    private final SceneManager sm;

    // Factories
    private final PlayerEntityFactory playerFactory;
    private final StageFactoryRegistry stageRegistry;
    private final GameRenderer renderer;

    // Engine references
    private final EntityManager em;
    private final MovementManager mm;
    private final AudioManager audioManager;

    // Players
    private Entity player1;
    private Entity player2;
    private boolean multiplayer = false;

    // Transition overlay
    private boolean showingTransition = false;
    private float transitionTimer = 0f;
    private String transitionMessage = "";

    // Guide state
    private boolean showingGuide = false;
    private int guidePage = 0;

    public GameStateManager(SceneManager sm, EntityManager em, MovementManager mm,
                            PlayerEntityFactory playerFactory, StageFactoryRegistry stageRegistry,
                            GameRenderer renderer, AudioManager audioManager,
                            MenuScene menuScene, EndScene endScene,
                            Map<Stage, FoodStageScene> stageScenes) {
        this.sm = sm;
        this.em = em;
        this.mm = mm;
        this.playerFactory = playerFactory;
        this.stageRegistry = stageRegistry;
        this.renderer = renderer;
        this.audioManager = audioManager;
        this.menuScene = menuScene;
        this.endScene = endScene;
        this.stageScenes = stageScenes;
    }

    // ─── Scene Transitions ───

    public void startGame(boolean isMultiplayer) {
        this.multiplayer = isMultiplayer;
        playerFactory.clearAllEntities(em, mm);

        StageProfile profile = stageRegistry.get(Stage.TODDLER).getProfile();
        Entity[] players;
        if (multiplayer) {
            players = playerFactory.createPlayers(em, mm, Stage.TODDLER,
                    profile.getScoreThreshold(), GameRenderer.WORLD_W, GameRenderer.WORLD_H);
        } else {
            players = playerFactory.createSinglePlayer(em, mm, Stage.TODDLER,
                    profile.getScoreThreshold(), GameRenderer.WORLD_W, GameRenderer.WORLD_H);
        }
        player1 = players[0];
        player2 = players[1];
        sm.setScene(stageScenes.get(Stage.TODDLER));
    }

    public boolean isMultiplayer() { return multiplayer; }

    private void advanceToNextStage() {
        if (player1 == null) return;
        PlayerTag tag = player1.getComponent(PlayerTag.class);
        if (tag == null) return;

        StageProfile currentProfile = stageRegistry.get(tag.getCurrentStage()).getProfile();
        Stage nextStage = currentProfile.getNextStage();

        if (nextStage == null) {
            triggerVictory();
            return;
        }

        playerFactory.clearFoodEntities(em, mm);

        StageProfile nextProfile = stageRegistry.get(nextStage).getProfile();
        updatePlayersForStage(nextStage, nextProfile.getScoreThreshold());
        resetPlayerPositions();

        transitionMessage = currentProfile.getTransitionMessage();
        showingTransition = true;
        transitionTimer = TRANSITION_DURATION;

        sm.setScene(stageScenes.get(nextStage));
        audioManager.playAudio(SFX_BUBBLE);
    }

    private void resetPlayerPositions() {
        float worldW = GameRenderer.WORLD_W;
        float worldH = GameRenderer.WORLD_H;
        float size = PlayerEntityFactory.PLAYER_SIZE;
        if (multiplayer) {
            resetPosition(player1, worldW / 3f - size / 2f, worldH / 2f - size / 2f);
            resetPosition(player2, worldW * 2f / 3f - size / 2f, worldH / 2f - size / 2f);
        } else {
            resetPosition(player1, worldW / 2f - size / 2f, worldH / 2f - size / 2f);
        }
    }

    private void resetPosition(Entity player, float x, float y) {
        if (player == null) return;
        Transform t = mm.getTransform(player.getId());
        if (t != null) t.setPosition(x, y);
    }

    private void updatePlayersForStage(Stage stage, int threshold) {
        updatePlayerStage(player1, stage, threshold);
        updatePlayerStage(player2, stage, threshold);
    }

    private void updatePlayerStage(Entity player, Stage stage, int threshold) {
        if (player == null) return;
        PlayerTag tag = player.getComponent(PlayerTag.class);
        if (tag != null) tag.setCurrentStage(stage);

        ScoreTracker tracker = player.getComponent(ScoreTracker.class);
        if (tracker != null) {
            tracker.reset();
            tracker.setThreshold(threshold);
        }

        FoodCollectHandler handler = player.getComponent(FoodCollectHandler.class);
        if (handler != null) handler.clearLevelUp();
    }

    public void triggerVictory() {
        endScene.setVictory(true);
        sm.setScene(endScene);
        audioManager.playAudio(SFX_BUBBLE);
    }

    public void triggerGameOver() {
        endScene.setVictory(false);
        sm.setScene(endScene);
    }

    public void returnToMenu() {
        playerFactory.clearAllEntities(em, mm);
        player1 = null;
        player2 = null;
        sm.setScene(menuScene);
    }

    // ─── Game State Checks ───

    public void checkGameState() {
        FoodCollectHandler handler1 = getHandler(player1);
        FoodCollectHandler handler2 = getHandler(player2);

        if ((handler1 != null && handler1.isGameOverTriggered()) ||
            (handler2 != null && handler2.isGameOverTriggered())) {
            if (handler1 != null) handler1.clearGameOver();
            if (handler2 != null) handler2.clearGameOver();
            triggerGameOver();
            return;
        }

        checkHandlerEffects(handler1);
        checkHandlerEffects(handler2);

        boolean levelUp = (handler1 != null && handler1.isLevelUpTriggered()) ||
                           (handler2 != null && handler2.isLevelUpTriggered());
        if (levelUp) {
            if (handler1 != null) handler1.clearLevelUp();
            if (handler2 != null) handler2.clearLevelUp();
            advanceToNextStage();
        }
    }

    private void checkHandlerEffects(FoodCollectHandler handler) {
        if (handler == null || !handler.hasCollected()) return;

        switch (handler.getLastFlashType()) {
            case NONE:
                audioManager.playAudio(SFX_BITE);
                break;
            case RED:
                audioManager.playAudio(SFX_SLURP);
                renderer.triggerRedFlash();
                break;
            case GREEN:
                audioManager.playAudio(SFX_BUBBLE);
                renderer.triggerGreenFlash();
                break;
            case PURPLE:
                audioManager.playAudio(SFX_SLURP);
                renderer.triggerPurpleFlash();
                break;
        }
        handler.clearLastCollect();
    }

    private FoodCollectHandler getHandler(Entity player) {
        if (player == null) return null;
        return player.getComponent(FoodCollectHandler.class);
    }

    // ─── Transition Overlay ───

    public boolean isShowingTransition() { return showingTransition; }
    public String getTransitionMessage() { return transitionMessage; }

    public int getCountdown() {
        return (int) Math.ceil(transitionTimer);
    }

    public void updateTransition(float dt) {
        transitionTimer -= dt;
        if (transitionTimer <= 0) {
            showingTransition = false;
        }
    }

    // ─── Guide State ───

    public boolean isShowingGuide() { return showingGuide; }
    public int getGuidePage() { return guidePage; }

    public void openGuide() {
        showingGuide = true;
        guidePage = 0;
    }

    public void handleGuideInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            if (guidePage < 2) {
                guidePage++;
            } else {
                showingGuide = false;
            }
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (guidePage > 0) {
                guidePage--;
            } else {
                showingGuide = false;
            }
        }
    }

    // ─── Getters ───

    public Entity getPlayer1() { return player1; }
    public Entity getPlayer2() { return player2; }
    public MenuScene getMenuScene() { return menuScene; }
    public EndScene getEndScene() { return endScene; }
}
