package com.PhoneBook.java;

import com.ContactsLogic.java.Contact;
import com.ContactsLogic.java.ContactsContainer;
import com.ContactsLogic.java.ContactsDataBase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;

public class Main extends Application {

    /**
     * Путь до текущего файла с контактами
     */
    private static final String pathToCSV = "contacts.csv";
    /**
     * Контейнер с контактами
     */
    public static ContactsContainer contactsContainer;

    /**
     * Входная точка программы
     * @param args Входные параметры
     */
    public static void main(String[] args)
    {
        //ContactsDataBase db = new ContactsDataBase();
        //db.importContacts();

        // Инициализация контейнера
        contactsContainer = new ContactsContainer();
        // Запуск оконнго прилодения
        launch(args);
    }

    /**
     * Метод, срабатыающий при запуске приложения
     * @param primaryStage Главная сцена
     * @throws Exception Ошибка при запуске приложения
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Получаю ресурсы файла разметки
        Parent root = FXMLLoader.load(getClass().getResource("MainWindowMarkup.fxml"));
        // Задаю имя окна
        primaryStage.setTitle("Phone Book");
        // Задаю размер сцены
        primaryStage.setScene(new Scene(root, 700, 500));
        // Ограничиваю размеры главного окна
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        // Вывожу на экран
        primaryStage.show();
    }

    @Override
    public void stop()
    {
        try
        {
            contactsContainer.closeDB();
        }
        catch (SQLException e)
        {
            MainWindowController.createWarningWindow(e.getMessage());
        }
    }

    /**
     * Экспортирует сохраненные контакты из контейнера в файл contacts.csv
     * @throws IOException Ошибка при работе с файлами
     */
    public static void exportContacts() throws IOException
    {
        contactsContainer.exportToCSV(pathToCSV);
    }

    /**
     * Импортирует контакты из файла contacts.csv
     * @throws IOException Ошибка при работе с файлами
     * @throws IndexOutOfBoundsException Ошибка форматирования файла
     * @throws DateTimeParseException Ошибка форматирования даты
     */
    public static void importContacts() throws IOException, IndexOutOfBoundsException, DateTimeParseException, SQLException
    {
        contactsContainer.importFromCSV(pathToCSV);
    }

    /**
     * Проверяет содержат ли поля контакта точку с запятой
     * @param contact Проверяемый контакт
     * @return true - если содержит, false - если не содержит
     */
    public static boolean containsSemicolon(final Contact contact)
    {
        return contact.getLastName().contains(";") ||
                contact.getFirstName().contains(";") ||
                contact.getThirdName().contains(";") ||
                contact.getMobilePhoneNumber().contains(";") ||
                contact.getHomePhoneNumber().contains(";") ||
                contact.getAddress().contains(";") ||
                contact.getAdditionalInfo().contains(";");
    }
}
