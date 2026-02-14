package io.github.some_example_name.entity;

//import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private Map<Integer, Entity> entities;
    private int nextId;

    public EntityManager() {
        this.entities = new HashMap<>();
        this.nextId = 0;
    }

    public Entity createEntity() {
    	Entity entity = new Entity(nextId);
        entities.put(nextId, entity);
        nextId++;
        return entity;
    }

    public void destroyEntity(int id) {
        Entity entity = entities.remove(id);
        if (entity != null) {
            entity.destroy();
        }
    }

    public Entity getEntity(int id) {
        return entities.get(id);
    }

    /**
     * Get all entities
     */
    public Collection<Entity> getAllEntities() {
        return entities.values();
    }

    /**
     * Get all active entities
     */
    public List<Entity> getActiveEntities() {
        List<Entity> active = new ArrayList<>();
        for (Entity entity : entities.values()) {
            if (entity.isActive()) {
                active.add(entity);
            }
        }
        return active;
    }

    /**
     * Get all entities that have a specific component
     */
    public List<Entity> getEntitiesWithComponent(String componentType) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities.values()) {
            if (entity.isActive() && entity.hasComponent(componentType)) {
                result.add(entity);
            }
        }
        return result;
    }

    /**
     * Update all active entities
     */
    public void updateAll(float dt) {
        // Currently just a hook for entity-level updates
        // Individual component updates happen in their respective managers
    }

    /**
     * Get total entity count
     */
    public int count() {
        return entities.size();
    }

    /**
     * Remove all entities
     */
    public void clear() {
        for (Entity entity : entities.values()) {
            entity.destroy();
        }
        entities.clear();
    }
}

