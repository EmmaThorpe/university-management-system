package cs308.group7.usms.ui;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.lang.reflect.Field;
import java.util.*;

public class MainUI {
    Stage currentStage = new Stage();
    String css = this.getClass().getResource("/css/style.css").toExternalForm();
    Scene currScene;

    protected Map<String, Node> currentFields;

    protected Map<String, Text> currentText;

    protected Map<String, Button> currentButtons;

    protected Map<String, Boolean> validFields;

    protected Map<String, Dialog> currentModals;

    protected Map<String, String> currentValues;

    public Map<String, String> getValues(){
        return currentValues;
    }


    /**  Display the first scene and shows the stage
     */
    public void displayFirstScene() {
        Application.setUserAgentStylesheet(css);
        currentStage.setScene(currScene);
        currentStage.setWidth(1196);
        currentStage.setHeight(672);
        currentStage.showAndWait();

    }


    /**Switches out scenes to display a new one
     */
    public void displayScene() {
        currentStage.setScene(currScene);
        currentStage.setWidth(1196);
        currentStage.setHeight(672);
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
        currentModals = new HashMap<>();
        currentValues =  new HashMap<>();
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

    public Map<String, Dialog> getCurrentModals(){return currentModals;}

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


    protected ToggleButton setToggleOption(ToggleGroup group, String operatorName, FontIcon icon) {
        String operatorNameDisplay = operatorName.toUpperCase();
        icon.getStyleClass().add("card-graphic");
        ToggleButton op = new ToggleButton(operatorNameDisplay, icon);
        op.setToggleGroup(group);
        op.setUserData(operatorName.toLowerCase());
        op.setContentDisplay(ContentDisplay.TOP);
        op.getStyleClass().add("card-toggle");
        return op;
    }


    protected ToggleButton setToggleOption(ToggleGroup group, String operatorName) {
        String operatorNameDisplay = operatorName.toUpperCase();
        ToggleButton op = new ToggleButton(operatorNameDisplay);
        op.setToggleGroup(group);
        op.setUserData(operatorName);
        op.setContentDisplay(ContentDisplay.TOP);
        op.getStyleClass().add("card-toggle");
        return op;
    }


    protected VBox makePanel(VBox content) {
        content.setPadding(new Insets(20));
        content.setSpacing(20.0);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("panel");

        return panel;
    }

    protected VBox makeTopPanel(VBox content) {
        content.setPadding(new Insets(10));
        content.setSpacing(20.0);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("top-panel");

        return panel;
    }

    protected VBox makeBottomPanel(VBox content) {
        content.setPadding(new Insets(20));
        content.setSpacing(20.0);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("bottom-panel");

        return panel;
    }

    protected VBox makePanelWithAction(VBox content, Button action) {
        content.setPadding(new Insets(20));

        HBox btnContainer = new HBox(action);
        btnContainer.setAlignment(Pos.BOTTOM_CENTER);
        btnContainer.setPadding(new Insets(0, 0, 20, 0));

        VBox panel = new VBox(content, btnContainer);

        panel.setSpacing(5);
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

    protected VBox makeScrollableBottomPanel(ScrollPane content) {
        content.setPadding(new Insets(20));
        content.fitToHeightProperty().set(true);
        content.fitToWidthProperty().set(true);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("bottom-panel");

        return panel;
    }


    protected VBox makeScrollablePanelWithAction(ScrollPane content, Button action) {
        content.setPadding(new Insets(20));
        content.fitToHeightProperty().set(true);
        content.fitToWidthProperty().set(true);

        HBox btnContainer = new HBox(action);
        btnContainer.setAlignment(Pos.BOTTOM_CENTER);
        btnContainer.setPadding(new Insets(0, 0, 20, 0));

        VBox panel = new VBox(content, btnContainer);

        panel.setSpacing(5);
        panel.getStyleClass().add("panel");

        return panel;
    }

    protected VBox makeScrollablePart(VBox content) {
        ScrollPane scrollPart = new ScrollPane(content);
        scrollPart.setPadding(new Insets(20));
        scrollPart.fitToHeightProperty().set(true);
        scrollPart.fitToWidthProperty().set(true);
        return new VBox(scrollPart);
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

    public ComboBox makeDropdown(List<String> choices) {
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

    /*
    Modal
     */
    protected Dialog makeModal(Button trigger, String btnTxt, VBox modalContent, boolean disabled) {
        DialogPane modalDialog = new DialogPane();

        modalDialog.getStyleClass().add("modal");

        Text headerTitle = new Text(btnTxt.toUpperCase());
        headerTitle.getStyleClass().add("modal-header-text");
        HBox header = new HBox();
        header.getStyleClass().add("modal-header");
        header.setId("");


        header.getChildren().add(headerTitle);
        header.setSpacing(10);

        modalDialog.setHeader(header);

        Button actionBtn = inputButton(btnTxt.toUpperCase());

        if(disabled){
           actionBtn.setDisable(true);
        }

        HBox btnContainer = modalButtonBar(actionBtn, modalDialog);
        VBox content = new VBox(modalContent, btnContainer);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        modalDialog.setContent(content);

        Dialog modal = new Dialog();
        modal.setDialogPane(modalDialog);
        trigger.setOnAction(e -> {
            modal.showAndWait();
        });

        Window modalWindow = modal.getDialogPane().getScene().getWindow();
        modalWindow.setOnCloseRequest(windowEvent -> modalWindow.hide());

        currentModals.put(btnTxt, modal);
        return modal;
    }



    public void makeNotificationModal(String openedModal,String modalContent, boolean isSuccess) {
        if (openedModal != null) {
            closeOpenedModal(openedModal);
        }

        DialogPane modalDialog = new DialogPane();

        modalDialog.getStyleClass().add("modal");

        Text headerTitle;

        if(isSuccess){
            headerTitle = new Text("SUCCESS");
        }else{
            headerTitle = new Text("ERROR");
        }
        headerTitle.getStyleClass().add("modal-header-text");
        HBox header = new HBox();

        FontIcon modalGraphic;
        if (isSuccess) {
            header.getStyleClass().add("modal-header-suc");
            modalGraphic = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
            modalGraphic.getStyleClass().add("modal-graphic");
            header.getChildren().add(modalGraphic);
            header.setId("SUCCESS");
        } else{
            header.getStyleClass().add("modal-header-err");
            modalGraphic = new FontIcon(FontAwesomeSolid.TIMES_CIRCLE);
            modalGraphic.getStyleClass().add("modal-graphic");
            header.getChildren().add(modalGraphic);
            header.setId("ERROR");
        }

        header.getChildren().add(headerTitle);
        header.setSpacing(10);

        modalDialog.setHeader(header);
        VBox content = new VBox(new Text(modalContent));
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        modalDialog.setContent(content);

        Dialog modal = new Dialog();
        modal.setDialogPane(modalDialog);

        Window modalWindow = modal.getDialogPane().getScene().getWindow();
        modalWindow.setOnCloseRequest(windowEvent -> modalWindow.hide());

        modal.showAndWait();
    }

    protected void setModalContent(String modalName, VBox updateContent){
        Dialog modal = currentModals.get(modalName);
        VBox modalContent = (VBox) modal.getDialogPane().getContent();
        modalContent.getChildren().set(0, updateContent);
        currentModals.replace(modalName, modal);
    }

    protected void closeOpenedModal(String modalKey) {
        Scene scene = currentModals.get(modalKey).getDialogPane().getScene();
        Window currentModalWindow = currentModals.get(modalKey).getDialogPane().getScene().getWindow();
        currentModalWindow.hide();
    }

    protected HBox modalButtonBar(Button action, DialogPane modal){
        ButtonType cancelButtonType = new ButtonType("CANCEL", ButtonBar.ButtonData.CANCEL_CLOSE);

        modal.getButtonTypes().addAll(cancelButtonType);

        Button cancelButton = (Button) modal.lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("outline-button");

        HBox btnContainer = new HBox(action, cancelButton);

        modal.getButtonTypes().removeAll(cancelButtonType);

        btnContainer.setSpacing(10.0);
        btnContainer.setAlignment(Pos.BOTTOM_RIGHT);
        btnContainer.setPadding(new Insets(20, 0, 0, 0));
        return btnContainer;
    }
    protected HBox makeListButton(String id, FontIcon listGraphic, VBox listContent) {
        Label nameDisplay = new Label(id);
        nameDisplay.getStyleClass().add("list-id");

        StackPane iconStack = makeCircleIcon(25, "list-back" ,listGraphic, "list-graphic");

        listContent.setSpacing(5.0);

        HBox listButton = new HBox(nameDisplay, iconStack, listContent);
        listButton.setAlignment(Pos.CENTER);
        listButton.setSpacing(20.0);
        listButton.setPadding(new Insets(10));
        listButton.getStyleClass().add("list-button");

        HBox.setHgrow(listContent, Priority.ALWAYS);
        return listButton;
    }

    /*
     List
     */
    protected HBox listDetail(String title, String content){
        HBox titleDisplay = new HBox(new Text(title.toUpperCase()));
        titleDisplay.getStyleClass().add("list-detail");

        Text contentDisplay =new Text(content);

        HBox detail = new HBox(titleDisplay, contentDisplay);
        detail.setPadding(new Insets(10));
        detail.setSpacing(10);

        return detail;
    }



    protected HBox activeDetail(String text, Boolean isActive){
        HBox activatedDisplay = new HBox();
        if (isActive) {
            activatedDisplay.getChildren().add(new Text(text));
            activatedDisplay.getStyleClass().add("list-active");
        } else {
            activatedDisplay.getChildren().add(new Text(text));
            activatedDisplay.getStyleClass().add("list-inactive");
        }
        return activatedDisplay;
    }

    protected VBox infoContainer(VBox content) {
        content.setPadding(new Insets(10));
        content.setSpacing(5);
        VBox infoBox = new VBox(content);
        infoBox.getStyleClass().add("info-box");

        return infoBox;
    }

    protected VBox infoDetailLong(String title, String content){
        HBox titleDisplay = new HBox(new Text(title.toUpperCase()));
        titleDisplay.getStyleClass().add("list-detail");

        Text contentDisplay =new Text(content);
        contentDisplay.setWrappingWidth(370);

        VBox detail = new VBox(titleDisplay, contentDisplay);
        detail.setPadding(new Insets(10));
        detail.setSpacing(10);

        return detail;
    }

    public void createScene(String top, Pane mainContent, Pane bottom){
        VBox title = setTitle(top);

        BorderPane root = new BorderPane(mainContent);
        root.setTop(title);
        root.setBottom(bottom);

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }

    /*
     Input
     */


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



    protected VBox inputFieldSetValue(String text, String value){
        Label label=new Label(text);
        TextField field = new TextField(value);

        currentFields.put(text, field);

        VBox inputField = new VBox(label, field);
        inputField.setPadding(new Insets(10));

        return inputField;
    }

    protected VBox inputFieldLong(String text){
        Label label=new Label(text);
        TextArea field=new TextArea();
        field.setWrapText(true);
        field.setPrefRowCount(4);

        currentFields.put(text, field);

        VBox inputField = new VBox(label, field);
        inputField.setPadding(new Insets(10));

        return inputField;
    }

    protected VBox inputFieldLongSetValue(String text, String value){
        Label label=new Label(text);
        TextArea field=new TextArea(value);
        field.setWrapText(true);
        field.setPrefRowCount(4);

        currentFields.put(text, field);

        VBox inputField = new VBox(label, field);
        inputField.setPadding(new Insets(10));

        return inputField;
    }



    protected VBox dropdownField(String text, List<String> choices){
        Label label=new Label(text);
        ComboBox field = makeDropdown(choices);

        currentFields.put(text, field);

        VBox inputField = new VBox(label, field);
        inputField.setPadding(new Insets(10));

        return inputField;
    }






    protected Text inputText(String name){
        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");
        currentText.put(name, inputText);
        return inputText;
    }

    protected VBox textAndField(String text, ChangeListener<String> listener){
        Label label=new Label(text);
        TextField field=new TextField();
        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");

        currentFields.put(text, field);
        currentText.put(text, inputText);

        field.textProperty().addListener(listener);

        validFields.put(text, false);

        return new VBox(label, field, inputText);
    }

    protected VBox longTextAndField(String text, ChangeListener<String> listener){
        Label label=new Label(text);
        TextArea field=new TextArea();
        field.setWrapText(true);
        field.setPrefRowCount(4);

        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");

        currentFields.put(text, field);
        currentText.put(text, inputText);

        field.textProperty().addListener(listener);

        validFields.put(text, false);

        return new VBox(label, field, inputText);
    }

    protected VBox setTextAndField(String text, String value, ChangeListener<String> listener, Boolean valid){
        Label label=new Label(text);
        TextField field=new TextField(value);
        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");

        currentFields.put(text, field);
        currentText.put(text, inputText);

        field.textProperty().addListener(listener);

        validFields.put(text, valid);

        return new VBox(label, field, inputText);
    }

    protected VBox setLongTextAndField(String text, String value, ChangeListener<String> listener, Boolean valid){
        Label label=new Label(text);
        TextArea field=new TextArea(value);
        field.setWrapText(true);
        field.setPrefRowCount(4);

        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");

        currentFields.put(text, field);
        currentText.put(text, inputText);

        field.textProperty().addListener(listener);

        validFields.put(text, valid);

        return new VBox(label, field, inputText);
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







    public boolean validPassword(String password, Text output){
        String specialChars = "@!#$%&/()=?@£{}.-;<>_,*";
        boolean upperCharacter = false;
        boolean lowerCharacter = false;
        boolean number = false;
        boolean specialCharacter = false;

        for (int i = 0; i < password.length(); i++){
            char curr = password.charAt(i);

            if(Character.isUpperCase(curr)){
                upperCharacter = true;
            }else if(Character.isLowerCase(curr)){
                lowerCharacter = true;
            }else if(Character.isDigit(curr)){
                number = true;
            }else if(specialChars.contains(Character.toString(curr))){
                specialCharacter = true;
            }else{
                output.setText("Contains character not allowed in passwords");
                return false;
            }
        }

        if(!upperCharacter){
            output.setText("Password must contain an uppercase letter");
            return false;
        }else if(!lowerCharacter){
            output.setText("Password must contain a lowercase letter");
            return false;
        }else if(!number){
            output.setText("Password must contain a number");
            return false;
        }else if(!specialCharacter){
            output.setText("Password must contain a special letter");
            return false;
        }
        return true;
    }



}
