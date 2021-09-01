package com.PhoneBook.java;

import com.ContactsLogic.java.Contact;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainWindowController
{
    /**
     * Текщий список с контактами (в таблице)
     */
    private ObservableList<Contact> currentContactsList;

    // Таблица и поля на окне
    @FXML
    private TableView<Contact> contactsTable;
    @FXML
    private TableColumn<Contact, String> lastNameColumn;
    @FXML
    private TableColumn<Contact, String> firstNameColumn;
    @FXML
    private TableColumn<Contact, String> thirdNameColumn;
    @FXML
    private TableColumn<Contact, String> phoneNumberColumn;
    @FXML
    private TableColumn<Contact, String> addressColumn;
    @FXML
    private TableColumn<Contact, LocalDate> birthdayColumn;
    @FXML
    private TableColumn<Contact, String> additionalInfoColumn;

    @FXML
    private TextField searchField;

    /**
     * Срабатывает при открытии окна
     */
    @FXML
    private void initialize()
    {
        createDBConnection();
        // Привязываю колонны к полям по названаниям
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("firstName"));
        thirdNameColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("thirdName"));
        // Значение в колонне с мобильным телефонов = мобильный телефон или домашний телефон
        phoneNumberColumn.setCellValueFactory(param -> {
            Contact c = param.getValue();
            if (!c.getMobilePhoneNumber().isBlank())
                return new SimpleObjectProperty<>(c.getMobilePhoneNumber());
            else
                return new SimpleObjectProperty<>(c.getHomePhoneNumber());
        });
        // Так же создаю подсказку с домашним телефоном для каждой ячейки колонны
        phoneNumberColumn.setCellFactory (column -> new TableCell<Contact, String>()
        {
            @Override
            protected void updateItem(String item, boolean empty)
            {
                // Получаю контакт в строке
                Contact c = (Contact)super.getTableRow().getItem();
                // Если контакта нет, то в ячейке пустое место
                if (c == null)
                {
                    setText("");
                }
                // Иначе мобильный или домашний телефон
                else
                {
                    super.updateItem(item, empty);
                    setText(item);
                    // Подсказка с домашним телефоном
                    setTooltip(new Tooltip(c.getHomePhoneNumber()));
                }
            }
        });
        addressColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("address"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<Contact, LocalDate>("birthdayDate"));
        additionalInfoColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("additionalInfo"));

        // Открываю файл contacts.csv
        openLastSession();
        // Создаю смотрибельный лист с контактами из контейнера
        currentContactsList = FXCollections.observableArrayList(Main.contactsContainer.getContacts());
        // Заполняю таблицу
        contactsTable.setItems(currentContactsList);
    }

    private void createDBConnection()
    {
        try
        {
            Main.contactsContainer.initDB();
        }
        catch (SQLException e)
        {
            createWarningWindow(e.getMessage());
        }
    }

    /**
     * Срабатывает при нажатии на кнопку Поиск или ENTER на клавиатуре
     * @param actionEvent Событие
     */
    @FXML
    public void onSearchButtonClick(ActionEvent actionEvent) {
        // Поисковой запрос
        String searchRequest = searchField.getText();
        // Проверка на заполненность
        if (!searchRequest.isEmpty())
        {
            // Получаю отфильтрованный по Фамилии, имени или Отчеству список контактов
            List list = Main.contactsContainer.filterByNames(searchRequest);
            // Создаю из него смотрибельный лист
            ObservableList<Contact> filteredList =
                    FXCollections.observableList(list);
            // Заполняю таблицу
            contactsTable.setItems(filteredList);
        }
        else
        {
            // Восстанавливаю текущий список контактов
            contactsTable.setItems(currentContactsList);
        }
    }

    /**
     * Срабатывает при нажатии на пункт меню Импортировать
     * @param actionEvent Событие
     */
    public void onImportButtonClicked(ActionEvent actionEvent) {
        try
        {
            // Файловый проводние
            FileChooser fileChooser = new FileChooser();
            // Задаю ему название
            fileChooser.setTitle("Выберите файл с контактами");
            // Выбираю расширения подходящих файлов
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV files", "*.csv"),
                    new FileChooser.ExtensionFilter("All files", "*.*"));
            // Получаю выбранный файл
            File selectedFile = fileChooser.showOpenDialog((Stage)contactsTable.getScene().getWindow());
            // Если файл был выбран
            if (selectedFile != null)
            {
                // Импортирую список контактов и провожу слияние
                Main.contactsContainer.importFromCSV(selectedFile.getPath());
            }
            else
            {
                createWarningWindow("No file selected.");
                return;
            }
        }
        catch (SQLException e)
        {
            createWarningWindow("Failed to insert contact into data base.");
        }
        catch (IndexOutOfBoundsException e)
        {
            createWarningWindow("Format of csv file was wrong.");
        }
        catch (DateTimeParseException e)
        {
            createWarningWindow(
                    "Ошибка форматирования даты рождения. Невозможно прочитать строку " + e.getParsedString());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        // Решаю все возникшие конфлиты слияния
        resolveMergeConflict();
        // Сохраняю изменения
        saveChanges();
        // Обновляю таблицу
        updateTable();
    }

    /**
     * Создает окно с ошибкой
     * @param warningMessage Сообщение об ошибке
     */
    public static void createWarningWindow(final String warningMessage)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, warningMessage, ButtonType.OK);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    /**
     * Создает окно с подтверждением действия
     * @param questionMessage Вопрос подтверждения
     * @return true - если нажата ДА, false - если нажата НЕТ
     */
    public static boolean createConfirmationWindow(final String questionMessage)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, questionMessage, ButtonType.NO, ButtonType.YES);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    /**
     * Обновляет таблицу напрямую из контейнера
     */
    private void updateTable()
    {
        currentContactsList.clear();
        currentContactsList =
                FXCollections.observableArrayList(Main.contactsContainer.getContacts());
        contactsTable.setItems(currentContactsList);
    }

    /**
     * Срабатывает при нажатии на кнопку Добавить
     * @param actionEvent Событие
     */
    public void onAddButtonClicked(ActionEvent actionEvent)
    {
        try
        {
            // Получаю ресурсы файла разметки
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddContactWindowMarkup.fxml"));
            // Загружаю файл
            Parent root = loader.load();
            // Создаю новое окно
            Stage stage = new Stage();
            // Задаю ему название
            stage.setTitle("Add Contact");
            stage.setScene(new Scene(root));
            // Делаю новое окно модальным
            stage.initModality(Modality.APPLICATION_MODAL);
            // Запрещаю изменять размер
            stage.setResizable(false);
            // Вывожу на экран
            stage.showAndWait();
            // Сохраняю изменения
            saveChanges();
            // Обновляю таблицу с контктами
            updateTable();
        }
        catch (IOException e)
        {
            createWarningWindow(
                    "Файл разметки страницы невозможно прочитать.");
        }
    }

    /**
     * Срабывает при нажатии на кнопку Удалить
     * @param actionEvent Событие
     */
    public void onDeleteButtonClicked(ActionEvent actionEvent)
    {
        // Поулчаю выбранный контакт (из выбранной строки)
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        // Если контакт был выбран
        if (selectedContact != null)
        {
            // Вывожу сообщение с подтверждением
            if (createConfirmationWindow(
                    "Are you sure you want to delete selected contact?"))
            {
                // Пытаюсь удалить контакт
                try
                {
                    if (!Main.contactsContainer.removeContact(selectedContact)) {
                        createWarningWindow("Couldn't delete selected contact.");
                        return;
                    }
                }
                catch (SQLException e)
                {
                    createWarningWindow("Failed to remove contact from data base.");
                }
                // Сохраняю изменения
                saveChanges();
                // Обновляю таблицу
                updateTable();
            }
        }
        else createWarningWindow("No contact is selected.");
    }

    /**
     * Срабатывает при нажатии на пункт меню Выход
     * @param actionEvent Событие
     */
    public void onExitButtonClicked(ActionEvent actionEvent)
    {
        // Окно с подтверждением
        if (createConfirmationWindow("Are you sure you want to exit?"))
        {
            // На всякий случай сохраняю файл
            saveChanges();
            // Закрываю главное окно
            closeWindow();
        }
    }

    /**
     * Закрывает главное окно
     */
    private void closeWindow()
    {
        ((Stage)contactsTable.getScene().getWindow()).close();
    }

    /**
     * Срабатывает при отжатии кнопки при поиске (фильтрации)
     * @param keyEvent
     */
    public void onKeyReleasedHandle(KeyEvent keyEvent)
    {
        // Если отжатая кнопка является ENTER, то вызываю обработчик поиска (фильтрации)
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            onSearchButtonClick(null);
    }

    /**
     * Срабатывает при нажатии на кнопку Редактировать
     * @param actionEvent Событие
     */
    public void onEditButtonClicked(ActionEvent actionEvent)
    {
        // Получаю выбранный контакт
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        // Если контакт не был выбран
        if (selectedContact == null)
        {
            MainWindowController.createWarningWindow("Вы не выбрали редактируемый контак.");
            return;
        }
        try
        {
            // Загружаю файл разметки со страницей редактирования контакта
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditContactWindowMarkup.fxml"));
            Parent root = loader.load();
            // Получаю его контоллер
            EditContactWindowController controller = loader.<EditContactWindowController>getController();
            // Заполняю поля
            controller.initData(selectedContact);
            Stage stage = new Stage();
            // Присваиваю название
            stage.setTitle("Edit Contact");
            stage.setScene(new Scene(root));
            // Делаю окно модальным
            stage.initModality(Modality.APPLICATION_MODAL);
            // Запрещаю изменять размер окна
            stage.setResizable(false);
            // Вывожу на экран
            stage.showAndWait();
            // Сохраняю изменения
            saveChanges();
            // Обноваляю таблицу
            updateTable();
        }
        catch (IOException e)
        {
            createWarningWindow(
                    "Файл разметки страницы невозможно прочитать.");
        }
    }

    /**
     * Срабаытывает при нажатии на пункт меню Экспортировать
     * @param actionEvent Событие
     */
    public void onExportButtonClicked(ActionEvent actionEvent)
    {
        // Если контейнер пуст
        if (Main.contactsContainer.getContacts().isEmpty())
        {
            createWarningWindow("Таблица была пуста.");
            return;
        }
        try
        {
            // Загружаю файл с разметкой окна Экспортировать
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ExportWindowMarkup.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            // Задаю название окна
            stage.setTitle("Export");
            stage.setScene(new Scene(root));
            // Делаю окно модальным
            stage.initModality(Modality.APPLICATION_MODAL);
            // Запрещаю менять размер окна
            stage.setResizable(false);
            // Вывожу на экран
            stage.showAndWait();
        }
        catch (IOException e)
        {
            createWarningWindow(
                    "Файл разметки страницы невозможно прочитать.");
        }
    }

    /**
     * Срабатывает при нажатии на пункт меню Справка -> Открыть
     * @param actionEvent Событие
     */
    public void onReferenceButtonClicked(Event actionEvent)
    {
        try
        {
            // Загружаю файл разметки с окном Справки
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReferenceWindowMarkup.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            // Задаю название окна
            stage.setTitle("Справка");
            stage.setScene(new Scene(root));
            // Делаю его модальным
            stage.initModality(Modality.APPLICATION_MODAL);
            // Запрещаю менять размер
            stage.setResizable(false);
            // Вывожу на экран
            stage.showAndWait();
        }
        catch (IOException e)
        {
            createWarningWindow(
                    "Файл разметки страницы невозможно прочитать.");
        }
    }

    /**
     * Решает все возникшие конфликты слияния после импортирования
     */
    private void resolveMergeConflict()
    {
        // Пока контейнер с контактами не слит
        while (!Main.contactsContainer.isMerged())
        {
            // Получаю следующую (перую в очерди) конфликтующую пару
            Pair<Contact, Contact> c = Main.contactsContainer.nextNotMerged();
            try
            {
                // Загружаю файл с разметкой окна решения конфликтов слияния
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MergeWindowMarkup.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                // Меняю название окна
                stage.setTitle("Merge Conflict");
                stage.setScene(new Scene(root));
                // Делаю его модальным
                stage.initModality(Modality.APPLICATION_MODAL);
                // Запрещаю изменять размер
                stage.setResizable(false);
                // Получаю контроллер окна
                MergeWindowController mergeController = loader.<MergeWindowController>getController();
                // Заполняю все поля окна
                mergeController.initData(c);
                // Вывожу на экран
                stage.showAndWait();
            }
            catch (DateTimeParseException e)
            {
                createWarningWindow(
                        "Ошибка форматирования даты рождения. Невозможно прочитать строку " + e.getParsedString());
            }
            catch (IndexOutOfBoundsException e)
            {
                createWarningWindow(
                        "Ошибка форматирования файла с контактами.");
            }
            catch (IOException e)
            {
                createWarningWindow(
                        "Файл с контактами не найден или его невозможно прочитать.");
            }
            catch (RuntimeException e)
            {
                createWarningWindow(
                        "Ошибка форматирования файла с контактами.");
            }
        }
    }

    /**
     * Сохраняет изменения в контейнере с контактами в файл contacts.csv
     */
    private void saveChanges()
    {
        try
        {
            Main.exportContacts();
        }
        catch (IOException e)
        {
            createWarningWindow("Невозможно сохранить изменения в файл contacts.csv");
        }
    }

    /**
     * Загружает все контакты последней сессии из файла contacts.csv
     */
    private void openLastSession()
    {
        try
        {
            //Main.importContacts();
            Main.contactsContainer.importFromDB();
        }
        catch (SQLException e)
        {
            createWarningWindow("Failed to connect to data base.");
        }
        catch (IndexOutOfBoundsException e)
        {
            createWarningWindow("Format of csv file was wrong.");
        }
        catch (DateTimeParseException e)
        {
            createWarningWindow(
                    "Ошибка форматирования даты рождения. Невозможно прочитать строку " + e.getParsedString());
        }
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }
}
