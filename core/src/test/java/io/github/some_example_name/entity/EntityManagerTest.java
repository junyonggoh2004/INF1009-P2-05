package io.github.some_example_name.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityManagerTest {

    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = new EntityManager();
    }

    @Test
    void createEntityReturnsUniqueIds() {
        Entity a = em.createEntity();
        Entity b = em.createEntity();
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void getEntityByIdReturnsCorrectEntity() {
        Entity a = em.createEntity();
        assertSame(a, em.getEntity(a.getId()));
    }

    @Test
    void destroyEntityRemovesIt() {
        Entity a = em.createEntity();
        int id = a.getId();
        em.destroyEntity(id);
        assertFalse(em.exists(id));
    }

    @Test
    void getActiveEntitiesExcludesInactive() {
        Entity a = em.createEntity();
        Entity b = em.createEntity();
        b.setActive(false);
        assertEquals(1, em.getActiveEntities().size());
        assertSame(a, em.getActiveEntities().get(0));
    }

    @Test
    void clearRemovesAllEntities() {
        em.createEntity();
        em.createEntity();
        em.clear();
        assertEquals(0, em.count());
    }

    @Test
    void countTracksEntities() {
        assertEquals(0, em.count());
        em.createEntity();
        assertEquals(1, em.count());
        em.createEntity();
        assertEquals(2, em.count());
    }

    @Test
    void existsReturnsFalseForUnknownId() {
        assertFalse(em.exists(999));
    }

    @Test
    void getEntitiesWithComponentFindsCorrectEntities() {
        Entity a = em.createEntity();
        Entity b = em.createEntity();
        a.add(new Sprite("test", 10, 10));
        assertEquals(1, em.getEntitiesWithComponent("Sprite").size());
    }
}
