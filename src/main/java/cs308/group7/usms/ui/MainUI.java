package cs308.group7.usms.ui;

import cs308.group7.usms.model.User;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.util.*;

public class MainUI {
    Stage currentStage = new Stage();
    String css = this.getClass().getResource("/css/style.css").toExternalForm();
    Scene currScene;

    Map<String, Node> currentFields;

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
        currentFields = new HashMap<>();
        currentText = new HashMap<>();
        currentButtons = new HashMap<>();

    }


    /**gets the text fields currently shown on the page being shown
     * @return Map of text fields
     */
    public Map<String, Node> getCurrentFields(){
        return currentFields;
    }



    /** Gets the current text being shown on the stage currently
     * @return Current text displayed on scene
     */
    public Map<String, Text> getCurrentText(){
        return currentText;
    }


    public Map<String, Button> getCurrentButtons(){return currentButtons;}

    /**
     * Global components to be used across multiple UIs
     **/

    private VBox setTitle(String titleText) {
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        StackPane iconStack = makeCircleIcon(35, "login-back" ,appGraphic, "login-graphic");

        Text titleName = new Text(titleText);
        titleName.getStyleClass().add("login-title");

        VBox title = new VBox(iconStack, titleName);
        title.setAlignment(Pos.TOP_CENTER);
        return title;
    }

    protected HBox makeToolbar(String role) {
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        StackPane iconStack = makeCircleIcon(25, "toolbar-back" ,appGraphic, "toolbar-graphic");

        Text title = new Text(role);
        title.setTranslateY(5.0);
        title.getStyleClass().add("toolbar-title");

        HBox titleContainer = new HBox(iconStack, title);
        titleContainer.setPadding(new Insets(10));
        titleContainer.setSpacing(10);

        Button homeBtn = inputButton("HOME");
        homeBtn.getStyleClass().add("toolbar-btn");
        Button logoutBtn = inputButton("LOG OUT");
        logoutBtn.getStyleClass().add("toolbar-btn");
        HBox.setMargin(logoutBtn, new Insets(10));

        HBox logoutContainer = new HBox(homeBtn, logoutBtn);
        logoutContainer.setAlignment(Pos.CENTER);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox container = new HBox(titleContainer, region, logoutContainer);
        container.setPadding(new Insets(15));
        container.setSpacing(50);
        container.getStyleClass().add("toolbar-bar");

        return container;
    }
    protected VBox makePanel(VBox content) {
        content.setPadding(new Insets(20));
        content.setSpacing(20.0);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("panel");

        return panel;
    }

    protected VBox makeScrollablePanel(ScrollPane content) {
        content.setPadding(new Insets(20));
        content.fitToHeightProperty().set(true);
        content.fitToWidthProperty().set(true);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("panel");

        return panel;
    }

    protected Button[] stylePanelActions (Button[] btns) {
        int i = 0;
        for (Button btn: btns) {
            if (i % 2 == 0) {
                btn.getStyleClass().add("panel-button-1");
            } else {
                btn.getStyleClass().add("panel-button-2");
            }
            i++;
        }
        return btns;
    }
    protected void makeModal(Button trigger, String headTxt, VBox modalContent, boolean isSuccess, boolean isError) {
        DialogPane modalDialog = new DialogPane();

        modalDialog.getStyleClass().add("modal");

        Text headerTitle = new Text(headTxt.toUpperCase());
        headerTitle.getStyleClass().add("modal-header-text");
        HBox header = new HBox();

        FontIcon modalGraphic;
        if (isSuccess) {
            header.getStyleClass().add("modal-header-suc");
            modalGraphic = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
            modalGraphic.getStyleClass().add("modal-graphic");
            header.getChildren().add(modalGraphic);
        } else if (isError) {
            header.getStyleClass().add("modal-header-err");
            modalGraphic = new FontIcon(FontAwesomeSolid.TIMES_CIRCLE);
            modalGraphic.getStyleClass().add("modal-graphic");
            header.getChildren().add(modalGraphic);
        } else {
            header.getStyleClass().add("modal-header");
        }

        header.getChildren().add(headerTitle);
        header.setSpacing(10);

        VBox content = new VBox(modalContent);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        modalDialog.setHeader(header);
        modalDialog.setContent(content);

        ButtonType okButton = new ButtonType("CONFIRM", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("CANCEL", ButtonBar.ButtonData.CANCEL_CLOSE);

        modalDialog.getButtonTypes().addAll(okButton, cancelButton);
        modalDialog.lookupButton(cancelButton).getStyleClass().add("outline-button");

        Dialog modal = new Dialog();
        modal.setDialogPane(modalDialog);
        trigger.setOnAction(e -> {
            modal.showAndWait();
        });
    }
    public ComboBox makeDropdown(ArrayList<String> choices) {
        ComboBox choiceDropdown = new ComboBox();
        for (String choice : choices) {
            choiceDropdown.getItems().add(choice);
        }
        choiceDropdown.getSelectionModel().select(0);
        return choiceDropdown;
    }

    public StackPane makeCircleIcon(Integer radius, String backgroundStyle, FontIcon icon, String graphicStyle){
        StackPane iconStack = new StackPane();
        Circle appGraphicBack = new Circle(radius);
        appGraphicBack.getStyleClass().add(backgroundStyle);
        FontIcon appGraphic = icon;
        appGraphic.getStyleClass().add(graphicStyle);
        iconStack.getChildren().addAll(appGraphicBack, appGraphic);
        return iconStack;
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

        currentFields.put(text, field);

        VBox inputField = new VBox(label, field);
        inputField.setPadding(new Insets(10));

        return inputField;
    }

    protected VBox dropdownField(String text, ArrayList<String> choices){
        Label label=new Label(text);
        ComboBox field = makeDropdown(choices);

        currentFields.put(text, field);

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

    protected void twoPanelLayout(VBox left, VBox right, String title){

        HBox toolbar = makeToolbar(title);

        HBox actionPanel = new HBox(left, right);

        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        BorderPane root = new BorderPane(actionPanel);
        root.setTop(toolbar);
        BorderPane.setMargin(toolbar, new Insets(15));
        BorderPane.setMargin(actionPanel, new Insets(15));

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }


}
