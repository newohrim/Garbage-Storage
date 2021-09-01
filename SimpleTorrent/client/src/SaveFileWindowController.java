import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SaveFileWindowController
{
    private FileData fileData;

    @FXML
    private TextField pathField;

    public void initData(final FileData fileData)
    {
        this.fileData = fileData;
    }

    public void saveFileButtonClicked(ActionEvent actionEvent)
    {
        String path = pathField.getText();
        if (path.isBlank())
        {
            MainWindowController.showError("Path field was empty.");
            return;
        }
        startDownloading(fileData, path);
        closeWindow();
    }

    public void cancelButtonClicked(ActionEvent actionEvent)
    {
        closeWindow();
    }

    private void closeWindow()
    {
        ((Stage)pathField.getScene().getWindow()).close();
    }

    private void startDownloading(final FileData file, final String path)
    {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call()
            {
                try
                {
                    Main.fileClient.getFile(file, path);
                }
                catch (IOException e)
                {
                    MainWindowController.showError(e.getMessage());
                    e.printStackTrace();
                    Main.fileClient.resetLock();
                }
                return null;
            }
        };
        Thread downloadThread = new Thread(task);
        downloadThread.setDaemon(true);
        downloadThread.start();
    }
}
