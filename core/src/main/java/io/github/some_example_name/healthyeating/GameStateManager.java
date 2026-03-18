package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.factory.PlayerEntityFactory;
import io.github.some_example_name.factory.StageContentFactory;
import io.github.some_example_name.factory.StageFactoryRegistry;
import io.github.some_example_name.factory.StageProfile;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;
import io.github.some_example_name.scene.EndScene;
import io.github.some_example_name.scene.FoodStageScene;
import io.github.some_example_name.scene.MenuScene;
import io.github.some_example_name.scene.SceneManager;
import io.github.some_example_name.healthyeating.PlayerTag.Stage;

import java.util.Map;

/**
 * Manages game state, scene transitions, guide state, and transition overlays.
 * Uses StageFactoryRegistry for stage lookups instead of hardcoded stage logic.
 */
public class GameStateManager {

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
                            GameRenderer renderer, MenuScene menuScene, EndScene endScene,
                            Map<Stage, FoodStageScene> stageScenes) {
        this.sm = sm;
        this.em = em;
        this.mm = mm;
        this.playerFactory = playerFactory;
        this.stageRegistry = stageRegistry;
        this.renderer = renderer;
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

    /** Advances to the next stage based on the given target stage. */
    private void advanceToStage(Stage targetStage, String message, Sound sfxBubble) {
        playerFactory.clearFoodEntities(em, mm);

        StageProfile profile = stageRegistry.get(targetStage).getProfile();
        updatePlayersForStage(targetStage, profile.getScoreThreshold());
        resetPlayerPositions();

        transitionMessage = message;
        showingTransition = true;
        transitionTimer = TRANSITION_DURATION;

        sm.setScene(stageScenes.get(targetStage));
        sfxBubble.play();
    }

    public void advanceToStage2(Sound sfxBubble) {
        advanceToStage(Stage.TEEN, "You grew up to a Teen!", sfxBubble);
    }

    public void advanceToStage3(Sound sfxBubble) {
        advanceToStage(Stage.ADULT, "You grew up to an Adult!", sfxBubble);
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

    public void triggerVictory(Sound sfxBubble) {
        endScene.setVictory(true);
        sm.setScene(endScene);
        sfxBubble.play();
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

    public void checkGameState(Sound sfxBite, Sound sfxSlurp, Sound sfxBubble) {
        FoodCollectHandler handler1 = getHandler(player1);
        FoodCollectHandler handler2 = getHandler(player2);

        if ((handler1 != null && handler1.isGameOverTriggered()) ||
            (handler2 != null && handler2.isGameOverTriggered())) {
            if (handler1 != null) handler1.clearGameOver();
            if (handler2 != null) handler2.clearGameOver();
            triggerGameOver();
            return;
        }

        checkHandlerSounds(handler1, sfxBite, sfxSlurp, sfxBubble);
        checkHandlerSounds(handler2, sfxBite, sfxSlurp, sfxBubble);

        boolean levelUp = (handler1 != null && handler1.isLevelUpTriggered()) ||
                           (handler2 != null && handler2.isLevelUpTriggered());
        if (levelUp) {
            if (handler1 != null) handler1.clearLevelUp();
            if (handler2 != null) handler2.clearLevelUp();

            if (player1 == null) return;
            PlayerTag tag = player1.getComponent(PlayerTag.class);
            if (tag == null) return;

            switch (tag.getCurrentStage()) {
                case TODDLER: advanceToStage2(sfxBubble); break;
                case TEEN:    advanceToStage3(sfxBubble); break;
                case ADULT:   triggerVictory(sfxBubble);  break;
            }
        }
    }

    private void checkHandlerSounds(FoodCollectHandler handler,
                                    Sound sfxBite, Sound sfxSlurp, Sound sfxBubble) {
        if (handler == null) return;
        if (handler.wasLastCollectHealthy()) {
            sfxBite.play(0.5f);
            handler.clearLastCollect();
        } else if (handler.wasLastCollectMedicine()) {
            sfxBubble.play(0.5f);
            renderer.triggerGreenFlash();
            handler.clearLastCollect();
        } else if (handler.wasLastCollectOlder()) {
            sfxSlurp.play(0.7f);
            renderer.triggerPurpleFlash();
            handler.clearLastCollect();
        } else if (handler.wasLastCollectUnhealthy()) {
            sfxSlurp.play(0.5f);
            renderer.triggerRedFlash();
            handler.clearLastCollect();
        }
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
