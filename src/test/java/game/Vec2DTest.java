package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class Vec2DTest {

    @Test
    public void add() {
        Vec2D v1 = new Vec2D(-10, 14);
        Vec2D v2 = new Vec2D(-2, -3);
        Vec2D v = v1.add(v2);

        assertEquals(-12, v.x);
        assertEquals(11, v.y);
    }

    @Test
    public void sub() {
        Vec2D v1 = new Vec2D(-10, 14);
        Vec2D v2 = new Vec2D(-2, -3);
        Vec2D v = v1.sub(v2);

        assertEquals(-8, v.x);
        assertEquals(17, v.y);
    }

    @Test
    public void mulVec2D() {
        Vec2D v1 = new Vec2D(-10, 14);
        Vec2D v2 = new Vec2D(-2, -3);
        Vec2D v = v1.mul(v2);

        assertEquals(20, v.x);
        assertEquals(-42, v.y);
    }

    @Test
    public void mulInteger() {
        Vec2D v1 = new Vec2D(-10, 14);
        Vec2D v = v1.mul(10);

        assertEquals(-100, v.x);
        assertEquals(140, v.y);
    }

    @Test
    public void equals() {
        Vec2D v1 = new Vec2D(-10, 14);
        Vec2D v2 = new Vec2D(-10, 14);
        assertTrue(v1.equals(v1));
        assertTrue(v1.equals(v2));
        assertTrue(!v1.equals(null));
        assertTrue(!v1.equals(new Vec2D(0, 0)));
        assertTrue(v1.equals(v1.clone()));
    }

    @Test
    public void cloneVec2D() {
        Vec2D v1 = new Vec2D(-10, 14);
        Vec2D clone = v1.clone();
        clone.x = 99;
        assertEquals(-10, v1.x);
    }
}
