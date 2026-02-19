package io.github.some_example_name.prototype;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
import io.github.some_example_name.scene.MovingScene;
import io.github.some_example_name.scene.Scene;
import io.github.some_example_name.scene.SceneManager;
import io.github.some_example_name.scene.StaticScene;
import io.github.some_example_name.inputoutput.output.AudioManager;
import io.github.some_example_name.inputoutput.output.GameAudio;
import io.github.some_example_name.inputoutput.output.MusicTrack;

/**
 * Engine prototype demonstrating the abstract Entity/Component system.
 *
 * This is NOT a specific game — it showcases engine capabilities:
 * - Entity creation/destruction at runtime
 * - Component add/remove on the fly
 * - Scene switching via SceneManager (with lifecycle hooks)
 * - Debug overlay showing engine internals
 * - Collision detection and handler notification
 */
public class PrototypeEngine extends ApplicationAdapter {

    // ─── Engine ───
    private EngineCore engine;
    private EntityManager em;
    private MovementManager mm;
    private CollisionManager cm;
    private SceneManager sm;

    // ─── Rendering ───
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontSmall;
    private Scene.Renderer sceneRenderer;

    // ─── Config ───
    private static final float CIRCLE_SIZE = 20f;
    private static final float PLAYER_SIZE = 30f;
    private static final float PLAYER_SPEED = 250f;
    private static final float RESPAWN_DELAY = 3.0f;

    // ─── State ───
    private Entity player;
    private CollectHandler collectHandler;
    private final Random rng = new Random();
    private float worldW, worldH;

    // ─── Debug overlay ───
    private boolean debugMode = false;

    // ─── Scenes ───
    private StaticScene staticScene;
    private MovingScene movingScene;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        fontSmall = new BitmapFont();
        fontSmall.setColor(Color.LIGHT_GRAY);
        fontSmall.getData().setScale(0.85f);

        worldW = Gdx.graphics.getWidth();
        worldH = Gdx.graphics.getHeight();

        // ─── Init engine ───
        engine = new EngineCore();
        engine.init();

        // Load Sound Effects
        AudioManager am = engine.getIOManager().output().getAudioManager();
        am.loadSound("bubble_click", "Bubble-Click.ogg");

        // Load Background Music
        am.loadMusic("bgm_1", "BGM_1.mp3");
        am.loadMusic("bgm_2", "BGM_2.mp3");

        // Setup Scene 1 Music (Volume and Looping)
        GameAudio track1 = am.getAudio("bgm_1");
        if (track1 != null) {
            track1.setVolume(0.2f);
            if (track1 instanceof MusicTrack) {
                ((MusicTrack) track1).setLooping(true);
            }
        }

        // Setup Scene 2 Music (Volume and Looping)
        GameAudio track2 = am.getAudio("bgm_2");
        if (track2 != null) {
            track2.setVolume(0.2f);
            if (track2 instanceof MusicTrack) {
                ((MusicTrack) track2).setLooping(true);
            }
        }

        em = engine.getEntityManager();
        mm = engine.getMovementManager();
        cm = engine.getCollisionManager();
        sm = engine.getSceneManager();
        sceneRenderer = new GdxSceneRenderer();

        // ─── Create scenes ───
        staticScene = new StaticScene(em, mm, worldW, worldH);
        movingScene = new MovingScene(em, mm, worldW, worldH);

        // Player persists and is not scene-owned.
        createPlayer();

        // ─── Load first scene via SceneManager ───
        loadScene(staticScene);
        // Play BGM_1
        am.playAudio("bgm_1");
    }

    // ═══════════════════════════════════════════
    //  Scene Management (delegates to SceneManager)
    // ═══════════════════════════════════════════

    /**
     * Switches to a new scene via SceneManager.
     * SceneManager handles lifecycle: exit old -> unload old -> load new -> enter new.
     */
    private void loadScene(Scene scene) {
        // SceneManager handles lifecycle hooks for scene-owned entities.
        sm.setScene(scene);
    }

    // ═══════════════════════════════════════════
    //  Entity Creation
    // ═══════════════════════════════════════════

    private void createPlayer() {
        player = em.createEntity();
        player.add(new PlayerTag());

        Sprite sprite = new Sprite("triangle", PLAYER_SIZE, PLAYER_SIZE);
        sprite.setColor(0.2f, 0.9f, 0.3f, 1f);
        player.add(sprite);

        player.add(new Collider(PLAYER_SIZE, PLAYER_SIZE, 0, 0, 0, false));

        AudioManager am = engine.getIOManager().output().getAudioManager();
        collectHandler = new CollectHandler(am);
        player.add(collectHandler);

        mm.register(player.getId(),
                new Transform(worldW / 2f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f),
                new Motion());
    }

    // ═══════════════════════════════════════════
    //  Game Loop
    // ═══════════════════════════════════════════

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        if (dt <= 0) return;

        handleMovement();
        handleEngineControls();

        engine.update(dt);

        handleRespawns(dt);
        clampPlayerToScreen();

        draw();
    }

    // ═══════════════════════════════════════════
    //  Input
    // ═══════════════════════════════════════════

    private void handleMovement() {
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

    /**
     * Runtime engine controls — demonstrates engine capabilities live.
     */
    private void handleEngineControls() {
        // F1: Toggle debug overlay
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            debugMode = !debugMode;
        }

        // 1: Spawn new entity at runtime
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Scene current = sm.getCurrentScene();
            if (current == movingScene) {
                movingScene.spawnCircle();
            } else if (current == staticScene) {
                staticScene.spawnCircle();
            }
        }

        // 2: Destroy a random non-player entity
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            destroyRandomCircle();
        }

        // 3: Toggle player's collider on/off
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            togglePlayerCollider();
        }

        // 4: Switch scene via SceneManager
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            Scene current = sm.getCurrentScene();
            AudioManager am = engine.getIOManager().output().getAudioManager();
            if (current == staticScene) {
                loadScene(movingScene);
                am.playAudio("bgm_2"); // Switch to Scene 2 Music
            } else {
                loadScene(staticScene);
                am.playAudio("bgm_1"); // Switch to Scene 1 Music
            }
        }

        // 5: Randomize all circle colors
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            randomizeColors();
        }
    }

    // ═══════════════════════════════════════════
    //  Engine Control Methods
    // ═══════════════════════════════════════════

    /** Removes a random circle — demonstrates runtime entity destruction. */
    private void destroyRandomCircle() {
        for (Entity e : em.getActiveEntities()) {
            if (e.hasComponent(Collectible.class)) {
                mm.unregister(e.getId());
                em.destroyEntity(e.getId());
                return;
            }
        }
    }

    /** Toggles player collider — demonstrates runtime component add/remove. */
    private void togglePlayerCollider() {
        if (player.hasComponent(Collider.class)) {
            player.removeComponent("Collider");
            Sprite s = player.getComponent(Sprite.class);
            if (s != null) s.setColor(0.2f, 0.9f, 0.3f, 0.4f);
        } else {
            player.add(new Collider(PLAYER_SIZE, PLAYER_SIZE, 0, 0, 0, false));
            Sprite s = player.getComponent(Sprite.class);
            if (s != null) s.setColor(0.2f, 0.9f, 0.3f, 1f);
        }
    }

    /** Randomizes all circle colors — demonstrates runtime component modification. */
    private void randomizeColors() {
        for (Entity e : em.getActiveEntities()) {
            if (e.hasComponent(Collectible.class)) {
                Sprite s = e.getComponent(Sprite.class);
                if (s != null) {
                    s.setColor(
                            0.4f + rng.nextFloat() * 0.6f,
                            0.4f + rng.nextFloat() * 0.6f,
                            0.4f + rng.nextFloat() * 0.6f, 1f);
                }
            }
        }
    }

    // ═══════════════════════════════════════════
    //  Respawn + Boundary Logic
    // ═══════════════════════════════════════════

    private void handleRespawns(float dt) {
        for (Entity e : em.getActiveEntities()) {
            Collectible col = e.getComponent(Collectible.class);
            if (col == null) continue;

            col.tick(dt);

            if (col.isReadyToRespawn()) {
                col.respawn();
                Sprite sprite = e.getComponent(Sprite.class);
                if (sprite != null) sprite.setVisible(true);

                Transform t = mm.getTransform(e.getId());
                if (t != null) {
                    float margin = 50f;
                    float diameter = CIRCLE_SIZE * 2;
                    t.setPosition(
                            margin + rng.nextFloat() * (worldW - diameter - margin * 2),
                            margin + rng.nextFloat() * (worldH - diameter - margin * 2));
                }
            }
        }
    }

    private void clampPlayerToScreen() {
        Transform t = mm.getTransform(player.getId());
        if (t == null) return;
        if (t.getX() < 0) t.setX(0);
        if (t.getY() < 0) t.setY(0);
        if (t.getX() + PLAYER_SIZE > worldW) t.setX(worldW - PLAYER_SIZE);
        if (t.getY() + PLAYER_SIZE > worldH) t.setY(worldH - PLAYER_SIZE);
    }

    // ═══════════════════════════════════════════
    //  Rendering
    // ═══════════════════════════════════════════

    private void draw() {
        // ─── Let the current scene set the background color ───
        Scene current = sm.getCurrentScene();
        if (current != null) {
            current.render(sceneRenderer);
        } else {
            sceneRenderer.clear(0.08f, 0.08f, 0.15f, 1f);
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // ─── Draw entities ───
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity e : em.getActiveEntities()) {
            Sprite sprite = e.getComponent(Sprite.class);
            if (sprite == null || !sprite.isVisible()) continue;

            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            shapeRenderer.setColor(sprite.getR(), sprite.getG(), sprite.getB(), sprite.getA());

            if (e.hasComponent(PlayerTag.class)) {
                drawTriangle(t.getX(), t.getY(), sprite.getWidth(), sprite.getHeight());
            } else {
                float r = sprite.getWidth() / 2f;
                shapeRenderer.circle(t.getX() + r, t.getY() + r, r, 32);
            }
        }

        shapeRenderer.end();

        // ─── Debug: collider outlines ───
        if (debugMode) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1f, 0f, 0f, 0.7f);

            for (Entity e : em.getActiveEntities()) {
                Collider col = e.getComponent(Collider.class);
                Transform t = mm.getTransform(e.getId());
                if (col == null || t == null) continue;

                shapeRenderer.rect(
                        t.getX() + col.getOffsetX(),
                        t.getY() + col.getOffsetY(),
                        col.getWidth(), col.getHeight());
            }

            shapeRenderer.end();
        }

        // ─── Text overlay ───
        batch.begin();

        // Engine header
        font.draw(batch, "Engine Prototype - Entity/Component System Demo", 10, worldH - 10);
        fontSmall.draw(batch,
                "[WASD] Move  [1] Spawn  [2] Destroy  [3] Toggle Collider  [4] Switch Scene  [5] Recolor  [F1] Debug",
                10, worldH - 30);

        // Status line — uses SceneManager for scene name
        String sceneName = (current != null) ? current.getSceneName() : "None";
        boolean hasCollider = player.hasComponent(Collider.class);
        fontSmall.draw(batch,
                sceneName + "  |  Collected: " + collectHandler.getScore()
                        + "  |  Collider: " + (hasCollider ? "ON" : "OFF"),
                10, worldH - 48);

        // Respawn info
        int hidden = countCollected();
        if (hidden > 0) {
            fontSmall.draw(batch, hidden + " entity(s) respawning...", 10, worldH - 64);
        }

        // ─── Debug panel ───
        if (debugMode) {
            float dx = worldW - 260;
            float dy = worldH - 10;

            font.draw(batch, "[ DEBUG ]", dx, dy); dy -= 20;

            fontSmall.draw(batch, "Active Entities: " + em.getActiveEntities().size(), dx, dy); dy -= 16;
            fontSmall.draw(batch, "Player ID: " + player.getId(), dx, dy); dy -= 16;

            int sprites = 0, colliders = 0, collectibles = 0;
            for (Entity e : em.getActiveEntities()) {
                if (e.hasComponent(Sprite.class)) sprites++;
                if (e.hasComponent(Collider.class)) colliders++;
                if (e.hasComponent(Collectible.class)) collectibles++;
            }
            fontSmall.draw(batch, "Sprites: " + sprites + "  Colliders: " + colliders, dx, dy); dy -= 16;
            fontSmall.draw(batch, "Collectibles: " + collectibles, dx, dy); dy -= 16;
            fontSmall.draw(batch, "Scene: " + sceneName, dx, dy); dy -= 16;
            fontSmall.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), dx, dy); dy -= 16;

            StringBuilder ids = new StringBuilder("IDs: ");
            for (Entity e : em.getActiveEntities()) {
                ids.append(e.getId()).append(" ");
            }
            fontSmall.draw(batch, ids.toString(), dx, dy);
        }

        batch.end();
    }

    private void drawTriangle(float x, float y, float w, float h) {
        shapeRenderer.triangle(x, y, x + w, y, x + w / 2f, y + h);
    }

    private int countCollected() {
        int count = 0;
        for (Entity e : em.getActiveEntities()) {
            Collectible col = e.getComponent(Collectible.class);
            if (col != null && col.isCollected()) count++;
        }
        return count;
    }

    private static class GdxSceneRenderer implements Scene.Renderer {
        @Override
        public void clear(float r, float g, float b, float a) {
            Gdx.gl.glClearColor(r, g, b, a);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
    }

    @Override
    public void dispose() {
        if (engine != null) engine.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (fontSmall != null) fontSmall.dispose();
    }
}
