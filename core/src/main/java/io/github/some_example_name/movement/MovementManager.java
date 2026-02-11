package io.github.some_example_name.movement;

import java.util.HashMap;
import java.util.Map;

import io.github.some_example_name.entity.EntityManager;

/**
 * MovementManager: owns and updates Transform/Motion components.
 * Managers talk to managers - Entity does NOT access Transform/Motion directly.
 */

public class MovementManager {

    // gravity acceleration (pixels/sec^2). Flip sign if your Y-axis is up.
    private static final float GRAVITY = 980f;
    
    // Store Transform components by entity ID
    private Map<Integer, Transform> transforms;
    
    // Store Motion components by entity ID
    private Map<Integer, Motion> motions;
    
    // Initialize the manager
    public void init() {
        transforms = new HashMap<>();
        motions = new HashMap<>();
    }
    
    // Register an entity with movement components
    public void register(int entityId, Transform transform, Motion motion) {
        if (transform != null) {
            transforms.put(entityId, transform);
        }
        if (motion != null) {
            motions.put(entityId, motion);
        }
    }
    
    // Unregister an entity
    public void unregister(int entityId) {
        transforms.remove(entityId);
        motions.remove(entityId);
    }
    
    // Get Transform for an entity
    public Transform getTransform(int entityId) {
        return transforms.get(entityId);
    }
    
    // Get Motion for an entity
    public Motion getMotion(int entityId) {
        return motions.get(entityId);
    }
    
    // Check if entity has Transform
    public boolean hasTransform(int entityId) {
        return transforms.containsKey(entityId);
    }
    
    // Check if entity has Motion
    public boolean hasMotion(int entityId) {
        return motions.containsKey(entityId);
    }
    
    // Apply movement physics for this frame
    public void apply(float dt, EntityManager em) {
    	// Check if invalid delta time or missing EntityManager (dt can't be negative)
    	if (dt <= 0f || em == null) return;

    	// Check if maps are initialized
        if (transforms == null || motions == null) return;
        
        // Loop through all entities that have Motion as not all entities need to move
        for (Map.Entry<Integer, Motion> entry : motions.entrySet()) {
        	// Extract entity ID from the map entry
        	int entityId = entry.getKey();
        	
        	// Talk to EntityManager for the active entity 
            if (!em.isActive(entityId)) {
                continue; 
            }
        	
        	// Extract Motion component from the map entry
            Motion m = entry.getValue();
            
            // Skip if no Transform for this entity --> Motion w/o Transform = can't update position
            if (!transforms.containsKey(entityId)) {
                continue;
            }
            
            // Get Transform component for this entity --> holds the position (x, y) and rotation data
            Transform t = transforms.get(entityId);
            
            // Check for null components
            if (t == null || m == null) {
                continue;
            }
            
            // Physic Calculation: Acceleration --> velocity 
            // using: v_new = v_old + a * dt
            // vx = horizontal velocity, getAx() = horizontal acceleration
            float vx = m.getVx() + m.getAx() * dt;
            
             // vy = vertical velocity, getAy() = vertical acceleration
            float vy = m.getVy() + m.getAy() * dt;
            
            // Physic Calculation: Gravity (if enabled) --> only affects vertical velocity (vy)
            if (m.isGravityEnabled()) {
                vy += GRAVITY * dt;		 // GRAVITY is constant (980 pixels/sec²)
            }
            
            // Physic Calculation: Clamp to Max Speed
            // Get the maximum allowed speed for this entity
            float max = m.getMaxSpeed();
            
            // Only clamp if maxSpeed is set (> 0)
            if (max > 0f) {
            	// Calculate speed squared (faster than sqrt)
                // speed² = vx² + vy² (Pythagorean theorem)
                float speedSq = vx * vx + vy * vy;
                
                // Calculate maxSpeed squared
                float maxSq = max * max;
                
                // Check if current speed exceeds maximum speed
                if (speedSq > maxSq) {
                	// Calculate actual speed using square root
                    // speed = √(vx² + vy²)
                    float speed = (float) Math.sqrt(speedSq);
                    
                    // Avoid division by zero
                    if (speed > 0f) {
                    	// Calculate scaling factor to reduce speed to max
                        // scale = maxSpeed / currentSpeed
                        float scale = max / speed;
                        
                        // Scale down horizontal velocity
                        vx *= scale;
                        
                        // Scale down vertical velocity
                        vy *= scale;
                    }
                }
            }
            
            // Physic Calculation: Velocity --> Position
            // Update x position using: x_new = x_old + vx * dt
            t.setX(t.getX() + vx * dt);
            
            // Update y position using: y_new = y_old + vy * dt
            t.setY(t.getY() + vy * dt);
            
            // Update velocity in Motion
            m.setVx(vx);
            m.setVy(vy);
        }

    }

    // Update called by engine each frame
    public void update(float dt, EntityManager em) {
        apply(dt, em);
    }
    
    // Clean up resources
    public void dispose() {
    	// Clean up transforms map
        if (transforms != null) {
        	// Remove all entries from the map --> helps garbage collector free memory faster
            transforms.clear();
            
            // Set reference to null for garbage collection
            transforms = null;
        }
        
        // Clean up motions map
        if (motions != null) {
        	// Remove all entries from the map + release references to all motion components
            motions.clear();
            
            // Set reference to null for garbage collection
            motions = null;
        }
    }
}

