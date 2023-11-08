package cs308.group7.usms.ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class MainUI {
    Stage currentStage = new Stage();
    String css = this.getClass().getResource("/css/style.css").toExternalForm();
    Scene currScene;

    Map<String, TextField> currentTextFields;

    Map<String, Text> currentText;

    Map<String, Button> currentButtons;


    /**  Display the first scene and shows the stage
     */
    public void displayFirstScene() {
        Application.setUserAgentStylesheet(css);
        currentStage.setScene(currScene);
        currentStage.showAndWait();

    }


    /**Switches out scenes to display a new one
     */
    public void displayScene() {
        currentStage.setScene(currScene);

    }

    /**
     * Close the current stage
     */
    public void hideStage() {
        currentStage.close();

    }


    public void resetCurrentValues(){
        currentTextFields = new HashMap<>();
        currentText = new HashMap<>();
        currentButtons = new HashMap<>();

    }


    /**gets the text fields currently shown on the page being shown
     * @return Map of text fields
     */
    public Map<String, TextField> getCurrentTextFields(){
        return currentTextFields;
    }



    /** Gets the current text being shown on the stage currently
     * @return Current text displayed on scene
     */
    public Map<String, Text> getCurrentText(){
        return currentText;
    }


    public Map<String, Button> getCurrentButtons(){return currentButtons;}


    private VBox setTitle(String titleText) {
        StackPane iconStack = new StackPane();
        Circle appGraphicBack = new Circle(35);
        appGraphicBack.getStyleClass().add("login-back");
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        appGraphic.getStyleClass().add("login-graphic");

        iconStack.getChildren().addAll(appGraphicBack, appGraphic);


        Text titleName = new Text(titleText);
        titleName.getStyleClass().add("login-title");

        VBox title = new VBox(iconStack, titleName);
        title.setAlignment(Pos.TOP_CENTER);
        return title;
    }


    public void createScene(String top, Pane mainContent, Pane bottom){
        VBox title = setTitle(top);

        BorderPane root = new BorderPane(mainContent);
        root.setTop(title);
        root.setBottom(bottom);

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }

    protected VBox inputField(String text, Boolean password){
        Label label=new Label(text);

        TextField field;
        if(password){
            field=new PasswordField();
        }else{
            field=new TextField();
        }


        currentTextFields.put(text, field);

        VBox inputField = new VBox(label, field);
        inputField.setPadding(new Insets(10));

        return inputField;
    }



    protected Text inputText(String text){
        Text inputText = new Text();
        currentText.put(text, inputText);
        return inputText;
    }


    protected Button inputButton(String text){
        Button button = new Button(text);
        currentButtons.put(text, button);
        return button;
    }

    protected HBox bottomButtons(HBox buttons){
        buttons.setSpacing(20.0);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setPadding(new Insets(20, 0, 0, 0));
        return buttons;
    }


    public void notificationScene(String mainText, String buttonText, boolean tick){

        HBox notification = setNotficationCard(mainText, tick);
        notification.setFillHeight(false);

        Button returnBtn = inputButton(buttonText);

        HBox btnContainer = new HBox(returnBtn);
        btnContainer.setPadding(new Insets(20, 0, 0, 0));
        btnContainer.setAlignment(Pos.BOTTOM_CENTER);

        createScene("NOTICE", notification, btnContainer);
    }


    private HBox setNotficationCard(String msg, Boolean tick) {
        FontIcon notfiGraphic;
        if(tick){
            notfiGraphic = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
            notfiGraphic.getStyleClass().add("success-graphic");
        }else{
            notfiGraphic = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
            notfiGraphic.getStyleClass().add("notfi-graphic");
        }

        Label notfiMsg = new Label(msg, notfiGraphic);
        notfiMsg.setPadding(new Insets(10));
        notfiMsg.setWrapText(true);
        notfiMsg.setMaxWidth(250.0);
        notfiMsg.setGraphicTextGap(20.0);

        HBox notfiCard = new HBox(notfiMsg);
        notfiCard.setPadding(new Insets(20));
        notfiCard.getStyleClass().add("card");

        return notfiCard;
    }

}
