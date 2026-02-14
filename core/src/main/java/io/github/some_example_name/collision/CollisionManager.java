package io.github.some_example_name.collision;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;

/**
 * CollisionManager:
 * - collects entities with Collider
 * - checks pairs for collision (using CollisionDetector)
 * - calls resolver (if provided)
 * - notifies ONCE when collision starts
 *
 * Orchestration only.
 */
public class CollisionManager {

    private boolean[][] collisionMatrix;
    private MovementManager movementManager;
    private CollisionResolver resolver;
    private final Set<Long> activePairs = new HashSet<>();

    public void init(MovementManager mm, int layerCount) {
        this.movementManager = mm;

        collisionMatrix = new boolean[layerCount][layerCount];
        for (int i = 0; i < layerCount; i++) {
            for (int j = 0; j < layerCount; j++) {
                collisionMatrix[i][j] = true;
            }
        }
    }

    // demo to inject resolver
    public void setResolver(CollisionResolver resolver) {
        this.resolver = resolver;
    }

    public void update(float dt, EntityManager em) {
        if (em == null || movementManager == null) return;

        List<Entity> entities = collectCollidables(em);
        if (entities.size() < 2) return;

        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);

            Collider ca = a.getComponent(Collider.class);
            Transform ta = movementManager.getTransform(a.getId());
            if (ca == null || ta == null) continue;

            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);

                Collider cb = b.getComponent(Collider.class);
                Transform tb = movementManager.getTransform(b.getId());
                if (cb == null || tb == null) continue;

                if (!canCollide(ca.getLayer(), cb.getLayer())) continue;

                float ax = ta.getX() + ca.getOffsetX();
                float ay = ta.getY() + ca.getOffsetY();
                float bx = tb.getX() + cb.getOffsetX();
                float by = tb.getY() + cb.getOffsetY();

                //use the detector
                boolean hit = CollisionDetector.intersects(ca, cb, ax, ay, bx, by);

                long key = pairKey(a.getId(), b.getId());

                if (hit) {
                    // call resolver every frame while overlapping 
                    if (resolver != null) {
                        resolver.resolve(ca, cb, ta, tb, ax, ay, bx, by);
                    }

                    // notify once on enter
                    if (activePairs.add(key)) {
                        notifyCollision(a, b);
                    }
                } else {
                    activePairs.remove(key);
                }
            }
        }
    }

    public void dispose() {
        collisionMatrix = null;
        movementManager = null;
        resolver = null;
        activePairs.clear();
    }

    private void notifyCollision(Entity a, Entity b) {
        CollisionHandler ha = a.getComponent(CollisionHandler.class);
        if (ha != null) ha.onCollision(a, b);

        CollisionHandler hb = b.getComponent(CollisionHandler.class);
        if (hb != null) hb.onCollision(b, a);
    }

    private List<Entity> collectCollidables(EntityManager em) {
        List<Entity> list = em.getEntitiesWithComponent("Collider");
        return (list != null) ? list : new ArrayList<>();
    }

    private long pairKey(int idA, int idB) {
        int min = Math.min(idA, idB);
        int max = Math.max(idA, idB);
        return (((long) min) << 32) | (max & 0xffffffffL);
    }

    private boolean canCollide(int layerA, int layerB) {
        if (collisionMatrix == null) return true;
        if (layerA < 0 || layerB < 0) return false;
        if (layerA >= collisionMatrix.length || layerB >= collisionMatrix.length) return false;
        return collisionMatrix[layerA][layerB];
    }

    public void setLayerCollision(int a, int b, boolean canCollide) {
        if (collisionMatrix == null) return;
        if (a < 0 || b < 0) return;
        if (a >= collisionMatrix.length || b >= collisionMatrix.length) return;

        collisionMatrix[a][b] = canCollide;
        collisionMatrix[b][a] = canCollide;
    }
}
