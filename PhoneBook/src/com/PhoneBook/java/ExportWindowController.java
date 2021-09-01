package com.PhoneBook.java;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ExportWindowController
{
    /**
     * Поле с путем до файла
     */
    @FXML
    private TextField pathField;

    public void onExportButtonClicked(ActionEvent actionEvent)
    {
        // Получаю путь
        String path = pathField.getText().trim();
        // Проверяю на заполненность
        if (!path.isBlank())
        {
            try
            {
                // Экспортирую
                Main.contactsContainer.exportToCSV(path);
                // Закрываю окно
                closeWindow();
            }
            catch (IOException e)
            {
                MainWindowController.createWarningWindow(e.getMessage());
            }
        }
        else MainWindowController.createWarningWindow("Путь до файла пустой.");
    }

    /**
     * Срабатывает при нажатии на кнопку отмены
     * @param actionEvent
     */
    public void onCancelButtonClicked(ActionEvent actionEvent)
    {
        closeWindow();
    }

    /**
     * Закрывает окно
     */
    private void closeWindow()
    {
        ((Stage)pathField.getScene().getWindow()).close();
    }
}
