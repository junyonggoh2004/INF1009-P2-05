
package io.github.some_example_name.demo;

import io.github.some_example_name.entity.*;
import io.github.some_example_name.movement.*;
import io.github.some_example_name.collision.*;

public class DemoCollision {

    public static void main(String[] args) {

        // Create managers
        EntityManager em = new EntityManager();

        MovementManager mm = new MovementManager();
        mm.init();

        CollisionManager cm = new CollisionManager();
        cm.init(mm, 3); // 3 layers for example

        // Create two entities
        Entity a = em.createEntity();
        Entity b = em.createEntity();

        // Add components 
        a.add(Collider.rect(50, 50, 0, 0, 0, false));
        b.add(Collider.rect(50, 50, 0, 0, 0, false));

        a.add(new PrintCollisionHandler());
        b.add(new PrintCollisionHandler());

        // Register movement components
        mm.register(a.getId(), new Transform(0, 0), new Motion(60, 0));  // moves right
        mm.register(b.getId(), new Transform(200, 0), new Motion(0, 0)); // stationary

        // Simulate frames
        float dt = 0.016f; // ~60 FPS

        for (int frame = 0; frame < 200; frame++) {

            mm.update(dt, em);   // update movement
            cm.update(dt, em);   // detect collision

            Transform ta = mm.getTransform(a.getId());
            Transform tb = mm.getTransform(b.getId());

            System.out.printf("Frame %d | A(%.1f) B(%.1f)%n",
                    frame, ta.getX(), tb.getX());
        }

        cm.dispose();
        mm.dispose();
    }
}
