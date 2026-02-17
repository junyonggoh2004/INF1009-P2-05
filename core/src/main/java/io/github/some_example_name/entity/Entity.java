package io.github.some_example_name.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Entity {

    /** Unique id for a specific entity */
    private final int id;

    /** Whether this entity is active in the game world */
    private boolean active;

    /**
     Map of component class simple name -> component instance.
     Key is derived from getClass().getSimpleName() when adding.
     */
    private final Map<String, Component> components;

    /**
     Creates a new entity with the given ID.
     Entities should be created through EntityManager, not directly.
     @param id Unique identifier assigned by EntityManager
     */
    public Entity(int id) {
        this.id = id;
        this.active = true;
        this.components = new HashMap<>();
    }

    /**
     Adds a component to this entity.
     The component is keyed by its class simple name.
     If a component of the same type already exists, it will be replaced.
     @param component The component to add
     */
    public void add(Component component) {
        if (component == null) return;
        components.put(component.getClass().getSimpleName(), component);
    }

    /**
     * Gets a component by its type identifier (class simple name).
     *
     * @param type The component type (e.g., "Collider")
     * @return The component, or null if not found
     */
    public Component getComponent(String type) {
        return components.get(type);
    }

    /**
     Gets a component with automatic type casting.
     Supports interface lookups — e.g., getComponent(CollisionHandler.class)
     will find a PrintCollisionHandler stored on the entity.
     @param type The class or interface to search for
     @param <T> The component type
     @return The matching component, or null if not found
     */
    public <T> T getComponent(Class<T> type) {
        for (Component c : components.values()) {
            if (type.isInstance(c)) {
                return type.cast(c);
            }
        }
        return null;
    }

    /**
     Checks if this entity has a component of the specified type (by string key).
     @param type The component type to check for
     @return true if the component exists
     */
    public boolean hasComponent(String type) {
        return components.containsKey(type);
    }

    /**
     Checks if this entity has a component assignable to the given class/interface.
     @param type The class or interface to check for
     @param <T> The type
     @return true if a matching component exists
     */
    public <T> boolean hasComponent(Class<T> type) {
        for (Component c : components.values()) {
            if (type.isInstance(c)) return true;
        }
        return false;
    }

    /**
     Removes a component from this entity by its string key.
     @param type The type of component to remove
     */
    public void removeComponent(String type) {
        components.remove(type);
    }

    /**
     Gets all components attached to this entity.
    @return Collection of all components
     */
    public Collection<Component> getAllComponents() {
        return components.values();
    }


    /**
     Gets the unique ID of this entity.
     IDs are assigned by EntityManager and never change.
     @return The entity's unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     Checks if this entity is active.
     Inactive entities are typically skipped by all systems.
     @return true if active
     */
    public boolean isActive() {
        return active;
    }

    /**
     Enables or disables this entire entity.
     When disabled, all systems should skip this entity.
     @param active true to enable, false to disable
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     Cleans up this entity by removing all components.
     Called by EntityManager when destroying an entity.
     */
    public void destroy() {
        active = false;
        components.clear();
    }

    @Override
    public String toString() {
        return "Entity{id=" + id + ", active=" + active + ", components=" + components.keySet() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}