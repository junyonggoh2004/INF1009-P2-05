package io.github.some_example_name.collision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * and notifies CollisionHandlers on both entities.
 *
 * CollisionManager talks to EntityManager + MovementManager.
 */

public class CollisionManager {

    private CollisionDetector detector;
    private CollisionResolver resolver; 
    private CollisionHandler handler; 

    private MovementManager movementManager;

    // collisionMatrix[a][b] == true means layer a is allowed to collide with layer b
    private boolean[][] collisionMatrix;

    // If false then trigger colliders are ignored completely.
    // If true then triggers can be detected + events fired, but NOT physically resolved.
    private boolean enableTriggers = true;

    //Collision state tracking (needed for Enter / Stay / Exit)

    // Pairs that were colliding last frame
    private final Set<Long> previousPairs = new HashSet<>();

    // Pairs that are colliding this frame
    private final Set<Long> currentPairs = new HashSet<>();

    // For Exit events, we need to remember which entities were in each pair last frame.
    private final Map<Long, Entity[]> previousPairEntities = new HashMap<>();
    private final Map<Long, Entity[]> currentPairEntities  = new HashMap<>();


    /**
     * Initializes the manager and creates the collision matrix.
     *
     * @param mm         MovementManager used to fetch Transforms
     * @param detector   CollisionDetector strategy (required)
     * @param resolver   CollisionResolver strategy (optional; can be null)
     * @param layerCount number of collision layers (>= 1)
     */
    public void init(MovementManager mm,
                     CollisionDetector detector,
                     CollisionResolver resolver,
                     int layerCount) {

        this.movementManager = mm;
        this.detector = detector;
        this.resolver = resolver;

        if (layerCount <= 0) layerCount = 1;

        // Default: every layer can collide with every layer
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


     // Sets whether two layers should be tested for collision.
     //Symmetric: (A,B) also updates (B,A).
    
    public void setLayerCollision(int layerA, int layerB, boolean canCollide) {
        if (collisionMatrix == null) return;

        // Ignore invalid layers to avoid crashes
        if (layerA < 0 || layerB < 0 ||
            layerA >= collisionMatrix.length ||
            layerB >= collisionMatrix.length) {
            return;
        }

        collisionMatrix[layerA][layerB] = canCollide;
        collisionMatrix[layerB][layerA] = canCollide;
    }

//Main collision update. Call once per frame.
//Detects collisions and fires Enter/Stay/Exit events.
    
    public void update(float dt, EntityManager em) {
        if (em == null || movementManager == null || detector == null || collisionMatrix == null) return;

        // Start new frame: clear current state
        currentPairs.clear();
        currentPairEntities.clear();

        List<Entity> entities = collectCollidables(em);

        // If nothing to check, we still need to fire Exit for pairs from last frame
        if (entities == null || entities.size() < 2) {
            fireExitEvents();
            commitFrameState();
            return;
        }

        // Pairwise check (i < j ensures each pair is checked once)
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);

            Collider aCol = a.getComponent(Collider.class);
            Transform aTr = movementManager.getTransform(a.getId());
            if (aCol == null || aTr == null) continue;

            // Ignore trigger colliders completely if triggers are disabled
            if (!enableTriggers && aCol.isTrigger()) continue;

            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);

                Collider bCol = b.getComponent(Collider.class);
                Transform bTr = movementManager.getTransform(b.getId());
                if (bCol == null || bTr == null) continue;

                if (!enableTriggers && bCol.isTrigger()) continue;

                // Layer filter (skip pairs that should not collide)
                if (!canCollide(aCol.getLayer(), bCol.getLayer())) continue;

                // Overlap check (strategy)
                if (detector.intersects(aCol, aTr, bCol, bTr)) {

                    // Stable key for unordered pair (A,B) == (B,A)
                    long key = pairKey(a.getId(), b.getId());

                    // Mark collision as happening this frame
                    currentPairs.add(key);
                    currentPairEntities.put(key, new Entity[] { a, b });

                    // Fire Enter or Stay depending on whether the pair existed last frame
                    if (!previousPairs.contains(key)) {
                        notifyEnter(a, b);
                    } else {
                        notifyStay(a, b);
                    }

                    // Resolve only if BOTH colliders are solid (non-trigger)
                    if (resolver != null && !aCol.isTrigger() && !bCol.isTrigger()) {
                        resolver.resolve(a, aCol, aTr, b, bCol, bTr);
                    }

                    CollisionHandler ha = a.getComponent(CollisionHandler.class);
                    if (ha != null) ha.onEnter(a, b);

                    CollisionHandler hb = b.getComponent(CollisionHandler.class);
                    if (hb != null) hb.onEnter(b, a);
                }
            }
        }

        // After all checks pairs that disappeared should fire Exit
        fireExitEvents();

        // Save current state to become previous state for next frame
        commitFrameState();
    }
    
    public void setHandler(CollisionHandler handler) {
        this.handler = handler;
    }


    //fires collision events through a CollisionHandler callback
    private void notifyEnter(Entity a, Entity b) {
        if (handler != null) handler.onEnter(a, b);
    }

    private void notifyStay(Entity a, Entity b) {
        if (handler != null) handler.onStay(a, b);
    }

    private void notifyExit(Entity a, Entity b) {
        if (handler != null) handler.onExit(a, b);
    }


  //Fires Exit for pairs that were colliding last frame but not this frame.

    private void fireExitEvents() {
        for (long oldKey : previousPairs) {
            if (!currentPairs.contains(oldKey)) {
                Entity[] pair = previousPairEntities.get(oldKey);
                if (pair != null && pair.length == 2) {
                    notifyExit(pair[0], pair[1]);
                }
            }
        }
    }
 
     //Commits current frame state into previous frame state.
     
    private void commitFrameState() {
        previousPairs.clear();
        previousPairs.addAll(currentPairs);

        previousPairEntities.clear();
        previousPairEntities.putAll(currentPairEntities);
    }

   
    //Collects entities that currently have a Collider component.
   
    private List<Entity> collectCollidables(EntityManager em) {
        return em.getEntitiesWithComponent("Collider");
    }

  //Checks if two layers are allowed to collide.

    private boolean canCollide(int layerA, int layerB) {
        if (collisionMatrix == null) return false;

        if (layerA < 0 || layerB < 0 ||
            layerA >= collisionMatrix.length ||
            layerB >= collisionMatrix.length) {
            return false;
        }

        return collisionMatrix[layerA][layerB];
    }

    //Creates a stable key for an unordered pair of entity IDs.
    //Ensures (A,B) == (B,A).
  
    private long pairKey(int idA, int idB) {
        int min = Math.min(idA, idB);
        int max = Math.max(idA, idB);
        return (((long) min) << 32) | (max & 0xffffffffL);
    }

    public void dispose() {
        movementManager = null;
        resolver = null;
        detector = null;
        handler = null;
        collisionMatrix = null;
        

        previousPairs.clear();
        currentPairs.clear();
        previousPairEntities.clear();
        currentPairEntities.clear();
    }
}
