package io.github.some_example_name.collision;

import java.util.List;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.movement.MovementManager;
import io.github.some_example_name.movement.Transform;

/**
 * CollisionManager
 *
 * Finds all entities that have a Collider component
 * Checks each pair (A vs B) once
 * Skips pairs that should not collide (layer filtering)
 * If they overlap, it calls the resolver to handle the result
 *
 * CollisionManager talks to EntityManager + MovementManager.
 */
public class CollisionManager {

    private CollisionDetector detector;
    private CollisionResolver resolver;
    private MovementManager movementManager;

    // Layer filtering: layerA vs layerB
    private boolean[][] collisionMatrix;

    // If false, trigger colliders are ignored completely.
    // If true, triggers can be detected, but we still avoid physical resolution for triggers.
    private boolean enableTriggers = true;

    /**
     * Initializes the manager and creates the collision matrix.
     *
     * mm         MovementManager (used to fetch Transforms)
     * layerCount number of collision layers 
     */
    		 public void init(MovementManager mm,
                     CollisionDetector detector,
                     CollisionResolver resolver,
                     int layerCount) {

        this.movementManager = mm;
        this.detector = detector;
        this.resolver = resolver;

        if (layerCount <= 0) layerCount = 1;

        collisionMatrix = new boolean[layerCount][layerCount];
        for (int i = 0; i < layerCount; i++) {
            for (int j = 0; j < layerCount; j++) {
                collisionMatrix[i][j] = true;
            }
        }
    }


    public void setEnableTriggers(boolean enableTriggers) {
        this.enableTriggers = enableTriggers;
    }

    /**
     * Sets whether two layers should be tested for collision.
     * This is symmetric: setting (A,B) also sets (B,A).
     *
     * Example:
     * - setLayerCollision(1, 1, false)  // money doesn't collide with money
     */
    public void setLayerCollision(int layerA, int layerB, boolean canCollide) {
        if (collisionMatrix == null) return;

        if (layerA < 0 || layerB < 0 ||
            layerA >= collisionMatrix.length ||
            layerB >= collisionMatrix.length) {
            return;
        }

        collisionMatrix[layerA][layerB] = canCollide;
        collisionMatrix[layerB][layerA] = canCollide;
    }

    public void update(float dt, EntityManager em) {
        if (em == null || movementManager == null || detector == null || collisionMatrix == null) return;

        List<Entity> entities = collectCollidables(em);
        if (entities == null || entities.size() < 2) return;

        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);

            Collider aCol = a.getComponent(Collider.class);
            Transform aTr = movementManager.getTransform(a.getId());
            if (aCol == null || aTr == null) continue;

            if (!enableTriggers && aCol.isTrigger()) continue;

            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);

                Collider bCol = b.getComponent(Collider.class);
                Transform bTr = movementManager.getTransform(b.getId());
                if (bCol == null || bTr == null) continue;

                if (!enableTriggers && bCol.isTrigger()) continue;

                if (!canCollide(aCol.getLayer(), bCol.getLayer())) continue;

                if (detector.intersects(aCol, aTr, bCol, bTr)) {

                    // Only resolve "solid" collisions (non-trigger)
                    if (resolver != null && !aCol.isTrigger() && !bCol.isTrigger()) {
                        resolver.resolve(a, aCol, aTr, b, bCol, bTr);
                    }
                }
            }
        }
    }

    //Collects all entities that currently have a Collider component.    
    private List<Entity> collectCollidables(EntityManager em) {
        return em.getEntitiesWithComponent("Collider");
    }

//Checks if whether two layers are allowed to collide.
//The collisionMatrix = which layer combinations are valid.
  

    private boolean canCollide(int layerA, int layerB) {
        if (collisionMatrix == null) return false;
      
        //If a layer index is invalid, we return false to avoid crashing.

        if (layerA < 0 || layerB < 0 ||
            layerA >= collisionMatrix.length ||
            layerB >= collisionMatrix.length) {
            return false;
        }

        return collisionMatrix[layerA][layerB];
    }

    public void dispose() {
        movementManager = null;
        resolver = null;
        detector = null;
        collisionMatrix = null;
    }
}
