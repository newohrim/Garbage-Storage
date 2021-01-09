package com.MultithreadedFable.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Vector2Test {

    @Test
    void getX() {
        Vector2 vec = new Vector2(10.5, -8.5);
        assertEquals(10.5, vec.getX());
    }

    @Test
    void getY() {
        Vector2 vec = new Vector2(10.5, -8.5);
        assertEquals(-8.5, vec.getY());
    }

    @Test
    void testEquals() {
        Vector2 vec = new Vector2(10.5, -8.5);
        Vector2 temp = new Vector2(10.5, -8.5);
        assertTrue(vec.equals(temp));
        assertTrue(temp.equals(vec));
        vec = new Vector2(0, -8.5);
        assertFalse(vec.equals(temp));
        assertFalse(temp.equals(vec));
    }
}