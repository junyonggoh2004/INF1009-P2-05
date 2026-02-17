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
import io.github.some_example_name.core.EngineCore;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.Sprite;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;

/**
 * Prototype game demonstrating the abstract engine.
 *
 * - Player (triangle) moves with WASD / arrow keys
 * - Circles are scattered randomly on screen
 * - Touching a circle collects it (disappears)
 * - Circles respawn after a few seconds
 * - Background changes when all circles are collected
 * - Score is displayed on screen
 */
public class PrototypeEngine extends ApplicationAdapter {

    // ─── Engine ───
    private EngineCore engine;
    private EntityManager em;
    private MovementManager mm;

    // ─── Rendering ───
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    // ─── Game config ───
    private static final int CIRCLE_COUNT = 10;
    private static final float CIRCLE_SIZE = 20f;
    private static final float PLAYER_SIZE = 30f;
    private static final float PLAYER_SPEED = 250f;
    private static final float RESPAWN_DELAY = 3.0f;

    // ─── Game state ───
    private Entity player;
    private CollectHandler collectHandler;
    private final Random rng = new Random();
    private float worldW, worldH;

    // ─── Scene state (simple background color change) ───
    private Color bgColor;
    private static final Color BG_NORMAL = new Color(0.08f, 0.08f, 0.15f, 1f);
    private static final Color BG_ALL_COLLECTED = new Color(0.05f, 0.15f, 0.08f, 1f);

    @Override
    public void create() {
        // ─── Init rendering ───
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        bgColor = BG_NORMAL;

        worldW = Gdx.graphics.getWidth();
        worldH = Gdx.graphics.getHeight();

        // ─── Init engine (creates + wires all managers) ───
        engine = new EngineCore();
        engine.init();

        // Grab references for convenience
        em = engine.getEntityManager();
        mm = engine.getMovementManager();

        // ─── Create game entities ───
        createPlayer();
        createCircles();
    }

    // ─── Entity creation ───

    private void createPlayer() {
        player = em.createEntity();

        // Tag so systems can identify the player
        player.add(new PlayerTag());

        // Visual data: green triangle
        Sprite sprite = new Sprite("triangle", PLAYER_SIZE, PLAYER_SIZE);
        sprite.setColor(0.2f, 0.9f, 0.3f, 1f);
        player.add(sprite);

        // Collision box matching the sprite size
        player.add(new Collider(PLAYER_SIZE, PLAYER_SIZE, 0, 0, 0, false));

        // Collision handler — what happens when player touches something
        collectHandler = new CollectHandler();
        player.add(collectHandler);

        // Register movement at center of screen
        mm.register(player.getId(),
                new Transform(worldW / 2f - PLAYER_SIZE / 2f, worldH / 2f - PLAYER_SIZE / 2f),
                new Motion());
    }

    private void createCircles() {
        float margin = 40f;
        for (int i = 0; i < CIRCLE_COUNT; i++) {
            Entity circle = em.createEntity();

            // Random color for variety
            float diameter = CIRCLE_SIZE * 2;
            Sprite sprite = new Sprite("circle", diameter, diameter);
            sprite.setColor(
                    0.4f + rng.nextFloat() * 0.6f,
                    0.4f + rng.nextFloat() * 0.6f,
                    0.4f + rng.nextFloat() * 0.6f,
                    1f
            );
            circle.add(sprite);

            // Respawns after RESPAWN_DELAY seconds
            circle.add(new Collectible(RESPAWN_DELAY));

            // Collision box
            circle.add(new Collider(diameter, diameter, 0, 0, 0, false));

            // Random position (with margin so circles aren't at edges)
            float x = margin + rng.nextFloat() * (worldW - diameter - margin * 2);
            float y = margin + rng.nextFloat() * (worldH - diameter - margin * 2);

            // Static — circles don't move (velocity 0,0)
            mm.register(circle.getId(), new Transform(x, y), new Motion());
        }
    }

    // ─── Game loop ───

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        if (dt <= 0) return;

        // 1. Handle input
        handleInput();

        // 2. Engine update (movement → collision, in correct order)
        engine.update(dt);

        // 3. Game-specific logic
        handleRespawns(dt);
        clampPlayerToScreen();
        updateScene();

        // 4. Draw
        draw();
    }

    // ─── Input ───

    private void handleInput() {
        Motion pm = mm.getMotion(player.getId());
        if (pm == null) return;

        float vx = 0f;
        float vy = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    vy += PLAYER_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  vy -= PLAYER_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  vx -= PLAYER_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) vx += PLAYER_SPEED;

        pm.setVx(vx);
        pm.setVy(vy);
    }

    // ─── Respawn logic ───

    private void handleRespawns(float dt) {
        for (Entity e : em.getActiveEntities()) {
            Collectible col = e.getComponent(Collectible.class);
            if (col == null) continue;

            col.tick(dt);

            if (col.isReadyToRespawn()) {
                col.respawn();

                // Show the circle again
                Sprite sprite = e.getComponent(Sprite.class);
                if (sprite != null) {
                    sprite.setVisible(true);
                }

                // Randomize position on respawn
                Transform t = mm.getTransform(e.getId());
                if (t != null) {
                    float margin = 40f;
                    float diameter = CIRCLE_SIZE * 2;
                    t.setPosition(
                            margin + rng.nextFloat() * (worldW - diameter - margin * 2),
                            margin + rng.nextFloat() * (worldH - diameter - margin * 2)
                    );
                }
            }
        }
    }

    // ─── Scene management (simple background color change) ───

    private void updateScene() {
        boolean allCollected = true;

        for (Entity e : em.getActiveEntities()) {
            Collectible col = e.getComponent(Collectible.class);
            if (col != null && !col.isCollected()) {
                allCollected = false;
                break;
            }
        }

        bgColor = allCollected ? BG_ALL_COLLECTED : BG_NORMAL;
    }

    // ─── Clamp player to screen bounds ───

    private void clampPlayerToScreen() {
        Transform t = mm.getTransform(player.getId());
        if (t == null) return;

        if (t.getX() < 0) t.setX(0);
        if (t.getY() < 0) t.setY(0);
        if (t.getX() + PLAYER_SIZE > worldW) t.setX(worldW - PLAYER_SIZE);
        if (t.getY() + PLAYER_SIZE > worldH) t.setY(worldH - PLAYER_SIZE);
    }

    // ─── Rendering ───

    private void draw() {
        // Clear screen with scene background color
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Enable transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity e : em.getActiveEntities()) {
            Sprite sprite = e.getComponent(Sprite.class);
            if (sprite == null || !sprite.isVisible()) continue;

            Transform t = mm.getTransform(e.getId());
            if (t == null) continue;

            float x = t.getX();
            float y = t.getY();

            shapeRenderer.setColor(sprite.getR(), sprite.getG(), sprite.getB(), sprite.getA());

            if (e.hasComponent(PlayerTag.class)) {
                // Draw triangle for player
                drawTriangle(x, y, sprite.getWidth(), sprite.getHeight());
            } else {
                // Draw circle for collectibles
                float radius = sprite.getWidth() / 2f;
                shapeRenderer.circle(x + radius, y + radius, radius, 32);
            }
        }

        shapeRenderer.end();

        // Draw UI text (score)
        batch.begin();
        font.draw(batch, "Score: " + collectHandler.getScore(), 10, worldH - 10);
        font.draw(batch, "WASD to move | Collect all circles!", 10, worldH - 30);

        // Show respawn hint if any circles are collected
        int hidden = countCollected();
        if (hidden > 0) {
            font.draw(batch, hidden + " circle(s) respawning...", 10, worldH - 50);
        }
        batch.end();
    }

    /**
     * Draws an upward-pointing triangle.
     */
    private void drawTriangle(float x, float y, float w, float h) {
        shapeRenderer.triangle(
                x, y,               // bottom-left
                x + w, y,           // bottom-right
                x + w / 2f, y + h   // top-center
        );
    }

    // ─── Utility ───

    private int countCollected() {
        int count = 0;
        for (Entity e : em.getActiveEntities()) {
            Collectible col = e.getComponent(Collectible.class);
            if (col != null && col.isCollected()) {
                count++;
            }
        }
        return count;
    }

    // ─── Cleanup ───

    @Override
    public void dispose() {
        if (engine != null) engine.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}