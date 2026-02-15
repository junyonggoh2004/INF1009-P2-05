package io.github.some_example_name.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManager {

    /** Map of entity ID -> Entity */
    private Map<Integer, Entity> entities;

    /** Counter for generating unique entity IDs */
    private int nextId;

    public EntityManager() {
        this.entities = new HashMap<>();
        this.nextId = 0;
    }

    /**
     * Creates a new entity with a unique ID.
     * The entity starts with no components — add them after creation.
     *
     * @return The newly created entity
     */
    public Entity createEntity() {
        Entity entity = new Entity(nextId);
        entities.put(nextId, entity);
        nextId++;
        return entity;
    }

    /**
     * Destroys an entity immediately and removes it from the manager.
     *
     * @param id The ID of the entity to destroy
     */
    public void destroyEntity(int id) {
        Entity entity = entities.remove(id);
        if (entity != null) {
            entity.destroy();
        }
    }

    /**
     * Gets an entity by its ID.
     *
     * @param id The entity ID
     * @return The entity, or null if not found
     */
    public Entity getEntity(int id) {
        return entities.get(id);
    }

    /**
     * Gets all entities (active and inactive).
     *
     * @return Collection of all entities
     */
    public Collection<Entity> getAllEntities() {
        return entities.values();
    }

    /**
     * Gets all active entities.
     *
     * @return List of entities where isActive() is true
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
     * Gets all active entities that have a specific component type.
     *
     * @param componentType The component type to filter by (class simple name)
     * @return List of matching active entities
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
     * Gets all active entities that have ALL of the specified component types.
     *
     * @param componentTypes The component types to filter by
     * @return List of matching active entities
     */
    public List<Entity> getEntitiesWithComponents(String... componentTypes) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities.values()) {
            if (!entity.isActive()) continue;

            boolean hasAll = true;
            for (String type : componentTypes) {
                if (!entity.hasComponent(type)) {
                    hasAll = false;
                    break;
                }
            }

            if (hasAll) {
                result.add(entity);
            }
        }
        return result;
    }

    /**
     * Update hook called each frame.
     * Currently a placeholder for entity-level updates.
     * Individual component updates happen in their respective managers.
     *
     * @param dt Delta time since last frame
     */
    public void updateAll(float dt) {
        // Hook for entity-level updates
    }

    /**
     * Remove all entities and reset.
     */
    public void clear() {
        for (Entity entity : entities.values()) {
            entity.destroy();
        }
        entities.clear();
    }

    /**
     * Gets the total number of entities.
     *
     * @return Entity count
     */
    public int count() {
        return entities.size();
    }

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id The entity ID
     * @return true if entity exists
     */
    public boolean exists(int id) {
        return entities.containsKey(id);
    }
}