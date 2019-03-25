package sample;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RegexValidator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class Controller {

    @FXML
    public JFXTextField input;
    public JFXButton btnEnter;
    public JFXButton btnNewGame;
    public Label correctAnswerLabel;
    public AnchorPane menuBar;
    public JFXListView<String> menuItems;
    public TableView<Record> table;
    public StackPane dialogPane;

    private int guessCount = 0;
    private String answer;
    private boolean gameEnded;

    private TableColumn inputColumn = new TableColumn("輸入");
    private TableColumn resultColumn = new TableColumn("結果");

    @FXML
    public void initialize() {
        //Games
        guessCount = 0;
        answer = makeAnswer();

        //Button & Label
        btnEnter.setVisible(true);
        btnNewGame.setVisible(false);
        correctAnswerLabel.setVisible(false);

        //Slide Menu Table
        ObservableList<String> menuList = FXCollections.observableArrayList("新遊戲", "更換樣式", "玩法", "關於", "離開");
        menuItems.setItems(menuList);

        //Result Table
        inputColumn.setSortable(false);
        resultColumn.setSortable(false);

        inputColumn.setCellValueFactory(new PropertyValueFactory<>("input")); //Fetch "input" type from Record class
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result")); //Fetch "result" type from Record class
        table.getColumns().addAll(inputColumn, resultColumn);

        /********JFoenix********/
        //TextField

        Platform.runLater(() -> input.requestFocus());

        RegexValidator validator = new RegexValidator();
        validator.setRegexPattern("^(?:([\\d])(?!.*\\1)){4}$");

        input.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                isWrongInput(validator);
            } else { //Press any key to let validator's text disappear
                input.getValidators().remove(validator);
                input.validate();
            }
        });

        btnEnter.setOnAction(event -> {
            isWrongInput(validator);
        });

    }

    private void isWrongInput(RegexValidator validator) {
        input.getValidators().add(validator);
        input.validate();

        validator.setMessage("請輸入四位不重複的數字");

        if (input.getText().matches("^(?:([\\d])(?!.*\\1)){4}$")) {
            guessNumber();
            input.clear();
        }
    }


    /*****ENTER ACTIONS*****/
    public void btnEntered(ActionEvent actionEvent) {
        menuSlide(true);
        guessNumber();
        input.clear();
    }

    /*****INITIALIZE ANSWER*****/
    private String makeAnswer() {
        StringBuilder ran = new StringBuilder("    ");
        for (int i = 0; i < 4; i++) {

            //Set every char of answer
            ran.setCharAt(i, (char) (Math.random() * 10 + 48));

            //Check if repeat
            for (int j = 0; j < i; j++) {
                if (ran.charAt(i) == ran.charAt(j)) {
                    i--;
                    break;
                }
            }
        }
        return ran.toString();
    }

    /*****************MAIN FUNCTION*********************/
    private void guessNumber() {
        String str = input.getText();

        //Main part
        int a = 0, b = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (str.charAt(i) == answer.charAt(j)) {
                    if (i == j) {
                        a++;break;
                    } else {
                        b++;break;
                    }
                }
            }
        }
        String result = a + "A" + b + "B";
        table.getItems().add(new Record(str, result));

        //You win
        if (a == 4) {
            youWin();
        }
        //You lose
        else if (guessCount++ == 6) {
            youLose();
        }
    }


    /*****GAME ENDED*****/

    /**
     * WIN
     **/
    private void youWin() {
        btnEnter.setVisible(false);
        btnNewGame.setVisible(true);

        btnNewGame.requestFocus();

        correctAnswerLabel.setVisible(true);

        gameEnded = true; //this one is for enter key disabled
        input.setDisable(true);
    }

    /**
     * LOSE
     **/
    private void youLose() {

        showDialog("您已經輸了", "您的輸入超過7次，您已經輸了\n" +
                "正確答案是" + answer);

        btnEnter.setVisible(false);
        btnNewGame.setVisible(true);

        gameEnded = true; //this one is for enter key disabled
        input.setDisable(true);
    }

    /*****NEW GAME*****/
    public void btnNewGame(ActionEvent actionEvent) {
        menuSlide(true);
        newGame();
    }

    public void keyNewGame(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER && gameEnded) {
            menuSlide(true);
            newGame();
        }
    }

    private void newGame() {
        table.getItems().clear(); //Remove all data

        gameEnded = false;

        input.setDisable(false); //Let text-field can type again
        input.requestFocus(); //Focus on text-field

        guessCount = 0;
        answer = makeAnswer();

        btnEnter.setVisible(true);
        btnNewGame.setVisible(false);

        correctAnswerLabel.setVisible(false);
    }


    /*****SLIDE MENU BAR EFFECT*****/
    public void menuSlideOutAction(ActionEvent actionEvent) {
        menuSlide(false);
    }

    private void menuSlide(boolean forClose) {
        TranslateTransition openMenu = new TranslateTransition(new Duration(200), menuBar);
        openMenu.setToX(0);
        TranslateTransition closeMenu = new TranslateTransition(new Duration(200), menuBar);

        if (menuBar.getTranslateX() != 0 && !forClose) {
            openMenu.play();
            menuBar.requestFocus();
        } else {
            closeMenu.setToX(-(menuBar.getWidth()));
            closeMenu.play();
        }
    }

    /***********MENU BAR***********/
    public void menuItemClicked(MouseEvent mouseEvent) {
        if (menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("新遊戲"))) {
            newGame();
            menuSlide(false);
        }

        /***STYLE***/
        else if (menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("更換樣式"))) {
            showDialog("Sorry", "更換樣式尚未開放");
        }

        /***HELP***/
        else if (menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("玩法"))) {

            showDialog("玩法", "程式會亂數產生4個不重複的數字(1~9)，\n" +
                            "使用者請輸入一組數字，程式會根據這個數字給出幾A幾B，\n" +
                            "其中A前面的數字表示位置正確的數的個數，\n" +
                            "而B前的數字表示數字正確而位置不對的數的個數。\n" +
                            "當輸入次數超過7次時，則代表您輸了。");
        }

        /***ABOUT***/
        else if (menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("關於"))) {

            FlowPane flowPane = new FlowPane();
            flowPane.getStyleClass().add("about");

            Label author = new Label("Author  : Beneb Siny (Github : ");
            Hyperlink link = new Hyperlink("https://github.com/benebsiny");
            link.setOnAction(event -> {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/benebsiny"));
                    } catch (IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            Label author1 = new Label(" )   ");
            Label label = new Label("Version : 1.2.0\n\nCopyright © 2019 Beneb Siny. All rights reserved");

            flowPane.getChildren().addAll(author, link, author1, label);

            Alert alert = showAlert(Alert.AlertType.NONE,"關於","");
            alert.setHeaderText("猜數字遊戲");
            alert.getDialogPane().contentProperty().set(flowPane);
            alert.showAndWait();
        }

        /***EXIT***/
        else if (menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("離開"))) {
            Platform.exit();
        }
    }

    public void focusOnMainPane(MouseEvent mouseEvent) {
        menuSlide(true);
    }


    /*****Dialog*****/
    private void showDialog(String title, String body) {

        //Styling
        Text hd = new Text(title);
        hd.setFill(Color.WHITE);
        hd.setFont(new Font(18));

        Text bd = new Text(body);
        bd.setFill(Color.WHITE);
        bd.setFont(new Font(13));

        //Content
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(hd);
        content.setBody(bd);

        content.setStyle("-fx-background-color: #252526;"); //Dialog's background color

        JFXDialog dialog = new JFXDialog(dialogPane, content, JFXDialog.DialogTransition.CENTER);

        JFXButton btnDialogEnter = new JFXButton("確定");
        btnDialogEnter.setOnAction(event -> {
            Platform.runLater(()->btnNewGame.requestFocus()); //While lose the game, focused on the "New Game Button"
            dialog.close();
        });
        btnDialogEnter.setStyle("-fx-text-fill: #d8d8d8;-fx-font-size: 14");

        content.setActions(btnDialogEnter);

        dialog.show();
        dialog.setOnDialogOpened(event -> btnDialogEnter.requestFocus());

    }

    private Alert showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);

        //Styling
        DialogPane dialog = alert.getDialogPane();
        dialog.getStylesheets().add(getClass().getResource("DarkTheme.css").toExternalForm());
        dialog.getStyleClass().add("alert");

        //Icon
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("sample/GuessNumber.png"));


        //Close button
        alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = alert.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        //Content
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        return alert;
    }
}