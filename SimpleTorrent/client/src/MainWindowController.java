import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MainWindowController {

    private static final long MAX_FILE_SIZE = 137438953472L;
    private static final long MIN_FILE_SIZE = 1;
    private static final String BYTES = "Б";
    private static final String KBYTES = "КБ";
    private static final String MBYTES = "МБ";
    private static final String GBYTES = "ГБ";

    @FXML
    private TableView<FileData> filesTable;
    @FXML
    private TableColumn<FileData, String> fileNameColumn;
    @FXML
    private TableColumn<FileData, String> fileSizeColumn;
    @FXML
    private TableColumn<FileData, LocalDateTime> fileLastChangeColumn;
    @FXML
    private TableView<FileData> savedFilesTable;
    @FXML
    private TableColumn<FileData, String> savedFileNameColumn;
    @FXML
    private TableColumn<FileData, String> savedFileSizeColumn;
    @FXML
    private TableColumn<FileData, LocalDateTime> savedFileLastChangeColumn;
    @FXML
    private ProgressBar downloadProgressBar;
    @FXML
    private Label downloadingProgressLabel;

    private FileData[] currentFiles;

    /**
     * Срабатывает при открытии окна
     */
    @FXML
    private void initialize()
    {
        downloadingProgressLabel.setVisible(false);
        downloadProgressBar.setVisible(false);
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<FileData, String>("fileName"));
        fileSizeColumn.setCellValueFactory(this::formatFileSize);
        fileLastChangeColumn.setCellValueFactory(this::formatDateTime);

        savedFileNameColumn.setCellValueFactory(new PropertyValueFactory<FileData, String>("fileName"));
        savedFileSizeColumn.setCellValueFactory(this::formatFileSize);
        savedFileLastChangeColumn.setCellValueFactory(this::formatDateTime);

        filesTable.setRowFactory( tv -> {
            TableRow<FileData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    FileData rowData = row.getItem();
                    askForDownload(rowData);
                }
            });
            return row ;
        });

        connectToHost();
        updateTable();
    }

    private ObservableValue<LocalDateTime> formatDateTime(TableColumn.CellDataFeatures<FileData, LocalDateTime> param) {
        FileData fileData = param.getValue();
        return new SimpleObjectProperty<>
                (LocalDateTime.ofInstant(Instant.ofEpochMilli(fileData.getFileLastChange()), ZoneId.systemDefault()));
    }

    private SimpleObjectProperty<String> formatFileSize(TableColumn.CellDataFeatures<FileData, String> param)
    {
        long fileSize = param.getValue().getFileSize();
        String sizeType = BYTES;
        if (fileSize >= 1073741824)
        {
            fileSize /= 1073741824;
            sizeType = GBYTES;
        }
        else if (fileSize >= 1048576)
        {
            fileSize /= 1048576;
            sizeType = MBYTES;
        }
        else if (fileSize >= 1024)
        {
            fileSize /= 1024;
            sizeType = KBYTES;
        }
        return new SimpleObjectProperty<>(String.format("%d %s", fileSize, sizeType));
    }

    private void updateTable()
    {
        currentFiles = getFiles();
        filesTable.setItems(FXCollections.observableArrayList(currentFiles));
        Main.fileClient.setWindowController(this);
    }

    public void onConnectButtonClick(ActionEvent actionEvent)
    {
        connectToHost();
        updateTable();
    }

    private void connectToHost()
    {
        try {
            // Получаю ресурсы файла разметки
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConnectionWindowMarkup.fxml"));
            // Загружаю файл
            Parent root = loader.load();
            // Создаю новое окно
            Stage stage = new Stage();
            // Задаю ему название
            stage.setTitle("Connect to host");
            stage.setScene(new Scene(root));
            // Делаю новое окно модальным
            stage.initModality(Modality.APPLICATION_MODAL);
            // Запрещаю изменять размер
            stage.setResizable(false);
            // Вывожу на экран
            stage.showAndWait();
        }
        catch (IOException e)
        {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }

    private FileData[] getFiles()
    {
        try
        {
            return Main.fileClient.getFileList();
        }
        catch (ClassNotFoundException e)
        {
            showError(e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            showError(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private void askForDownload(final FileData fileData)
    {
        if (Main.fileClient.isLock())
        {
            showError("Downloading process haven't finished yet.");
            return;
        }
        if (fileData.getFileSize() > MAX_FILE_SIZE)
        {
            showError(String.format("Max file size is %d Bytes.", MAX_FILE_SIZE));
            return;
        }
        if (fileData.getFileSize() < MIN_FILE_SIZE)
        {
            showError(String.format("Min file size is %d Bytes.", MIN_FILE_SIZE));
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Download file with size " + fileData.getFileSize() + " bytes?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES)
        {
            //if (savedFilesTable.getItems().contains(fileData))
            //{
            //    if (!showConfirmationWindow("Файл уже загружен. Перезаписать его?"))
            //    {
            //        return;
            //    }
            //}
            downloadFile(fileData);
        }
    }

    public void updateProgressBar(final double value)
    {
        downloadProgressBar.setVisible(true);
        downloadingProgressLabel.setVisible(true);
        downloadProgressBar.progressProperty().set(value);
        if (downloadProgressBar.progressProperty().get() >= 1.0d)
        {
            downloadProgressBar.setVisible(false);
            downloadingProgressLabel.setVisible(false);
        }
    }

    public void addSavedFile(final FileData fileData)
    {
        if (!savedFilesTable.getItems().contains(fileData))
            savedFilesTable.getItems().add(fileData);
    }

    public static void showError(final String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static boolean showConfirmationWindow(final String message)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        return alert.getResult() == ButtonType.YES;
    }

    public void onShowReferenceClick(ActionEvent actionEvent)
    {
        try
        {
            // Получаю ресурсы файла разметки
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReferenceWindowMarkup.fxml"));
            // Загружаю файл
            Parent root = loader.load();
            // Создаю новое окно
            Stage stage = new Stage();
            // Задаю ему название
            stage.setTitle("Справка");
            stage.setScene(new Scene(root));
            // Делаю новое окно модальным
            stage.initModality(Modality.APPLICATION_MODAL);
            // Запрещаю изменять размер
            stage.setResizable(false);
            // Вывожу на экран
            stage.showAndWait();
        }
        catch (IOException e)
        {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadFile(final FileData fileData)
    {
        try
        {
            // Получаю ресурсы файла разметки
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SaveFileWindowMarkup.fxml"));
            // Загружаю файл
            Parent root = loader.load();
            // Создаю новое окно
            Stage stage = new Stage();
            // Задаю ему название
            stage.setTitle("Сохранить файл");
            stage.setScene(new Scene(root));
            // Делаю новое окно модальным
            stage.initModality(Modality.APPLICATION_MODAL);
            // Запрещаю изменять размер
            stage.setResizable(false);
            SaveFileWindowController contoller = loader.<SaveFileWindowController>getController();
            contoller.initData(fileData);
            // Вывожу на экран
            stage.showAndWait();
        }
        catch (IOException e)
        {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }
}
