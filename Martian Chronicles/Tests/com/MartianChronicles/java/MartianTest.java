package com.MartianChronicles.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MartianTest {

    @Test
    void setParent() {
        InnovatorMartian<String> Jack = new InnovatorMartian<String>("Jack");
        InnovatorMartian<String> Kelvin = new InnovatorMartian<String>("Kelvin");
        InnovatorMartian<String> Marvin = new InnovatorMartian<String>("Marvin");
        InnovatorMartian<String> Miky = new InnovatorMartian<String>("Miky");
        InnovatorMartian<String> Lola = new InnovatorMartian<String>("Lola");
        InnovatorMartian<String> Voyage = new InnovatorMartian<String>("Voyage");
        InnovatorMartian<String> Keka = new InnovatorMartian<String>("Keka");
        Jack.addChild(Kelvin);
        Jack.addChild(Marvin);
        Kelvin.addChild(Miky);
        Kelvin.addChild(Lola);
        Miky.addChild(Voyage);
        Miky.addChild(Keka);
        Voyage.setParent(Marvin);
        assertNotEquals(Miky, Voyage.getParent());
        assertEquals(Marvin, Voyage.getParent());
    }

    @Test
    void setChildren() {
        InnovatorMartian<String> Jack = new InnovatorMartian<String>("Jack");
        InnovatorMartian<String> Kelvin = new InnovatorMartian<String>("Kelvin");
        InnovatorMartian<String> Marvin = new InnovatorMartian<String>("Marvin");
        InnovatorMartian<String> Miky = new InnovatorMartian<String>("Miky");
        InnovatorMartian<String> Lola = new InnovatorMartian<String>("Lola");
        InnovatorMartian<String> Voyage = new InnovatorMartian<String>("Voyage");
        InnovatorMartian<String> Keka = new InnovatorMartian<String>("Keka");
        Jack.addChild(Kelvin);
        Jack.addChild(Marvin);
        Kelvin.addChild(Miky);
        Kelvin.addChild(Lola);
        Miky.addChild(Voyage);
        Miky.addChild(Keka);
        Marvin.setChildren(Miky.getChildren());
        assertEquals(0, Miky.getChildren().size());
        assertEquals(2, Marvin.getChildren().size());
    }

    @Test
    void addChild() {
        InnovatorMartian<String> Jack = new InnovatorMartian<String>("Jack");
        InnovatorMartian<String> Kelvin = new InnovatorMartian<String>("Kelvin");
        InnovatorMartian<String> Marvin = new InnovatorMartian<String>("Marvin");
        InnovatorMartian<String> Miky = new InnovatorMartian<String>("Miky");
        InnovatorMartian<String> Lola = new InnovatorMartian<String>("Lola");
        InnovatorMartian<String> Voyage = new InnovatorMartian<String>("Voyage");
        InnovatorMartian<String> Keka = new InnovatorMartian<String>("Keka");
        Jack.addChild(Kelvin);
        Jack.addChild(Marvin);
        Kelvin.addChild(Miky);
        Kelvin.addChild(Lola);
        Miky.addChild(Voyage);
        Miky.addChild(Keka);
        Marvin.addChild(Voyage);
        assertEquals(1, Miky.getChildren().size());
        assertEquals(1, Marvin.getChildren().size());
    }

    @Test
    void deleteChild() {
        InnovatorMartian<String> Jack = new InnovatorMartian<String>("Jack");
        InnovatorMartian<String> Kelvin = new InnovatorMartian<String>("Kelvin");
        InnovatorMartian<String> Marvin = new InnovatorMartian<String>("Marvin");
        InnovatorMartian<String> Miky = new InnovatorMartian<String>("Miky");
        InnovatorMartian<String> Lola = new InnovatorMartian<String>("Lola");
        InnovatorMartian<String> Voyage = new InnovatorMartian<String>("Voyage");
        InnovatorMartian<String> Keka = new InnovatorMartian<String>("Keka");
        Jack.addChild(Kelvin);
        Jack.addChild(Marvin);
        Kelvin.addChild(Miky);
        Kelvin.addChild(Lola);
        Miky.addChild(Voyage);
        Miky.addChild(Keka);
        Miky.deleteChild(Voyage);
        assertEquals(1, Miky.getChildren().size());
    }
}