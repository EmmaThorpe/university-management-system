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

public class MainUI {
    Stage currentStage = new Stage();
    String css = this.getClass().getResource("/css/style.css").toExternalForm();
    Scene currScene;

    protected Map<String, Node> currentFields;

    protected Map<String, Text> currentText;

    protected Map<String, Button> currentButtons;

    protected Map<String, Boolean> validFields;

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



    public ComboBox makeDropdown(List<String> choices) {
        ComboBox choiceDropdown = new ComboBox();
        for (String choice : choices) {
            choiceDropdown.getItems().add(choice);
        }
        choiceDropdown.getSelectionModel().select(0);
        return choiceDropdown;
    }



    public void createScene(String top, Pane mainContent, Pane bottom){
        VBox title = setTitle(top);

        BorderPane root = new BorderPane(mainContent);
        root.setTop(title);
        root.setBottom(bottom);

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }

    private VBox setTitle(String titleText) {
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        StackPane iconStack = makeCircleIcon(35, "login-back" ,appGraphic, "login-graphic");

        Text titleName = new Text(titleText);
        titleName.getStyleClass().add("login-title");

        VBox title = new VBox(iconStack, titleName);
        title.setAlignment(Pos.TOP_CENTER);
        return title;
    }

    public StackPane makeCircleIcon(Integer radius, String backgroundStyle, FontIcon icon, String graphicStyle){
        StackPane iconStack = new StackPane();
        Circle appGraphicBack = new Circle(radius);
        appGraphicBack.getStyleClass().add(backgroundStyle);
        icon.getStyleClass().add(graphicStyle);
        iconStack.getChildren().addAll(appGraphicBack, icon);
        return iconStack;
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


    protected VBox createButtonsVBox(ArrayList<Button> btnsList){
        Button[] btns = btnsList.toArray(new Button[0]);
        btns = stylePanelActions(btns);

        VBox btnView = new VBox(btns);

        btnView.setAlignment(Pos.CENTER);
        btnView.setSpacing(20.0);
        btnView.setPadding(new Insets(10));

        return btnView;
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


    protected HBox makeWeekButton(int weekNo) {
        Text nameDisplay = new Text("Week " + weekNo);

        VBox weekDetails = new VBox(nameDisplay);
        weekDetails.setSpacing(5.0);
        return makeListButton(null, new FontIcon(FontAwesomeSolid.CHALKBOARD), weekDetails);
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

    protected VBox textAndField(String value, ChangeListener<String> listener){
        Label label=new Label(value);
        TextField field=new TextField();
        return styleTextAndField(field, label, value, listener, false);
    }
    protected VBox setTextAndField(String text, String value, ChangeListener<String> listener){
        Label label=new Label(text);
        TextField field=new TextField(value);

        return styleTextAndField(field, label, text, listener, true);
    }

    private VBox styleTextAndField(TextInputControl field, Label label, String text, ChangeListener<String> listener, Boolean valid){
        Text inputText = new Text();
        inputText.getStyleClass().add("notice-text");

        currentFields.put(text, field);
        currentText.put(text, inputText);

        field.textProperty().addListener(listener);

        validFields.put(text, valid);

        return new VBox(label, field, inputText);
    }

    protected VBox longTextAndField(String text, ChangeListener<String> listener){
        Label label=new Label(text);
        TextArea field=new TextArea();
        field.setWrapText(true);
        field.setPrefRowCount(4);

        return styleTextAndField(field, label, text, listener, false);
    }



    protected VBox setLongTextAndField(String text, String value, ChangeListener<String> listener){
        Label label=new Label(text);
        TextArea field=new TextArea(value);
        field.setWrapText(true);
        field.setPrefRowCount(4);

        return styleTextAndField(field, label, text, listener, true);
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

    protected HBox makeUserListButton(String userID, String fname, String lname, String userType,
                                      String activated) {
        Text nameDisplay = new Text(fname + " " + lname);

        HBox activatedDisplay = new HBox();
        if (activated.equals("ACTIVATED")) {
            activatedDisplay.getChildren().add(activeDetail(activated, true));
        } else {
            activatedDisplay.getChildren().add(activeDetail(activated, false));
        }

        FontIcon appGraphic;
        if (userType.equals("STUDENT")) {
            appGraphic =  new FontIcon(FontAwesomeSolid.USER);
        } else if (userType.equals("LECTURER")) {
            appGraphic =  new FontIcon(FontAwesomeSolid.CHALKBOARD_TEACHER);
        } else {
            appGraphic = new FontIcon(FontAwesomeSolid.USER_TIE);
        }

        VBox userDetails = new VBox(nameDisplay, activatedDisplay);
        userDetails.setSpacing(5.0);

        return makeListButton(userID, appGraphic, userDetails);
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

    protected VBox makePanelButtons(ScrollPane contentPane, VBox contentVBox,  Button action){
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

    protected HBox makeStudentMarkListButton(String userID, String fname, String lname, String labMark,
                                             String examMark) {
        Text nameDisplay = new Text(fname + " " + lname);

        HBox examDisplay = listDetail("EXAM" , examMark);
        HBox labDisplay = listDetail("LAB" , labMark);

        HBox markDetails = new HBox(labDisplay, examDisplay);
        markDetails.setSpacing(5.0);

        VBox userDetails = new VBox(nameDisplay, markDetails);
        userDetails.setSpacing(5.0);

        return makeListButton(userID, new FontIcon(FontAwesomeSolid.USER), userDetails);
    }

    protected HBox listDetail(String title, String content){
        HBox titleDisplay = new HBox(new Text(title.toUpperCase()));
        titleDisplay.getStyleClass().add("list-detail");

        Text contentDisplay =new Text(content);

        HBox detail = new HBox(titleDisplay, contentDisplay);
        detail.setPadding(new Insets(10));
        detail.setSpacing(10);

        return detail;
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
