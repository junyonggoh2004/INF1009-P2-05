package io.github.some_example_name.movement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;

/**
 * MovementManager: owns and updates Transform/Motion components.
 * Managers talk to managers - Entity does NOT access Transform/Motion directly.
 */

public class MovementManager {
  
    // Store Transform components by entity ID
    private Map<Integer, Transform> transforms;
    
    // Store Motion components by entity ID
    private Map<Integer, Motion> motions;
    
    // Delegate calculations to PhysicsCalculation
    private PhysicsCalculation calculator;  
    
    // Initialize the manager
    public void init() {
        transforms = new HashMap<>();
        motions = new HashMap<>();
        calculator = new PhysicsCalculation();
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
        
        // Check if calculator is initialized
        if (calculator == null) return;
        
        // Get all active entities from EntityManager
        List<Entity> activeEntities = em.getActiveEntities();
        
        // Check if list is valid
        if (activeEntities == null) return;

        // Loop through all active entities
        for (Entity entity : activeEntities) {
            // Get entity ID
            int entityId = entity.getId();
            
            // Get Motion and Transform components
            Motion m = getMotion(entityId);     
            Transform t = getTransform(entityId);
            
            // Check for null components
            if (t == null || m == null) {
                continue;
            }
            
            // DELEGATE to calculator (for physics calculation)
            calculator.calculate(t, m, dt);
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
        
        // Clear the calculator reference
        calculator = null;  // NEW
    }
}

