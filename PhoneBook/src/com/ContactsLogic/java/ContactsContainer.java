package com.ContactsLogic.java;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ContactsContainer {
    /**
     * Список контактов, содержащихся в контейнере.
     */
    private ArrayList<Contact> contacts;
    /**
     * Список импортированных контактов, вызвавших конфликт.
     */
    private Queue<Pair<Contact, Contact>> mergeList;
    /**
     * База данных, привязанная к контейнеру.
     */
    private ContactsDataBase contactsDataBase;

    /**
     * Возвращает текущий список контактов.
     * @return Коллекция контактов.
     */
    public Collection<Contact> getContacts() { return contacts; }

    /**
     * Возвращает следующий неслившийся после импортирования контакт из очереди слияния.
     * @return Конфликтующую пару импортированный контакт/контакт в контейнере.
     */
    public Pair<Contact, Contact> nextNotMerged() { return mergeList.poll(); }

    /**
     * Проверяет прошло ли добавление контактов без конфликтов.
     * @return true - если конфликтов не было, false - иначе.
     */
    public boolean isMerged() { return mergeList.isEmpty(); }

    public ContactsContainer ()
    {
        contacts = new ArrayList<Contact>();
        mergeList = new ArrayDeque<>();
        contactsDataBase = new ContactsDataBase();
    }

    /**
     * Добавляет контакт в контейнер.
     * @param contact Добавляемый контакт.
     * @return всегда true.
     * @throws NullPointerException Передаваемый контакт оказался null.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public boolean addContact(final Contact contact) throws NullPointerException, SQLException
    {
        if (contact == null)
            throw new NullPointerException("Contact was null.");
        for (Contact c : contacts)
        {
            if (c.compareByNames(contact))
            {
                mergeList.offer(new Pair<>(contact, c));
                return false;
            }
        }
        contactsDataBase.insertContact(contact);
        contacts.add(contact);
        return true;
    }

    /**
     * Удаляет контакт из контейнера.
     * @param contact Удаляемый контакт.
     * @return true - если контейнер содержит контакт, false - иначе.
     * @throws NullPointerException Передаваемый контакт оказался null.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public boolean removeContact(final Contact contact) throws NullPointerException, SQLException
    {
        if (contact == null)
            throw new NullPointerException("Contact was null.");
        contactsDataBase.removeContact(contact);
        return contacts.remove(contact);
    }

    /**
     * Импортирует список контактов из CSV файла (разделитель - ;).
     * @param pathToCSV Путь к файлу.
     * @throws IOException Ошибка чтения файла.
     * @throws IndexOutOfBoundsException Ошибка форматирования файла.
     * @throws DateTimeParseException Ошибка формата даты.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void importFromCSV(final String pathToCSV)
            throws IOException, IndexOutOfBoundsException, DateTimeParseException, SQLException
    {
        mergeList.clear();
        BufferedReader br = new BufferedReader(new FileReader(pathToCSV));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            addContact(new Contact(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    parts[4],
                    parts[5],
                    parts[6].isBlank() ? null : LocalDate.parse(parts[6].trim()),
                    parts[7]
            ));
        }
    }

    /**
     * Экспортирует список контактов в контейнере в CSV файл (разделитель - ;).
     * @param pathToCSV Путь к файлу.
     * @throws IOException Ошибка записи в файл.
     */
    public void exportToCSV(final String pathToCSV) throws IOException
    {
        PrintWriter writer = new PrintWriter(pathToCSV, StandardCharsets.UTF_8);
        for (Contact c : contacts)
            writer.println(c.toString());
        writer.close();
    }

    /**
     * Фильтрует контакты по части Фамилии или Имени или Имени отчества.
     * @param partOfName Часть имя.
     * @return Список контактов подходящих под запрос.
     */
    public List<Contact> filterByNames(final String partOfName)
    {
        String lowerTrimmedName = partOfName.trim().toLowerCase();
        return contacts.stream().filter((Contact c) ->
                c.getLastName().toLowerCase().contains(lowerTrimmedName) ||
                c.getFirstName().toLowerCase().contains(lowerTrimmedName) ||
                c.getThirdName().toLowerCase().contains(lowerTrimmedName)).collect(Collectors.toList());
    }

    /**
     * Импортирует список контактов из привязанной базы данных.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void importFromDB() throws SQLException
    {
        ArrayList<Contact> importedContacts = contactsDataBase.importContacts();
        for(Contact c : importedContacts)
        {
            if (c.validate() && isUnique(c))
                contacts.add(c);
        }
    }

    /**
     * Инициализирует привязанную базу данных.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void initDB() throws SQLException
    {
        contactsDataBase.establishConnection();
    }

    /**
     * Инициализирует передаваемую в параметре базу данных (для теста).
     * @param connection Подключение к базе данных.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void initDB(Connection connection) throws SQLException
    {
        contactsDataBase.establishConnection(connection);
    }

    /**
     * Закрывает соединение с привязанной базой данных.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void closeDB() throws SQLException
    {
        contactsDataBase.closeConnection();
    }

    /**
     * Проверяет является ли контакт уникальным по ФИО для контейнера.
     * @param contact
     * @return
     */
    private boolean isUnique(final Contact contact)
    {
        for (Contact c : contacts)
        {
            if (contact.compareByNames(c))
                return false;
        }
        return true;
    }
}
