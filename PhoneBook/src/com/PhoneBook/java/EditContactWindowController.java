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

public class EditContactWindowController
{
    /**
     * Редактируемый контакт
     */
    private Contact currentContact;

    // Поля в окне
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
     * Заполняет поля информацией из редактируемого контакта
     * @param editableContact
     */
    public void initData(final Contact editableContact)
    {
        currentContact = editableContact;
        lastNameField.setText(currentContact.getLastName());
        firstNameField.setText(currentContact.getFirstName());
        thirdNameField.setText(currentContact.getThirdName());
        mobilePhoneNumberField.setText(currentContact.getMobilePhoneNumber());
        homePhoneNumberField.setText(currentContact.getHomePhoneNumber());
        addressField.setText(currentContact.getAddress());
        birthdayField.setValue(currentContact.getBirthdayDate());
        additionalInfoField.setText(currentContact.getAdditionalInfo());
    }

    /**
     * Срабатывает при нажатии на кнопку Сохранить
     * @param actionEvent Событие
     */
    public void onSaveButtonClicked(ActionEvent actionEvent)
    {
        // Убираю границы с полей
        removeBorders();
        // Получаю отредактированный контакт
        Contact editedContact = extractData();
        // Проверяю на наличие точки с запятой
        if (Main.containsSemicolon(editedContact))
        {
            MainWindowController.createWarningWindow("Символ \";\" недопустим.");
        }
        // Проверяю контакт на условие заполненности
        else if (editedContact.validate())
        {
            // Проверяю на совпадения по ФИО
            boolean success = false;
            try
            {
                success = Main.contactsContainer.addContact(editedContact);
            }
            catch (SQLException e)
            {
                MainWindowController.createWarningWindow(
                        "Failed to insert contact into data base.");
            }
            if (success)
            {
                // Если отредактированный контакт успешно добавлен, то удаляем его предыдущую версию
                try
                {
                    Main.contactsContainer.removeContact(currentContact);
                }
                catch (SQLException e)
                {
                    MainWindowController.createWarningWindow(
                            "Failed to remove contact from data base.");
                }
                // Закрываем окно
                closeWindow();
            }
            else
            {
                // Получаем не слившуюся пару
                Pair<Contact, Contact> pair = Main.contactsContainer.nextNotMerged();
                // Проверяем на совпадение по ФИО
                if (currentContact.compareByNames(editedContact))
                {
                    // Если ФИО не было изменено, то просто заменяем текущий контект
                    currentContact.replaceBy(editedContact);
                    // Закрываем окно
                    closeWindow();
                }
                else
                {
                    // Совпадение по ФИО с другим контактом не допустимо
                    MainWindowController.createWarningWindow(
                            "Контакт с таким ФИО уже существует.");
                }
            }
        }
        else
        {
            MainWindowController.createWarningWindow(
                    "Фамилия, имя или хотя бы один номер телефона были не заполнены.");
            // Помечаю некорректные поля красным
            markIncorrectFields(editedContact);
        }
    }

    /**
     * Срабатывает при нажатии на кнопку отмены
     * @param actionEvent Событие
     */
    public void onCancelButtonClicked(ActionEvent actionEvent)
    {
        // Закрываю окно
        closeWindow();
    }

    /**
     * Получает новый контакт из полей окна
     * @return Возаращет новый контакт
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
     * Закрывает окно
     */
    private void closeWindow()
    {
        ((Stage)lastNameField.getScene().getWindow()).close();
    }

    /**
     * Помечает некорректные поля красным
     * @param contact
     */
    private void markIncorrectFields(final Contact contact)
    {
        // Помечаю только некорректно заполненные поля
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
     * Убирает границы с полей
     */
    private void removeBorders()
    {
        lastNameField.setStyle("-fx-text-box-border: #2b2b2b;");
        firstNameField.setStyle("-fx-text-box-border: #2b2b2b;");
        mobilePhoneNumberField.setStyle("-fx-text-box-border: #2b2b2b;");
        homePhoneNumberField.setStyle("-fx-text-box-border: #2b2b2b;");
    }
}
