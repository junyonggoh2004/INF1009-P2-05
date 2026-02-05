package io.github.some_example_name.movement;

import java.util.List;

import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.movement.Motion;
import io.github.some_example_name.movement.Transform;

/**
 * MovementManager: updates position using Motion + Transform.
 * Assumes Entity has: isActive(), hasComponent(String), getComponent(String)
 * Assumes EntityManager has: getAll()
 * Assumes Transform has: getX/getY/setX/setY
 * Assumes Motion has: getVx/getVy/getAx/getAy/getMaxSpeed/isGravityEnabled and setters for vx/vy (and optionally ax/ay)
 */

public class MovementManager {

    // gravity acceleration (pixels/sec^2). Flip sign if your Y-axis is up.
    private static final float GRAVITY = 980f;
    
    // init manager 
    public void init() {
    }
    
    // apply() = do the actual movement math for this frame
    public void apply(float dt, EntityManager em) {
    	
    	// check if invalid dt or missing EntityManager
    	if (dt <= 0f || em == null) return;

    	// get all entities from EntityManager
        List<Entity> entities = em.getAll();
        
        // check for entities list
        if (entities == null) {
            return;
        }
        
        // loop through every entity
        for (Entity e : entities) {

            // skip invalid/inactive entities
            if (e == null || !e.isActive()) {
            	continue;
            }

            // only move entities that have both components (transform + motion)
            if (!e.hasComponent("Transform") || !e.hasComponent("Motion")) {
            	continue;
            }
            
            // get Transform component (position)
            Transform t = (Transform) e.getComponent("Transform");
            
            // get Motion component (velocity/accel settings)
            Motion m = (Motion) e.getComponent("Motion");
            
            // check for missing components
            if (t == null || m == null) {
            	continue;
            }
            
            // compute new vx using acceleration
            // v = v + a*dt
            float vx = m.getVx() + m.getAx() * dt;
            
            // compute new vy using acceleration
            // v = v + a*dt
            float vy = m.getVy() + m.getAy() * dt;

            // add gravity if enabled
            if (m.isGravityEnabled()) {
                vy += GRAVITY * dt;
            }

            // read speed cap
            float max = m.getMaxSpeed();
            
            // clamp speed if maxSpeed is set
            if (max > 0f) {
            	
            	// compute speed^2
                float speedSq = vx * vx + vy * vy;
                
                // compute maxSpeed^2
                float maxSq = max * max;
                
                // if too fast, scale velocity down
                if (speedSq > maxSq) {
                	
                	// compute actual speed
                    float speed = (float) Math.sqrt(speedSq);
                    
                    // avoid divide-by-zero
                    if (speed > 0f) {
                    	
                    	// compute scaling factor
                        float scale = max / speed;
                        
                        // scale vx
                        vx *= scale;
                         
                        // scale vy
                        vy *= scale;
                    }
                }
            }
            
            // update x position using vx
            // p = p + v*dt
            t.setX(t.getX() + vx * dt);
            
            // update y position using vy
            t.setY(t.getY() + vy * dt);

            // store updated vx back into Motion
            m.setVx(vx);
            
            // store updated vy back into Motion
            m.setVy(vy);

            // optional: reset accel if per-frame forces
            // m.setAx(0f);
            // m.setAy(0f);
        }
    }

    // update() = usually called by the engine each frame; delegates to apply()
    public void update(float dt, EntityManager em) {
        apply(dt, em);
    }
    
    
    // dispose manager
    public void dispose() {
        
    }
}

