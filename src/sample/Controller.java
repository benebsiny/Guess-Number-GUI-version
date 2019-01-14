package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


public class Controller {

    @FXML
    public TextField input;
    public Button btnEnter;
    public Button btnNewGame;
    public Button btnOpenMenu;
    public TableView<Record> table;
    public Label correctAnswerLabel;
    public AnchorPane menuBar;
    public ListView<String> menuItems;

    private int guessCount = 0;
    private String answer;
    private boolean gameEnded;

    private TableColumn inputColumn = new TableColumn("輸入");
    private TableColumn resultColumn = new TableColumn("結果");

    @FXML
    public void initialize(){

        guessCount = 0;
        answer = makeAnswer();

        inputColumn.setSortable(false);
        resultColumn.setSortable(false);

        inputColumn.setCellValueFactory(new PropertyValueFactory<>("input")); //Fetch "input" type from Record class
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result")); //Fetch "result" type from Record class
        table.getColumns().addAll(inputColumn, resultColumn);

        btnEnter.setVisible(true);
        btnNewGame.setVisible(false);

        correctAnswerLabel.setVisible(false);

        ObservableList<String> menuList = FXCollections.observableArrayList("新遊戲","更換樣式","玩法","關於","離開");
        menuItems.setItems(menuList);
    }


    /*****ENTER ACTIONS*****/
    public void btnEntered(ActionEvent actionEvent) {
        menuSlide(true);
        guessNumber();
        input.clear();
    }

    public void keyEntered(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER && !gameEnded){
            menuSlide(true);
            guessNumber();
            input.clear();
        }
    }


    /*****INITIALIZE ANSWER*****/
    private String makeAnswer(){
        StringBuilder ran = new StringBuilder("    ");
        for(int i=0; i<4; i++){

            //Set every char of answer
            ran.setCharAt(i, (char)(Math.random()*9+49));

            //Check if repeat
            for(int j=0; j<i; j++){
                if(ran.charAt(i) == ran.charAt(j)){
                    i--;
                    break;
                }
            }
        }
        return ran.toString();
    }


    /*****************MAIN FUNCTION*********************/
    private void guessNumber(){
        String str = input.getText();

        //Main part
        if(!wrongInput(str)){
            int a = 0, b = 0;

            for (int i=0; i<4; i++){
                for (int j=0; j<4; j++){
                    if (str.charAt(i)==answer.charAt(j)){
                        if (i==j){
                            a++; break;
                        }
                        else{
                            b++; break;
                        }
                    }
                }
            }

            String result = a + "A" + b + "B";

            table.getItems().add(new Record(str,result));

            //Correct Answer
            if (a == 4){
                youWin();
            }
            else if(guessCount++ == 6){
                youLose();
            }
        }
        else{
            wrongInputDialog();
        }
    }


    /*****GAME ENDED*****/

    /**WIN**/
    private void youWin(){
        btnEnter.setVisible(false);
        btnNewGame.setVisible(true);

        btnNewGame.requestFocus();

        correctAnswerLabel.setVisible(true);

        gameEnded = true; //this one is for enter key disabled
        input.setDisable(true);
    }

    /**LOSE**/
    private void youLose() {
        Alert alert = showDialog(Alert.AlertType.INFORMATION,"您已經輸了","您的輸入超過7次，您已經輸了\n" +
                "正確答案是"+answer);
        alert.showAndWait();

        btnEnter.setVisible(false);
        btnNewGame.setVisible(true);

        btnNewGame.requestFocus();

        gameEnded = true; //this one is for enter key disabled
        input.setDisable(true);
    }

    /*****NEW GAME*****/
    public void btnNewGame(ActionEvent actionEvent) {
        menuSlide(true);
        newGame();
    }
    public void keyNewGame(KeyEvent keyEvent) {
        if(keyEvent.getCode()==KeyCode.ENTER && gameEnded){
            menuSlide(true);
            newGame();
        }
    }

    private void newGame(){
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
    public void menuSlideOutAction(ActionEvent actionEvent){
        menuSlide(false);
    }

    void menuSlide(boolean forClose){
        TranslateTransition openMenu = new TranslateTransition(new Duration(200), menuBar);
        openMenu.setToX(0);
        TranslateTransition closeMenu = new TranslateTransition(new Duration(200), menuBar);

        if(menuBar.getTranslateX()!=0 && !forClose){
            openMenu.play();
            menuBar.requestFocus();
        }
        else{
            closeMenu.setToX(-(menuBar.getWidth()));
            closeMenu.play();
        }
    }

    /***********MENU BAR***********/
    public void menuItemClicked(MouseEvent mouseEvent) {
        if(menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("新遊戲"))){
            newGame();
            menuSlide(false);
        }

        /***STYLE***/
        else if(menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("更換樣式"))){
            Alert alert = showDialog(Alert.AlertType.INFORMATION,"Sorry!","更換樣式尚未開放");
            alert.showAndWait();
        }

        /***HELP***/
        else if(menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("玩法"))){

            Alert alert = showDialog(Alert.AlertType.INFORMATION,"玩法","程式會亂數產生4個不重複的數字(1~9)，" +
                    "使用者請輸入一組數字，程式會根據這個數字給出幾A幾B，" +
                    "其中A前面的數字表示位置正確的數的個數，而B前的數字表示數字正確而位置不對的數的個數。\n" +
                    "當輸入次數超過7次時，則代表您輸了。");
            alert.showAndWait();
        }

        /***ABOUT***/
        else if(menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("關於"))){

            FlowPane flowPane = new FlowPane();
            flowPane.getStyleClass().add("about");

            Label author = new Label("Author  : Beneb Siny (Github : ");
            Hyperlink link = new Hyperlink("https://github.com/benebsiny");
            link.setOnAction(event -> {
                if(Desktop.isDesktopSupported()){
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/benebsiny"));
                    } catch (IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            Label author1 = new Label(" )   ");
            Label label = new Label("Version : 1.1.0\n\nCopyright © 2019 Beneb Siny. All rights reserved");

            flowPane.getChildren().addAll(author,link,author1,label);

            Alert alert = showDialog(Alert.AlertType.NONE,"關於","");
            alert.setHeaderText("猜數字遊戲");
            alert.getDialogPane().contentProperty().set(flowPane);
            alert.showAndWait();
        }

        /***EXIT***/
        else if(menuItems.getSelectionModel().getSelectedItems().equals(FXCollections.observableArrayList("離開"))){
            Alert alert = showDialog(Alert.AlertType.CONFIRMATION,"關閉程式","確定要關閉遊戲?");

            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK){ //ok button is pressed
                Platform.exit();
            }
        }
    }

    public void focusOnMainPane(MouseEvent mouseEvent) {
        menuSlide(true);
    }


    /*****EXCEPTION*****/
    private void wrongInputDialog(){
        Alert alert = showDialog(Alert.AlertType.ERROR,"輸入錯誤","請輸入4個不重複的數字(1~9)");
        alert.showAndWait();
    }

    private boolean wrongInput(String str){

        //If entered 4 words
        if(!str.matches("[1-9]{4}")){
            return true;
        }

        //If repeated
        for(int i=0; i<3; i++){
            for(int j=i+1; j<4; j++){
                if(str.charAt(i)==str.charAt(j)){
                    return true;
                }
            }
        }
        return false;
    }


    /*****Dialog*****/
    Alert showDialog(Alert.AlertType alertType, String title, String content){
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
