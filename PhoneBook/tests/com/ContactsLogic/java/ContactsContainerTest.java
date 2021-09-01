package com.ContactsLogic.java;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContactsContainerTest {

    private static final String pathToCSV = "contacts.csv";
    private static final String testConnectionUrl = "jdbc:derby:memory:myDB;create=true";
    private static final String clearDB = "DELETE FROM CONTACTS";
    private static final LocalDate testDate = LocalDate.now();

    private static Connection establishConnection() throws SQLException
    {
        return DriverManager.getConnection(testConnectionUrl);
    }

    private static void clearDataBase(Connection connection) throws SQLException
    {
        Statement statement = connection.createStatement();
        statement.execute(clearDB);
        statement.close();
    }

    private static ContactsContainer getContainer()
    {
        ContactsContainer contactsContainer = new ContactsContainer();
        try
        {
            contactsContainer.importFromCSV(pathToCSV);
        }
        catch (IOException | SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        return contactsContainer;
    }

    @Test
    void getContacts()
    {
        ContactsContainer container = new ContactsContainer();
        assertNotNull(container.getContacts());
    }

    @Test
    void addContact() throws SQLException {
        ContactsContainer container = new ContactsContainer();
        Connection currentConnection = establishConnection();
        container.initDB(currentConnection);
        assertTrue(container.addContact(new Contact(
                "Testov",
                "Test",
                "Testovich",
                "1111test",
                "0",
                "1",
                testDate,
                "info"
        )));
        assertTrue(container.isMerged());
        assertFalse(container.addContact(new Contact(
                "Testov",
                "Test",
                "Testovich",
                "2222test",
                "",
                "",
                null,
                ""
        )));
        assertFalse(container.isMerged());
        Pair<Contact, Contact> notMergedPair = container.nextNotMerged();
        assertNotNull(notMergedPair);
        assertTrue(container.isMerged());
        assertTrue(container.addContact(new Contact(
                "Chekhov",
                "Anton",
                "Pavlovich",
                "3333test",
                "test3333",
                "Chekhov street, 1",
                LocalDate.of(1860, 1, 29),
                "russian author"
        )));
        assertTrue(container.isMerged());
        clearDataBase(currentConnection);
        //container.closeDB();
    }

    @Test
    void removeContact() throws SQLException {
        ContactsContainer container = new ContactsContainer();
        Connection currentConnection = establishConnection();
        container.initDB(currentConnection);
        Contact contact = new Contact(
                "Testov",
                "Test",
                "Testovich",
                "1111test",
                "0",
                "1",
                testDate,
                "info"
        );
        assertTrue(container.addContact(contact));
        assertTrue(container.isMerged());
        assertTrue(container.removeContact(contact));
        assertFalse(container.removeContact(contact));
        clearDataBase(currentConnection);
        //container.closeDB();
    }

    @Test
    void importFromCSV() throws SQLException, IOException {
        ContactsContainer container = new ContactsContainer();
        Connection currentConnection = establishConnection();
        container.initDB(currentConnection);
        container.importFromCSV(pathToCSV);
        int size = container.getContacts().size();
        // already full of contacts
        try
        {
            container.importFromCSV(pathToCSV);
        }
        catch (IOException | SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        assertFalse(container.isMerged());
        assertEquals(size, container.getContacts().size());
        int i = 0;
        while (!container.isMerged())
        {
            assertNotNull(container.nextNotMerged());
            i++;
        }
        assertEquals(size, i);
        clearDataBase(currentConnection);
        //container.closeDB();
    }

    @Test
    void filterByNames() throws SQLException {
        ContactsContainer container = new ContactsContainer();
        Connection currentConnection = establishConnection();
        container.initDB(currentConnection);
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
        assertTrue(container.addContact(chekhov));
        List<Contact> filteredList1 = container.filterByNames("hekHov");
        List<Contact> filteredList2 = container.filterByNames("ntoN");
        List<Contact> filteredList3 = container.filterByNames("pavlov");
        assertEquals(1, filteredList1.size());
        assertEquals(1, filteredList2.size());
        assertEquals(1, filteredList3.size());
        assertTrue(container.removeContact(filteredList3.get(0)));
        clearDataBase(currentConnection);
        //container.closeDB();
    }

    @Test
    void importContactsDB() throws SQLException
    {
        ContactsContainer container = new ContactsContainer();
        container.initDB();
        container.importFromDB();
    }
}