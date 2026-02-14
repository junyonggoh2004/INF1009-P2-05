package io.github.some_example_name.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.CollisionManager;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;

public class DemoGame extends ApplicationAdapter {

    private SpriteBatch batch;

    // rename: box + money
    private Texture boxImg;
    private Texture moneyImg;
    private Texture bgImg; // optional

    private final Random rng = new Random();

    private EntityManager em;
    private MovementManager mm;
    private CollisionManager cm;

    private Entity box; // basket renamed to box

    private float spawnTimer = 0f;
    private float spawnInterval = 0.15f;
    private float moneyFallSpeed = -230f;

    private float worldW;
    private float worldH;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // load images (from runtime assets root)
        boxImg = new Texture("blackbox.png");
        moneyImg = new Texture("SGD.png");
        // bgImg = new Texture("bg.png"); // optional

        worldW = Gdx.graphics.getWidth();
        worldH = Gdx.graphics.getHeight();

        em = new EntityManager();

        mm = new MovementManager();
        mm.init();

        cm = new CollisionManager();
        cm.init(mm, 3);
        cm.setResolver(new DebugResolver());

        // Player box
        box = em.createEntity();
        box.add(Collider.rect(120, 60, 0, 0, 0, false));
        box.add(new PlayerHitHandler());

        mm.register(box.getId(),
                new Transform(worldW / 2f - 60f, 40f),
                new Motion(0, 0));
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        if (dt <= 0) return;

        // tick flash timer
        PlayerHitHandler h = box.getComponent(PlayerHitHandler.class);
        if (h != null) h.tick(dt);

        // input: move box left/right
        Motion bm = mm.getMotion(box.getId());
        Transform bt = mm.getTransform(box.getId());
        if (bm != null && bt != null) {
            float speed = 360f;
            float vx = 0f;

            boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            if (left) vx -= speed;
            if (right) vx += speed;

            bm.setVx(vx);
            bm.setVy(0f);
        }

        // spawn money
        spawnTimer += dt;
        while (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            spawnMoney();
        }

        // update systems
        mm.update(dt, em);
        cm.update(dt, em);

        clampBoxToScreen();
        cleanupHitMoney();
        cleanupFallenMoney();

        // clear
        Gdx.gl.glClearColor(0.06f, 0.06f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (bgImg != null) {
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(bgImg, 0, 0, worldW, worldH);
        }

        for (Entity e : em.getActiveEntities()) {
            Collider c = e.getComponent(Collider.class);
            Transform t = mm.getTransform(e.getId());
            if (c == null || t == null) continue;

            float x = t.getX() + c.getOffsetX();
            float y = t.getY() + c.getOffsetY();

            if (e == box) {
                boolean flash = (h != null && h.isHitFlashActive());

                // green flash
                if (flash) batch.setColor(0.4f, 1f, 0.4f, 1f);
                else batch.setColor(1f, 1f, 1f, 1f);

                batch.draw(boxImg, x, y, c.getWidth(), c.getHeight());
                batch.setColor(1f, 1f, 1f, 1f); // reset
            } else {
                batch.setColor(1f, 1f, 1f, 1f);

                // draw money WITHOUT squeezing: keep aspect ratio
                drawKeepAspect(moneyImg, x, y, c.getWidth(), c.getHeight());
            }
        }

        batch.end();
    }

    private void spawnMoney() {
        Entity money = em.createEntity();

        float targetW = 38f + rng.nextInt(10); // 38..47

        // keep aspect ratio based on image itself
        float imgW = Math.max(1, moneyImg.getWidth());
        float imgH = Math.max(1, moneyImg.getHeight());
        float aspect = imgH / imgW;

        float targetH = targetW * aspect;

        float x = rng.nextFloat() * (worldW - targetW);
        float y = worldH + 20f;

        // collider matches draw size (so collision feels correct)
        money.add(Collider.rect(targetW, targetH, 0, 0, 0, false));
        mm.register(money.getId(), new Transform(x, y), new Motion(0f, moneyFallSpeed));
    }

    // Keeps image aspect ratio inside a target rectangle
    private void drawKeepAspect(Texture tex, float x, float y, float targetW, float targetH) {
        float imgW = Math.max(1, tex.getWidth());
        float imgH = Math.max(1, tex.getHeight());
        float imgAspect = imgW / imgH;
        float targetAspect = targetW / targetH;

        float drawW = targetW;
        float drawH = targetH;

        if (imgAspect > targetAspect) {
            // image is wider -> fit width
            drawW = targetW;
            drawH = targetW / imgAspect;
        } else {
            // image is taller -> fit height
            drawH = targetH;
            drawW = targetH * imgAspect;
        }

        // center it in the collider box
        float dx = x + (targetW - drawW) / 2f;
        float dy = y + (targetH - drawH) / 2f;

        batch.draw(tex, dx, dy, drawW, drawH);
    }

    private void cleanupHitMoney() {
        for (Entity e : em.getActiveEntities()) {
            if (e == box) continue;

            RemoveMe rm = e.getComponent(RemoveMe.class);
            if (rm != null) {
                mm.unregister(e.getId());
                em.destroyEntity(e.getId());
            }
        }
    }

    private void cleanupFallenMoney() {
        for (Entity e : em.getActiveEntities()) {
            if (e == box) continue;

            Transform t = mm.getTransform(e.getId());
            Collider c = e.getComponent(Collider.class);
            if (t == null || c == null) continue;

            if (t.getY() + c.getHeight() < -120f) {
                mm.unregister(e.getId());
                em.destroyEntity(e.getId());
            }
        }
    }

    private void clampBoxToScreen() {
        Transform t = mm.getTransform(box.getId());
        Collider c = box.getComponent(Collider.class);
        if (t == null || c == null) return;

        float x = t.getX();
        float maxX = worldW - c.getWidth();

        if (x < 0) t.setX(0);
        if (x > maxX) t.setX(maxX);
    }

    @Override
    public void dispose() {
        if (cm != null) cm.dispose();
        if (mm != null) mm.dispose();

        if (batch != null) batch.dispose();
        if (boxImg != null) boxImg.dispose();
        if (moneyImg != null) moneyImg.dispose();
        if (bgImg != null) bgImg.dispose();
    }
}
