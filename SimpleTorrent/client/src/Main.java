import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static FileTorrentClient fileClient;

    public static void main(String[] args)
    {
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
        primaryStage.setTitle("Simple Torrent (client)");
        // Задаю размер сцены
        primaryStage.setScene(new Scene(root, 605, 430));
        // Ограничиваю размеры главного окна
        primaryStage.setMinWidth(605);
        primaryStage.setMinHeight(430);
        // Вывожу на экран
        primaryStage.show();
    }
}
