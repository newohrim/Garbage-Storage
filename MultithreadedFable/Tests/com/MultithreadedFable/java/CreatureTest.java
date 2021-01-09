package com.MultithreadedFable.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatureTest {

    @Test
    void getPosition() {
        Creature Lebed = new Creature(60, "Lebed");
        Creature Rak = new Creature(180, "Rak");
        Creature Shuka = new Creature(300, "Shuka");
        Dray dray = new Dray(Vector2.ZERO, Lebed, Rak, Shuka);
        assertEquals(dray.getPosition(), Lebed.getPosition());
        assertEquals(dray.getPosition(), Rak.getPosition());
        assertEquals(dray.getPosition(), Shuka.getPosition());
    }

    @Test
    void getName() {
        Creature Lebed = new Creature(60, "Lebed");
        Creature Rak = new Creature(180, "Lebed");
        assertEquals(Lebed.getName(), Rak.getName());
    }
}