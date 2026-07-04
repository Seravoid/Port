import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        File fxmlFile = new File("main.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1200, 800);
        
        primaryStage.setTitle("Морской порт — имитационное моделирование");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}