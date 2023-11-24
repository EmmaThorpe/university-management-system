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
import java.util.*;


//MainUI creates basic elements and scenes
public class MainUI {
    protected Stage currentStage = new Stage();
    protected Scene currScene;

    //loads stylesheet
    String css = this.getClass().getResource("/css/style.css").toExternalForm();


    //maps

    //current fields that the ui is showing
    protected Map<String, Node> currentFields;

    //current text that the ui is showing
    protected Map<String, Text> currentText;

    //current buttons that the ui is showing
    protected Map<String, Button> currentButtons;

    //current modals that the ui could show
    protected Map<String, Dialog> currentModals;

    //current values that the ui is storing
    protected Map<String, String> currentValues;

    //a map of whether fields are valid or not (used for input validation)
    protected Map<String, Boolean> validFields;



    /*
     * SCENE CONTROL
     */


    /**
     * Display the first scene and shows the stage
     */
    public void displayFirstScene() {
        Application.setUserAgentStylesheet(css);
        currentStage.setScene(currScene);
        currentStage.setWidth(1196);
        currentStage.setHeight(672);
        currentStage.showAndWait();

    }


    /*
     *Switches out scenes to display a new one
     */
    public void displayScene() {
        currentStage.setScene(currScene);
        currentStage.setWidth(1196);
        currentStage.setHeight(672);
    }


    /**
     * Closes the current stage
     */
    public void hideStage() {
        currentStage.close();

    }


    /** Creates a scene by taking in a top, main content and bottom
     * @param top - String containing title to put on the scene
     * @param mainContent - Pane containing the main content to show
     * @param bottom - Pane containg content to show at bottom of scene
     */
    public void createScene(String top, Pane mainContent, Pane bottom){
        VBox title = setTitle(top);

        BorderPane root = new BorderPane(mainContent);
        root.setTop(title);
        root.setBottom(bottom);

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }


    /*
     * VALUE CONTROL
     */

    /**
     * Resets the currently stored values
     */
    public void resetCurrentValues(){
        currentFields = new HashMap<>();
        currentText = new HashMap<>();
        currentButtons = new HashMap<>();
        currentModals = new HashMap<>();
        currentValues =  new HashMap<>();
    }


    /**gets the values currently shown on the page being shown
     * @return Map of the current values being shown in the ui
     */
    public Map<String, String> getValues(){
        return currentValues;
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


    /** Gets the current buttons being shown on the stage currently
     * @return Current buttons being displayed
     */
    public Map<String, Button> getCurrentButtons(){return currentButtons;}



    /*
     * TITLES AND ICON
     */


    /** Creates a title card
     * @param titleText - text to be set as the title
     * @return - a VBox containing the title
     */
    private VBox setTitle(String titleText) {
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        StackPane iconStack = makeCircleIcon(35, "login-back" ,appGraphic, "login-graphic");

        Text titleName = new Text(titleText);
        titleName.getStyleClass().add("login-title");

        VBox title = new VBox(iconStack, titleName);
        title.setAlignment(Pos.TOP_CENTER);
        return title;
    }

    /** Creates a circular icon
     * @param radius - radius of circle
     * @param backgroundStyle - string containing the id of the background styling
     * @param icon - icon contained in circle
     * @param graphicStyle - string containing the id of the graphic styling
     * @return
     */
    public StackPane makeCircleIcon(Integer radius, String backgroundStyle, FontIcon icon, String graphicStyle){
        StackPane iconStack = new StackPane();
        Circle appGraphicBack = new Circle(radius);
        appGraphicBack.getStyleClass().add(backgroundStyle);
        icon.getStyleClass().add(graphicStyle);
        iconStack.getChildren().addAll(appGraphicBack, icon);
        return iconStack;
    }


    /*
     * PANELS
     */



    /**Makes a panel and styles it
     * @param content - content to put in panel
     * @return - VBox panel
     */
    protected VBox makePanel(VBox content) {
        content.setPadding(new Insets(20));
        content.setSpacing(20.0);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("panel");

        return panel;
    }

    /** Creates a top panel
     * @param content - VBox of content to put in panel
     * @return - VBox containg the panel
     */
    protected VBox makeTopPanel(VBox content) {
        content.setPadding(new Insets(10));
        content.setSpacing(20.0);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("top-panel");

        return panel;
    }


    /** Creates a panel with an action
     * @param content - VBox containing the content to be added to panel
     * @param action - Action button
     * @return
     */
    protected VBox makePanelWithAction(VBox content, Button action) {
        content.setPadding(new Insets(20));

        return makePanelButtons(null, content, action);
    }


    /** Creates a scrollable panel
     * @param content - ScrollPane containing the content to be added to panel
     * @return - VBox containing a scrollable panel
     */
    protected VBox makeScrollablePanel(ScrollPane content) {
        content.setPadding(new Insets(20));
        content.fitToHeightProperty().set(true);
        content.fitToWidthProperty().set(true);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("panel");

        return panel;
    }


    /** Creates a bottom scrollable panel
     * @param content - ScrollPane containing the content to be added to panel
     * @return - VBox containing a scrollable panel
     */
    protected VBox makeScrollableBottomPanel(ScrollPane content) {
        content.setPadding(new Insets(20));
        content.fitToHeightProperty().set(true);
        content.fitToWidthProperty().set(true);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("bottom-panel");

        return panel;
    }

    /** Creates a scrollable panel with action
     * @param content - ScrollPane containing the content to be added to panel
     * @param action - Action button
     * @return - VBox containing a scrollable panel
     */
    protected VBox makeScrollablePanelWithAction(ScrollPane content, Button action) {
        content.setPadding(new Insets(20));
        content.fitToHeightProperty().set(true);
        content.fitToWidthProperty().set(true);

        return makePanelButtons(content, null, action);
    }


    /** Makes a panel of buttons
     * @param contentPane - ScrollPane of content
     * @param contentVBox - VBox containing content
     * @param action - Action Button
     * @return - VBox containing a panel full of buttons
     */
    private VBox makePanelButtons(ScrollPane contentPane, VBox contentVBox, Button action){
        HBox btnContainer = new HBox(action);
        btnContainer.setAlignment(Pos.BOTTOM_CENTER);
        btnContainer.setPadding(new Insets(0, 0, 20, 0));

        VBox panel;
        if(contentVBox==null){
            panel = new VBox(contentPane, btnContainer);
        }else{
            panel = new VBox(contentVBox, btnContainer);
        }


        panel.setSpacing(5);
        panel.getStyleClass().add("panel");

        return panel;
    }


    /** Creates a scrollable section
     * @param content - content containing VBox
     * @return - a VBox containing a scrollable section
     */
    protected VBox makeScrollablePart(VBox content) {
        ScrollPane scrollPart = new ScrollPane(content);
        scrollPart.setPadding(new Insets(20));
        scrollPart.fitToHeightProperty().set(true);
        scrollPart.fitToWidthProperty().set(true);
        return new VBox(scrollPart);
    }


    /** Styles the buttons
     * @param btns - Array of buttons to be styled
     * @return - Array of buttons
     */
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




    /*
     MODALS
     */


    /** Creates a modal
     * @param trigger - Button that triggers modal
     * @param btnTxt - Text for a new button
     * @param modalContent - VBox of content to put on modal
     * @param disabled - Boolean of whether it is disabled or not
     */
    protected void makeModal(Button trigger, String btnTxt, VBox modalContent, boolean disabled) {
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
        trigger.setOnAction(e -> modal.showAndWait());

        Window modalWindow = modal.getDialogPane().getScene().getWindow();
        modalWindow.setOnCloseRequest(windowEvent -> modalWindow.hide());

        currentModals.put(btnTxt, modal);
    }


    /** Creates a notification modal
     * @param openedModal - String containing the name of already opened modal (if any)
     * @param modalContent - String containing the content of modal
     * @param isSuccess - Boolean containing if the notification is a success or not
     */
    public void makeNotificationModal(String openedModal, String modalContent, boolean isSuccess) {
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
        Text modalText = new Text(modalContent);
        modalText.setWrappingWidth(200.0);
        VBox content = new VBox(modalText);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        modalDialog.setContent(content);

        Dialog modal = new Dialog();
        modal.setDialogPane(modalDialog);

        Window modalWindow = modal.getDialogPane().getScene().getWindow();
        modalWindow.setOnCloseRequest(windowEvent -> modalWindow.hide());

        modal.showAndWait();
    }


    /** Sets the modal content
     * @param modalName - String containing the name of the modal
     * @param updateContent - VBox of content
     */
    protected void setModalContent(String modalName, VBox updateContent){
        Dialog modal = currentModals.get(modalName);
        VBox modalContent = (VBox) modal.getDialogPane().getContent();
        modalContent.getChildren().set(0, updateContent);
        currentModals.replace(modalName, modal);
    }


    /** Close the opened modal
     * @param modalKey - String containing a key of the modal
     */
    protected void closeOpenedModal(String modalKey) {
        Scene scene = currentModals.get(modalKey).getDialogPane().getScene();
        Window currentModalWindow = currentModals.get(modalKey).getDialogPane().getScene().getWindow();
        currentModalWindow.hide();
    }


    /** Creates a modal bar button
     * @param action - action button
     * @param modal - DialogPane containing a modal
     * @return - A HBox containing a modal bar button
     */
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


    /**Creates a list button
     * @param id - String of id of button
     * @param listGraphic - Icon to display on button
     * @param listContent - VBox containing the list content
     * @return - HBox of a list button
     */
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
     LIST AND INFO DETAILS
     */


    /**Creates list details section
     * @param title - String containing title
     * @param content - String of content
     * @return - HBox of a list detail
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


    /** Creates an active section
     * @param text - Text of section
     * @param isActive - Boolean is active or not
     * @return - HBox containing the active section
     */
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


    /** Creates the information content
     * @param content - VBox of content
     * @return - VBox containing the information content
     */
    protected VBox infoContainer(VBox content) {
        content.setPadding(new Insets(10));
        content.setSpacing(5);
        VBox infoBox = new VBox(content);
        infoBox.getStyleClass().add("info-box");

        return infoBox;
    }


    /** Creates a long information content section
     * @param title - String of title
     * @param content - String of content
     * @return - VBox containing the long information content
     */
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





    /*
     INPUTS
     */


    /** Creates an input field
     * @param text - String of name of field
     * @param password - Boolean of if the field is a password or not
     * @return
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


    /** Creates a piece of text which will be displayed
     * @param name - String of what the text will say
     * @return the created piece of text
     */
    protected Text inputText(String name){
        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");
        currentText.put(name, inputText);
        return inputText;
    }


    /** Creates a text and a field and a listener on the field
     * @param text - The value used for the name of the text and the field
     * @param listener - The listener to be added to the field
     * @return - a VBox containing the created text and field
     */
    protected VBox textAndField(String text, ChangeListener<String> listener){
        Label label=new Label(text);
        TextField field=new TextField();
        return styleTextAndField(field, label, text, listener, false);
    }


    /** Creates a text and a field and sets the initial value of the field
     * @param text - The text used for the name of the text and the field
     * @param value - The initial value of the field
     * @param listener - The listener to be added to the field
     * @param valid - value of whether or not to set the field to initially be valid or not
     * @return - a VBox containing the created text and field
     */
    protected VBox setTextAndField(String text, String value, ChangeListener<String> listener, Boolean valid){
        Label label=new Label(text);
        TextField field=new TextField(value);

        return styleTextAndField(field, label, text, listener, valid);
    }


    /** Creates a long text and a field and a listener on the field
     * @param text - The value used for the name of the text and the field
     * @param listener - The listener to be added to the field
     * @return - a VBox containing the created text and field
     */
    protected VBox longTextAndField(String text, ChangeListener<String> listener){
        Label label=new Label(text);
        TextArea field=new TextArea();
        field.setWrapText(true);
        field.setPrefRowCount(4);

        return styleTextAndField(field, label, text, listener, false);
    }


    /** Creates a long text and sets the initial value of the field
     * @param text - The value used for the name of the text and the field
     * @param value - The initial value of the field
     * @param listener - The listener to be added to the field
     * @return - a VBox containing the created text and field
     */
    protected VBox setLongTextAndField(String text, String value, ChangeListener<String> listener){
        Label label=new Label(text);
        TextArea field=new TextArea(value);
        field.setWrapText(true);
        field.setPrefRowCount(4);

        return styleTextAndField(field, label, text, listener, true);
    }


    /** Styles the text and the field
     * @param field - the field to be styled
     * @param label - the label for the field
     * @param text - the text of the field
     * @param listener - the listener of the field
     * @param valid - a boolean on if the field should initially be set to valid or not
     * @return - a vbox containing the styled text and field
     */
    private VBox styleTextAndField(TextInputControl field, Label label, String text, ChangeListener<String> listener, Boolean valid){
        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");

        currentFields.put(text, field);
        currentText.put(text, inputText);

        field.textProperty().addListener(listener);

        validFields.put(text, valid);

        return new VBox(label, field, inputText);
    }







    /** Creates the dropdown field and adds it to current field
     * @param text - String of text to put in dropdown
     * @param choices - List of the choices
     * @return - the dropdown field
     */
    protected VBox dropdownField(String text, List<String> choices){
        Label label=new Label(text);
        ComboBox field = makeDropdown(choices);

        currentFields.put(text, field);

        VBox inputField = new VBox(label, field);
        inputField.setPadding(new Insets(10));

        return inputField;
    }


    /** Creates a dropdown box from a list of choices
     * @param choices - list of choices
     * @return - dropdown box
     */
    public ComboBox makeDropdown(List<String> choices) {
        ComboBox choiceDropdown = new ComboBox();
        for (String choice : choices) {
            choiceDropdown.getItems().add(choice);
        }
        choiceDropdown.getSelectionModel().select(0);
        return choiceDropdown;
    }


    /** Creates the toggle button
     * @param group - group of items that get toggled
     * @param operatorName - String of name
     * @param icon - icon to add to button
     * @return - String of name
     */
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



    /**Creates the toggle button without an icon
     * @param group - group of items that get toggled
     * @param operatorName - String of name
     * @return - String of name
     */
    protected ToggleButton setToggleOption(ToggleGroup group, String operatorName) {
        String operatorNameDisplay = operatorName.toUpperCase();
        ToggleButton op = new ToggleButton(operatorNameDisplay);
        op.setToggleGroup(group);
        op.setUserData(operatorName);
        op.setContentDisplay(ContentDisplay.TOP);
        op.getStyleClass().add("card-toggle");
        return op;
    }



    /** Creates an input button
     * @param text - button text
     * @return - the created button
     */
    protected Button inputButton(String text){
        Button button = new Button(text);
        currentButtons.put(text, button);
        return button;
    }


    /**Styles bottom buttons
     * @param buttons - HBox containing buttons
     * @return - The styled buttons
     */
    protected HBox bottomButtons(HBox buttons){
        buttons.setSpacing(20.0);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setPadding(new Insets(20, 0, 0, 0));
        return buttons;
    }



    /*
     NOTIFICATIONS
     */

    /** Creates a notification scene
     * @param mainText - String of text of notification
     * @param buttonText - String of text to put in button
     * @param tick - Boolean of if a tick or not to show at top of notification
     */
    public void notificationScene(String mainText, String buttonText, boolean tick){

        HBox notification = setNotificationCard(mainText, tick);
        notification.setFillHeight(false);

        Button returnBtn = inputButton(buttonText);

        HBox btnContainer = new HBox(returnBtn);
        btnContainer.setPadding(new Insets(20, 0, 0, 0));
        btnContainer.setAlignment(Pos.BOTTOM_CENTER);

        createScene("NOTICE", notification, btnContainer);
    }


    /**Creates the notification card
     * @param msg - message to put on notification
     * @param tick - Boolean of if a tick or not to show at top of notification
     * @return - Notification card
     */
    private HBox setNotificationCard(String msg, Boolean tick) {
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
