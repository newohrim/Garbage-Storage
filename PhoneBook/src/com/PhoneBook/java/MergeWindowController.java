package com.PhoneBook.java;

import com.ContactsLogic.java.Contact;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.control.TextField;

import java.sql.SQLException;


public class MergeWindowController {

    /**
     * Сохраненный контакт (который уже в контейнре)
     */
    private Contact savedContact;
    /**
     * Импортированный контакт (который не удалось добавить из-за конфликта)
     */
    private Contact importedContact;

    // Поля в окне
    @FXML
    private TextField lastNameFieldSaved;
    @FXML
    private TextField firstNameFieldSaved;
    @FXML
    private TextField thirdNameFieldSaved;
    @FXML
    private TextField mobilePhoneNumberFieldSaved;
    @FXML
    private TextField homePhoneNumberFieldSaved;
    @FXML
    private TextField lastNameFieldImported;
    @FXML
    private TextField firstNameFieldImported;
    @FXML
    private TextField thirdNameFieldImported;
    @FXML
    private TextField mobilePhoneNumberFieldImported;
    @FXML
    private TextField homePhoneNumberFieldImported;
    @FXML
    private Button skipButton;

    /**
     * Заполняет поля информацией из пары
     * @param contactPair Пара конфликтующих контактов
     */
    public void initData(Pair<Contact, Contact> contactPair)
    {
        importedContact = contactPair.getKey();
        savedContact = contactPair.getValue();

        lastNameFieldSaved.setText(savedContact.getLastName());
        firstNameFieldSaved.setText(savedContact.getFirstName());
        thirdNameFieldSaved.setText(savedContact.getThirdName());
        mobilePhoneNumberFieldSaved.setText(savedContact.getMobilePhoneNumber());
        homePhoneNumberFieldSaved.setText(savedContact.getHomePhoneNumber());
        lastNameFieldImported.setText(importedContact.getLastName());
        firstNameFieldImported.setText(importedContact.getFirstName());
        thirdNameFieldImported.setText(importedContact.getThirdName());
        mobilePhoneNumberFieldImported.setText(importedContact.getMobilePhoneNumber());
        homePhoneNumberFieldImported.setText(importedContact.getHomePhoneNumber());
    }

    /**
     * Срабатывает при нажатии на кнопку отмены
     * @param actionEvent Событие
     */
    public void onSkipButtonClicked(ActionEvent actionEvent)
    {
        // Закрываю окно
        closeWindow();
    }

    /**
     * Срабатывает при нажатии на кнопку Сохранить
     * @param actionEvent Событие
     */
    public void onSaveButton(ActionEvent actionEvent)
    {
        // Убираю границы с полей
        removeBorders();
        // Получаю пару контактов из полей окна
        Pair<Contact, Contact> editedContacts = extractData();
        // Если отредактированный контакт содержит точку с запятой
        if (Main.containsSemicolon(editedContacts.getValue()))
        {
            MainWindowController.createWarningWindow("Символ \";\" недопустим.");
        }
        // Проверка на заполненность контактов(а)
        else if (validateMerge(editedContacts))
        {
            // Добавляю контакт в контейнер (если обнаружится совпадение по ФИО, пара будет в конце очереди)
            try
            {
                Main.contactsContainer.addContact(editedContacts.getValue());
            }
            catch (SQLException e)
            {
                MainWindowController.createWarningWindow("Failed to insert contact into data base.");
            }
            // Закрываю окно
            closeWindow();
        }
        else
        {
            MainWindowController.createWarningWindow(
                    "Last or first names, phone numbers were empty or contacts names were the same.");
            // Помечаю некорректные поля красным
            markIncorrectFields(editedContacts.getValue());
        }
    }

    /**
     * Получаю пару контактов из полей окна
     * @return Пара новых контактов
     */
    private Pair<Contact, Contact> extractData()
    {
        return new Pair<Contact, Contact>(
                new Contact(
                        lastNameFieldSaved.getText(),
                        firstNameFieldSaved.getText(),
                        thirdNameFieldSaved.getText(),
                        mobilePhoneNumberFieldSaved.getText(),
                        homePhoneNumberFieldSaved.getText(),
                        savedContact.getAddress(),
                        savedContact.getBirthdayDate(),
                        savedContact.getAdditionalInfo()
                        ),
                new Contact(
                        lastNameFieldImported.getText(),
                        firstNameFieldImported.getText(),
                        thirdNameFieldImported.getText(),
                        mobilePhoneNumberFieldImported.getText(),
                        homePhoneNumberFieldImported.getText(),
                        importedContact.getAddress(),
                        importedContact.getBirthdayDate(),
                        importedContact.getAdditionalInfo()
                )
        );
    }

    /**
     * Проверка на заполненность пары контактов
     * @param pair Пара контактов
     * @return true - если контакты правильно заполнены и не совпадют по ФИО
     */
    private boolean validateMerge(Pair<Contact, Contact> pair)
    {
        return //pair.getKey().validate() &&
                pair.getValue().validate() &&
                !pair.getKey().compareByNames(pair.getValue());
    }

    /**
     * Помечает некорректные поля красным
     * @param contact Проверяемый контакт
     */
    private void markIncorrectFields(final Contact contact)
    {
        if (contact.getLastName().isBlank())
        {
            lastNameFieldImported.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        }
        if (contact.getFirstName().isBlank())
        {
            firstNameFieldImported.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        }
        if (contact.getMobilePhoneNumber().isBlank() && contact.getHomePhoneNumber().isBlank())
        {
            mobilePhoneNumberFieldImported.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            homePhoneNumberFieldImported.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        }
    }

    /**
     * Убирает границы с полей
     */
    private void removeBorders()
    {
        lastNameFieldImported.setStyle("-fx-text-box-border: #2b2b2b;");
        firstNameFieldImported.setStyle("-fx-text-box-border: #2b2b2b;");
        mobilePhoneNumberFieldImported.setStyle("-fx-text-box-border: #2b2b2b;");
        homePhoneNumberFieldImported.setStyle("-fx-text-box-border: #2b2b2b;");
    }

    /**
     * Закрывает окно
     */
    private void closeWindow()
    {
        ((Stage)skipButton.getScene().getWindow()).close();
    }
}
