import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectionWindowController {
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;

    public void onConnectButtonClick(ActionEvent actionEvent)
    {
        try
        {
            String ip = ipField.getText().trim();
            //if (!verifyIpAddress(ip))
            //{
            //    MainWindowController.showError("Ошибка формата ip адресса.");
            //    return;
            //}
            int port = Integer.parseInt(portField.getText().trim());
            if (port < 0 || port > 65535)
            {
                MainWindowController.showError("Порт ограничен в интервале [0; 65535].");
                return;
            }
            Main.fileClient = new FileTorrentClient(ip, port, passwordField.getText(), userNameField.getText());
            if (Main.fileClient.establishConnection())
            {
                System.out.printf("Connected to %s:%d.", ipField.getText(), port);
                closeWindow();
            }
            else
            {
                MainWindowController.showError("Password was incorrect.");
            }
        }
        catch (NumberFormatException e)
        {
            MainWindowController.showError("Порт был введено неверно. Введите целое число.");
        }
        catch (ClassNotFoundException | IOException e)
        {
            MainWindowController.showError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeWindow()
    {
        ((Stage)ipField.getScene().getWindow()).close();
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        closeWindow();
    }

    private static boolean verifyIpAddress(final String ip)
    {
        if (ip.equals("localhost"))
            return true;
        for (int i = 0; i < ip.length(); ++i)
        {
            if (ip.charAt(i) != '.' && (ip.charAt(i) < '0' || ip.charAt(i) > '9'))
            {
                // из-за днс возможно слово как адрес
                return true;
            }
        }
        try
        {
            String[] parts = ip.split(".");
            for (String part : parts)
            {
                int val = Integer.parseInt(part);
                if (val > 255)
                {
                    return false;
                }
            }
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }
}
