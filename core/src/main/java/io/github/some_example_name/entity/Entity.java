
package io.github.some_example_name.entity;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private final int id;
    private boolean active = true;

    // String key → Component 
    private final Map<String, Component> components = new HashMap<>();


    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void destroy() {
        active = false;
        components.clear();
    }

    public void add(Component c) {
        if (c == null) return;
        components.put(c.getClass().getSimpleName(), c);
    }

    public Component getComponent(String type) {
        return components.get(type);
    }

    public boolean hasComponent(String type) {
        return components.containsKey(type);
    }

    public void removeComponent(String type) {
        components.remove(type);
    }

 // Typed lookup that supports interfaces  (CollisionHandler.class works even if stored as PrintCollisionHandler)
    public <T> T getComponent(Class<T> type) {
        for (Component c : components.values()) {
            if (type.isInstance(c)) {
                return type.cast(c);
            }
        }
        return null;
    }

    public <T> boolean hasComponent(Class<T> type) {
        for (Component c : components.values()) {
            if (type.isInstance(c)) return true;
        }
        return false;
    }

}
