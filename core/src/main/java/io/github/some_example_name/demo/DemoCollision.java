package io.github.some_example_name.demo;

import io.github.some_example_name.entity.*;
import io.github.some_example_name.movement.*;
import io.github.some_example_name.collision.*;

public class DemoCollision {

    public static void main(String[] args) {

        EntityManager em = new EntityManager();

        MovementManager mm = new MovementManager();
        mm.init();

        CollisionManager cm = new CollisionManager();
        cm.init(mm, 3);

        //inject resolver (demo layer)
        cm.setResolver(new DebugResolver());

        Entity a = em.createEntity();
        Entity b = em.createEntity();

        a.add(Collider.rect(50, 50, 0, 0, 0, false));
        b.add(Collider.rect(50, 50, 0, 0, 0, false));

        a.add(new PrintCollisionHandler());

        mm.register(a.getId(), new Transform(0, 0), new Motion(60, 0));
        mm.register(b.getId(), new Transform(200, 0), new Motion(0, 0));

        float dt = 0.016f;

        for (int frame = 0; frame < 200; frame++) {
            mm.update(dt, em);
            cm.update(dt, em);

            Transform ta = mm.getTransform(a.getId());
            Transform tb = mm.getTransform(b.getId());

            System.out.printf("Frame %d | A(%.1f) B(%.1f)%n",
                    frame, ta.getX(), tb.getX());
        }

        cm.dispose();
        mm.dispose();
    }
}
