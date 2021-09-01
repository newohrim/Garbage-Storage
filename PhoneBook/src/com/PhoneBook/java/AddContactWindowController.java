package com.PhoneBook.java;

import com.ContactsLogic.java.Contact;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;

public class AddContactWindowController {

    // Поля в окне программы
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField thirdNameField;
    @FXML
    private TextField mobilePhoneNumberField;
    @FXML
    private TextField homePhoneNumberField;
    @FXML
    private TextField addressField;
    @FXML
    private DatePicker birthdayField;
    @FXML
    private TextArea additionalInfoField;

    /**
     * Получает контакт из заполненных полей
     * @return Возвращает новый контакт
     */
    private Contact extractData()
    {
        return new Contact(
                lastNameField.getText(),
                firstNameField.getText(),
                thirdNameField.getText(),
                mobilePhoneNumberField.getText(),
                homePhoneNumberField.getText(),
                addressField.getText(),
                birthdayField.getValue(),
                additionalInfoField.getText()
        );
    }

    /**
     * Срабатывает при нажатии на кнопку отмены
     * @param actionEvent
     */
    public void onCancelButtonClick(ActionEvent actionEvent)
    {
        closeWindow();
    }

    /**
     * Срабатывает при нажатии на кнопку Добавить
     * @param actionEvent Событие
     */
    public void onAddButtonClick(ActionEvent actionEvent)
    {
        // Убираю границы с полей
        removeBorders();
        // Получаю новый контакт из полей
        Contact contact = extractData();
        // Проверяю на наличие точки с запятой в полях
        if (Main.containsSemicolon(contact))
        {
            MainWindowController.createWarningWindow("Символ \";\" недопустим.");
        }
        // Проверяю на условие заполненности контакта
        else if (!contact.validate())
        {
            MainWindowController.createWarningWindow(
                    "Фамилия, имя или номер телефона были не заполнены.");
            // Помечаю некорректные поля красным
            markIncorrectFields(contact);
        }
        else
        {
            // Добавляю контакт в контейнер
            try
            {
                Main.contactsContainer.addContact(contact);
            }
            catch (SQLException e)
            {
                MainWindowController.createWarningWindow("Failed to insert contact in data base.");
            }
            // Если контакт с таким ФИО уже имеется
            if (!Main.contactsContainer.isMerged())
            {
                // Получаю неслившубся пару, чтобы не засорять очередь
                Pair<Contact, Contact> pair = Main.contactsContainer.nextNotMerged();
                MainWindowController.createWarningWindow("Контакт с таким ФИО уже существует.");
            }
            else
            {
                // Закрываю окно
                closeWindow();
            }
        }
    }

    /**
     * Помечает некорректные поля красным
     * @param contact Проверяемый контакт
     */
    private void markIncorrectFields(final Contact contact)
    {
        // Проверяю и меняю цвет, если необходимо
        if (contact.getLastName().isBlank())
        {
            lastNameField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        }
        if (contact.getFirstName().isBlank())
        {
            firstNameField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        }
        if (contact.getMobilePhoneNumber().isBlank() && contact.getHomePhoneNumber().isBlank())
        {
            mobilePhoneNumberField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            homePhoneNumberField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        }
    }

    /**
     * Делает проверяемые поля темно-черными (сбрасывает цвет поля)
     */
    private void removeBorders()
    {
        lastNameField.setStyle("-fx-text-box-border: #2b2b2b;");
        firstNameField.setStyle("-fx-text-box-border: #2b2b2b;");
        mobilePhoneNumberField.setStyle("-fx-text-box-border: #2b2b2b;");
        homePhoneNumberField.setStyle("-fx-text-box-border: #2b2b2b;");
    }

    /**
     * Закрывает окно
     */
    private void closeWindow()
    {
        ((Stage)lastNameField.getScene().getWindow()).close();
    }
}
