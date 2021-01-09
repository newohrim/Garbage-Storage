package com.MultithreadedFable.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DrayTest {

    @Test
    void basicTest() {
        Creature Lebed = new Creature(60, "Lebed");
        Creature Rak = new Creature(180, "Rak");
        Creature Shuka = new Creature(300, "Shuka");
        Dray dray = new Dray(Vector2.ZERO, Lebed, Rak, Shuka);
        assertFalse(dray.isAllowedToInteract());
        dray.begin();
        assertFalse(dray.isAllowedToInteract());
        assertFalse(dray.isPulling());
    }

    @Test
    void nullTest() {
        Creature Lebed = new Creature(60, "Lebed");
        Creature Rak = null;
        Creature Shuka = new Creature(300, "Shuka");
        assertThrows(NullPointerException.class, () -> new Dray(Vector2.ZERO, Lebed, Rak, Shuka));
        assertThrows(NullPointerException.class, () -> new Dray(null));
    }

    @Test
    void twiceBegin() {
        Creature Lebed = new Creature(60, "Lebed");
        Creature Rak = new Creature(180, "Rak");
        Creature Shuka = new Creature(300, "Shuka");
        Dray dray = new Dray(Vector2.ZERO, Lebed, Rak, Shuka);
        dray.begin();
        dray.begin();
    }

    @Test
    void modifiedTest() {
        Creature[] politicalCoords =
        {
            new Creature(0, "Radical Right"),
            new Creature(10, "Liberal Conservatism"),
            new Creature(20, "Neoconservatism"),
            new Creature(30, "Conservatism"),
            new Creature(30, "NationalConservatism"),
            new Creature(45, "Traditionalism"),
            new Creature(70, "Fascism"),
            new Creature(80, "NationalSocialism"),
            new Creature(90, "Authoritarianism"),
            new Creature(100, "Strasserism"),
            new Creature(120, "NationalBolshevism"),
            new Creature(135, "Bolshevism"),
            new Creature(155, "AuthoritarianSocialist"),
            new Creature(165, "Socialism"),
            new Creature(180, "Radical Left"),
            new Creature(190, "Democratic Socialism"),
            new Creature(205, "Utopian Socialism"),
            new Creature(225, "Anarcho-Communism"),
            new Creature(235, "Anarcho-Sindicalism"),
            new Creature(245, "Mutoelism"),
            new Creature(260, "Left Anarchism"),
            new Creature(270, "Liberalism"),
            new Creature(280, "Right Anarchism"),
            new Creature(290, "Anarcho-Individualism"),
            new Creature(315, "Anarcho-Capitalism"),
            new Creature(335, "Minarchism"),
            new Creature(345, "Liberal Conservatism")
        };
        Dray dray = new Dray(Vector2.ZERO, politicalCoords);
        assertFalse(dray.isAllowedToInteract());
        dray.begin();
        assertFalse(dray.isAllowedToInteract());
        assertFalse(dray.isPulling());
    }
}