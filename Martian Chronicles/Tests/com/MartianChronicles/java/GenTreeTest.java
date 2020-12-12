package com.MartianChronicles.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenTreeTest {

    @Test
    void makeMartianConservative() {
        String expected = "ConservatorMartian(String:Jack)\n" +
                "    ConservatorMartian(String:Kelvin)\n" +
                "        ConservatorMartian(String:Miky)\n" +
                "            ConservatorMartian(String:Voyage)\n" +
                "            ConservatorMartian(String:Keka)\n" +
                "        ConservatorMartian(String:Lola)\n" +
                "    ConservatorMartian(String:Marvin)\n";
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
        GenTree<String> tree = new GenTree<String>(Jack);
        tree.MakeMartianConservative(Jack);
        assertEquals(expected, tree.toString());
    }

    @Test
    void fromStringToobject() {
        String input = "InnovatorMartian(String:Jack)\n" +
                "    InnovatorMartian(String:Kelvin)\n" +
                "        InnovatorMartian(String:Miky)\n" +
                "            InnovatorMartian(String:Voyage)\n" +
                "            ConservatorMartian(String:Keka)\n" +
                "        InnovatorMartian(String:Lola)\n" +
                "    InnovatorMartian(String:Marvin)\n";
        String expected = "ConservatorMartian(String:Jack)\n" +
                "    ConservatorMartian(String:Kelvin)\n" +
                "        ConservatorMartian(String:Miky)\n" +
                "            ConservatorMartian(String:Voyage)\n" +
                "            ConservatorMartian(String:Keka)\n" +
                "        ConservatorMartian(String:Lola)\n" +
                "    ConservatorMartian(String:Marvin)\n";
        GenTree<String> tree = new GenTree<String>(input);
        assertEquals(expected, tree.toString());
    }

    @Test
    void makeTreeInteger() {
        String expected = "ConservatorMartian(Integer:0)\n" +
                "    ConservatorMartian(Integer:1)\n" +
                "        ConservatorMartian(Integer:2)\n" +
                "            ConservatorMartian(Integer:3)\n" +
                "            ConservatorMartian(Integer:4)\n" +
                "        ConservatorMartian(Integer:5)\n" +
                "    ConservatorMartian(Integer:6)\n";
        InnovatorMartian<Integer> Jack = new InnovatorMartian<Integer>(0);
        InnovatorMartian<Integer> Kelvin = new InnovatorMartian<Integer>(1);
        InnovatorMartian<Integer> Marvin = new InnovatorMartian<Integer>(6);
        InnovatorMartian<Integer> Miky = new InnovatorMartian<Integer>(2);
        InnovatorMartian<Integer> Lola = new InnovatorMartian<Integer>(5);
        InnovatorMartian<Integer> Voyage = new InnovatorMartian<Integer>(3);
        InnovatorMartian<Integer> Keka = new InnovatorMartian<Integer>(4);
        Jack.addChild(Kelvin);
        Jack.addChild(Marvin);
        Kelvin.addChild(Miky);
        Kelvin.addChild(Lola);
        Miky.addChild(Voyage);
        Miky.addChild(Keka);
        GenTree<String> tree = new GenTree<String>(Jack);
        tree.MakeMartianConservative(Jack);
        assertEquals(expected, tree.toString());
    }

    @Test
    void fromStringToobjectInteger() {
        String input = "InnovatorMartian(Integer:0)\n" +
                "    InnovatorMartian(Integer:1)\n" +
                "        InnovatorMartian(Integer:2)\n" +
                "            InnovatorMartian(Integer:3)\n" +
                "            ConservatorMartian(Integer:4)\n" +
                "        InnovatorMartian(Integer:5)\n" +
                "    InnovatorMartian(Integer:6)\n";
        String expected = "ConservatorMartian(Integer:0)\n" +
                "    ConservatorMartian(Integer:1)\n" +
                "        ConservatorMartian(Integer:2)\n" +
                "            ConservatorMartian(Integer:3)\n" +
                "            ConservatorMartian(Integer:4)\n" +
                "        ConservatorMartian(Integer:5)\n" +
                "    ConservatorMartian(Integer:6)\n";
        GenTree<String> tree = new GenTree<String>(input);
        assertEquals(expected, tree.toString());
    }
}