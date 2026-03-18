package io.github.some_example_name.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    /** Simple test component */
    static class TestComponent implements Component { }
    static class AnotherComponent implements Component { }

    /** Interface test for getComponent(Class) lookup */
    interface Taggable extends Component { }
    static class TagImpl implements Taggable { }

    @Test
    void entityStartsActive() {
        Entity e = new Entity(1);
        assertTrue(e.isActive());
        assertEquals(1, e.getId());
    }

    @Test
    void addAndGetComponentByClass() {
        Entity e = new Entity(1);
        TestComponent tc = new TestComponent();
        e.add(tc);
        assertSame(tc, e.getComponent(TestComponent.class));
    }

    @Test
    void addAndGetComponentByString() {
        Entity e = new Entity(1);
        TestComponent tc = new TestComponent();
        e.add(tc);
        assertSame(tc, e.getComponent("TestComponent"));
    }

    @Test
    void getComponentReturnsNullWhenMissing() {
        Entity e = new Entity(1);
        assertNull(e.getComponent(TestComponent.class));
        assertNull(e.getComponent("TestComponent"));
    }

    @Test
    void hasComponentByClass() {
        Entity e = new Entity(1);
        assertFalse(e.hasComponent(TestComponent.class));
        e.add(new TestComponent());
        assertTrue(e.hasComponent(TestComponent.class));
    }

    @Test
    void hasComponentByString() {
        Entity e = new Entity(1);
        assertFalse(e.hasComponent("TestComponent"));
        e.add(new TestComponent());
        assertTrue(e.hasComponent("TestComponent"));
    }

    @Test
    void interfaceLookupFindsImplementation() {
        Entity e = new Entity(1);
        TagImpl impl = new TagImpl();
        e.add(impl);
        assertSame(impl, e.getComponent(Taggable.class));
        assertTrue(e.hasComponent(Taggable.class));
    }

    @Test
    void removeComponent() {
        Entity e = new Entity(1);
        e.add(new TestComponent());
        e.removeComponent("TestComponent");
        assertFalse(e.hasComponent("TestComponent"));
    }

    @Test
    void destroyClearsComponents() {
        Entity e = new Entity(1);
        e.add(new TestComponent());
        e.add(new AnotherComponent());
        e.destroy();
        assertFalse(e.isActive());
        assertTrue(e.getAllComponents().isEmpty());
    }

    @Test
    void setActiveTogglesState() {
        Entity e = new Entity(1);
        e.setActive(false);
        assertFalse(e.isActive());
        e.setActive(true);
        assertTrue(e.isActive());
    }

    @Test
    void replaceComponentOfSameType() {
        Entity e = new Entity(1);
        TestComponent first = new TestComponent();
        TestComponent second = new TestComponent();
        e.add(first);
        e.add(second);
        assertSame(second, e.getComponent(TestComponent.class));
    }

    @Test
    void equalityBasedOnId() {
        Entity a = new Entity(5);
        Entity b = new Entity(5);
        Entity c = new Entity(6);
        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}
