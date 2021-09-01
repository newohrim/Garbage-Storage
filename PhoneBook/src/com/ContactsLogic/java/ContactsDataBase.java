package com.ContactsLogic.java;

import java.sql.*;
import java.util.ArrayList;

public class ContactsDataBase
{
    /**
     * Embedded driver
     */
    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    /**
     * Название базы данных
     */
    private static final String dbName = "DERBY\\contactsDB";
    /**
     * Команда для инициализации базы данных
     */
    private static final String connectionURL = "jdbc:derby:" + dbName + ";create=true";
    /**
     * Команда для создания таблицы CONTACTS
     */
    private static final String createString = "CREATE TABLE CONTACTS " +
            "(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT CONTACTS_PK PRIMARY KEY, " +
            "LAST_NAME VARCHAR(32) NOT NULL, " +
            "FIRST_NAME VARCHAR(32) NOT NULL, " +
            "THIRD_NAME VARCHAR(32), " +
            "MOBILE_PHONE VARCHAR(32), " +
            "HOME_PHONE VARCHAR(32), " +
            "ADDRESS VARCHAR(32), " +
            "BIRTHDAY_DATE DATE, " +
            "ADDITIONAL_INFO VARCHAR(128))";
    /**
     * Команда для отключения базы данных.
     */
    private static final String shutdownString = "jdbc:derby:;shutdown=true";
    /**
     * Команда для выбора всех контактов из таблицы.
     */
    private static final String selectAllString = "SELECT * from CONTACTS";
    /**
     * Шаблон команды для вставки контакта в базу данных.
     */
    private static final String insertString = "INSERT INTO CONTACTS (LAST_NAME, FIRST_NAME, THIRD_NAME, MOBILE_PHONE, HOME_PHONE, ADDRESS, BIRTHDAY_DATE, ADDITIONAL_INFO)";
    /**
     * Шаблон команды для удаления контакта из базы данных.
     */
    private static final String deleteString = "DELETE FROM CONTACTS WHERE ";

    /**
     * Текущее соединение с базой данных
     */
    private Connection connection = null;

    /**
     * Устанавливает соединение с базой данных, где хранятся текущие контакты
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void establishConnection() throws SQLException
    {
        connection = DriverManager.getConnection(connectionURL);
        Statement statement = connection.createStatement();
        if (!checkTableExists(connection))
        {
            System.out.println(" . . . . creating table CONTACTS");
            statement.execute(createString);
        }
        statement.close();
    }

    /**
     * Устанавливает соединение с базой данных, переданной в параметре (для теста)
     * @param connection Соединение с базой данных
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void establishConnection(Connection connection) throws SQLException
    {
        if (connection == null)
            throw new NullPointerException("Connection argument was null.");
        this.connection = connection;
        Statement statement = connection.createStatement();
        if (!checkTableExists(connection))
        {
            System.out.println(" . . . . creating table CONTACTS");
            statement.execute(createString);
        }
        statement.close();
    }

    /**
     * Закрывает соединение с базой данных.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public void closeConnection() throws SQLException
    {
        connection.close();
        boolean successClosed = false;
        try
        {
            DriverManager.getConnection(shutdownString);
        }
        catch (SQLException e)
        {
            if (e.getSQLState().equals("XJ015"))
                successClosed = true;
        }
        if (!successClosed)
            throw new SQLException("Failed to shutdown database.");
    }

    /**
     * Импортирует контакты из текущей базы данных
     * @return Список всех контактов из базы данных.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public ArrayList<Contact> importContacts() throws SQLException
    {
        ArrayList<Contact> importedList = new ArrayList<Contact>();
        PreparedStatement prepStatement = connection.prepareStatement(selectAllString);
        ResultSet response = prepStatement.executeQuery();
        while (response.next())
        {
            int id = response.getInt(1);
            String[] stringFields = new String[]
            {
                response.getString(2), // LAST_NAME[0]
                response.getString(3), // FIRST_NAME[1]
                response.getString(4), // THIRD_NAME[2]
                response.getString(5), // MOBILE_PHONE[3]
                response.getString(6), // HOME_PHONE[4]
                response.getString(7), // ADDRESS[5]
                response.getString(9)  // ADDITIONAL_INFO[6]
            };
            Date birthdayDate = response.getDate(8);
            importedList.add(new Contact(
                stringFields[0] != null ? stringFields[0] : "",
                stringFields[1] != null ? stringFields[1] : "",
                stringFields[2] != null ? stringFields[2] : "",
                stringFields[3] != null ? stringFields[3] : "",
                stringFields[4] != null ? stringFields[4] : "",
                stringFields[5] != null ? stringFields[5] : "",
                birthdayDate != null ? birthdayDate.toLocalDate() : null,
                stringFields[6] != null ? stringFields[6] : ""
            ));
            importedList.get(importedList.size() - 1).setId(id);
        }
        prepStatement.close();

        return importedList;
    }

    /**
     * Вставляет контакт в базу данных
     * @param contact Вставляемый контакт
     * @return Результат вставки (обычно false)
     * @throws NullPointerException Передаваемый контакт оказался null.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public boolean insertContact(final Contact contact) throws NullPointerException, SQLException
    {
        if (contact == null)
            throw new NullPointerException("Contact argument was null.");
        StringBuilder birthdayDate = new StringBuilder();
        if (contact.getBirthdayDate() != null)
        {
            birthdayDate.append("'");
            birthdayDate.append(contact.getBirthdayDate().toString());
            birthdayDate.append("'");
        }
        PreparedStatement prepStatement = connection.prepareStatement(
                insertString + " VALUES " + String.format("('%s', '%s', '%s', '%s', '%s', '%s', %s, '%s')",
                        contact.getLastName(),
                        contact.getFirstName(),
                        contact.getThirdName(),
                        contact.getMobilePhoneNumber(),
                        contact.getHomePhoneNumber(),
                        contact.getAddress(),
                        contact.getBirthdayDate() != null ? birthdayDate : "NULL",
                        contact.getAdditionalInfo()));
        boolean success = prepStatement.execute();
        prepStatement.close();
        return success;
    }

    /**
     * Удаляет запись контакта из базы данных.
     * @param contact Удаляемый контакт.
     * @return Результат запроса.
     * @throws NullPointerException Передаваемый контакт оказался null.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    public boolean removeContact(final Contact contact) throws NullPointerException, SQLException
    {
        if (contact == null)
            throw new NullPointerException("Contact argument was null.");
        PreparedStatement prepStatement = connection.prepareStatement(deleteString +
                String.format("LAST_NAME = '%s' AND FIRST_NAME = '%s' AND THIRD_NAME = '%s'",
                        contact.getLastName(), contact.getFirstName(), contact.getThirdName()));
        return prepStatement.execute();
    }

    /**
     * Проверяет наличие таблицы CONTACTS в базе данных.
     * @param conn Текущее подключение.
     * @return true - если таблица присутствует, false - иначе.
     * @throws SQLException Ошибка обращения к базе данных.
     */
    private static boolean checkTableExists(Connection conn) throws SQLException {
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, "CONTACTS", null);
        return tables.next();
    }
}
