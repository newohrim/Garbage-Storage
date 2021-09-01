package com.ContactsLogic.java;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void replaceBy()
    {
        Contact chekhov = new Contact(
                "Chekhov",
                "Anton",
                "Pavlovich",
                "3333test",
                "test3333",
                "Chekhov street, 1",
                LocalDate.of(1860, 1, 29),
                "russian author"
        );
        Contact tolstoy = new Contact(
                "Tolstoy",
                "Lev",
                "Nikolaevich",
                "4444test",
                "",
                "Tolstoy street, 9",
                LocalDate.of(1828, 9, 9),
                "russian author 2"
        );
        chekhov.replaceBy(tolstoy);
        assertEquals(tolstoy.getLastName(), chekhov.getLastName());
        assertEquals(tolstoy.getFirstName(), chekhov.getFirstName());
        assertEquals(tolstoy.getThirdName(), chekhov.getThirdName());
        assertEquals(tolstoy.getMobilePhoneNumber(), chekhov.getMobilePhoneNumber());
        assertEquals(tolstoy.getHomePhoneNumber(), chekhov.getHomePhoneNumber());
        assertEquals(tolstoy.getAddress(), chekhov.getAddress());
        assertEquals(tolstoy.getBirthdayDate(), chekhov.getBirthdayDate());
        assertEquals(tolstoy.getAdditionalInfo(), chekhov.getAdditionalInfo());
    }

    @Test
    void compareByNames()
    {
        Contact chekhov1 = new Contact(
                "Chekhov",
                "Anton",
                "Pavlovich",
                "3333test",
                "test3333",
                "Chekhov street, 1",
                LocalDate.of(1860, 1, 29),
                "russian author"
        );
        Contact chekhov2 = new Contact(
                "chekhov",
                "anton",
                "pavlovich",
                "",
                "",
                "",
                null,
                ""
        );
        Contact tolstoy = new Contact(
                "Tolstoy",
                "Lev",
                "Nikolaevich",
                "4444test",
                "",
                "Tolstoy street, 9",
                LocalDate.of(1828, 9, 9),
                "russian author 2"
        );
        assertFalse(chekhov1.compareByNames(tolstoy));
        assertTrue(chekhov1.compareByNames(chekhov2));
        assertTrue(chekhov1.compareByNames(chekhov1));
        assertTrue(tolstoy.compareByNames(tolstoy));
    }

    @Test
    void validate()
    {
        Contact chekhov = new Contact(
                "Chekhov",
                "Anton",
                "Pavlovich",
                "3333test",
                "test3333",
                "Chekhov street, 1",
                LocalDate.of(1860, 1, 29),
                "russian author"
        );
        assertTrue(chekhov.validate());
        Contact chekhovNull = new Contact(
                "",
                "",
                "",
                "",
                "",
                "",
                null,
                ""
        );
        assertFalse(chekhovNull.validate());
        Contact chekhov2 = new Contact(
                "C",
                "A",
                "",
                "",
                "",
                "",
                null,
                ""
        );
        assertFalse(chekhov2.validate());
        Contact chekhov3 = new Contact(
                "C",
                "A",
                "",
                "",
                "1234",
                "",
                null,
                ""
        );
        assertTrue(chekhov3.validate());
    }

    @Test
    void testTrim()
    {
        Contact chekhov = new Contact(
                "       Chekhov           ",
                "       Anton             ",
                "       Pavlovich            ",
                "   3333test   ",
                " test3333     ",
                "         Chekhov street, 1      ",
                LocalDate.of(1860, 1, 29),
                "      russian author        "
        );
        assertEquals("Chekhov", chekhov.getLastName());
        assertEquals("Anton", chekhov.getFirstName());
        assertEquals("Pavlovich", chekhov.getThirdName());
        assertEquals("3333test", chekhov.getMobilePhoneNumber());
        assertEquals("test3333", chekhov.getHomePhoneNumber());
        assertEquals("Chekhov street, 1", chekhov.getAddress());
        assertEquals("russian author", chekhov.getAdditionalInfo());
    }

    @Test
    void testToString()
    {
        Contact chekhov = new Contact(
                "Chekhov",
                "Anton",
                "Pavlovich",
                "3333test",
                "test3333",
                "Chekhov street, 1",
                LocalDate.of(1860, 1, 29),
                "russian author"
        );
        assertEquals("Chekhov;Anton;Pavlovich;3333test;test3333;Chekhov street, 1;1860-01-29;russian author;",
                chekhov.toString());
    }
}