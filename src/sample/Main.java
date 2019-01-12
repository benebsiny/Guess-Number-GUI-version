package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.getIcons().add(new Image("sample/GuessNumber.png"));
        primaryStage.setTitle("猜數字遊戲");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Controller con = new Controller();
            Alert alert = con.showDialog(Alert.AlertType.CONFIRMATION,"關閉程式","確定要關閉遊戲?");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.CANCEL){ //Do not close the window
                event.consume();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}
