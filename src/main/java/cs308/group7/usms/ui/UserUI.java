package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserUI extends MainUI{

    private ImageView pdfImg;

    /* FORM */

    protected VBox resetPass(Boolean manager) {
        validFields = new HashMap<>();
        VBox setPass = textAndField("NEW PASSWORD", passwordCheck(manager));
        VBox confirmPass = textAndField("CONFIRM NEW PASSWORD", confirmPasswordCheck(manager));

        return new VBox(setPass, confirmPass);
    }

    public VBox resetPassUser() {
        VBox oldPass = inputField("OLD PASSWORD", true);
        return new VBox(oldPass, resetPass(false));
    }

    protected ChangeListener<String> passwordCheck(Boolean manager){
        return (obs, oldText, newText) -> {
            if (newText.length() <8 || newText.length()>20) {
                currentText.get("NEW PASSWORD").setText("Passwords must be between 8 and 20 characters long");
            } else if (!validPassword(newText, currentText.get("NEW PASSWORD"))) {
                checkValidPasswordFields("NEW PASSWORD", false, manager);
            }else{
                currentText.get("NEW PASSWORD").setText("");
                checkValidPasswordFields("NEW PASSWORD", true, manager);
            }
        };

    }

    protected ChangeListener<String> confirmPasswordCheck(Boolean manager){
        return (obs, oldText, newText) -> {
            TextField newPassField = (TextField) currentFields.get("NEW PASSWORD");
            if (!newText.equals(newPassField.getText())) {
                currentText.get("CONFIRM NEW PASSWORD").setText("Passwords must match");
                checkValidPasswordFields("CONFIRM NEW PASSWORD", false, manager);
            } else {
                currentText.get("CONFIRM NEW PASSWORD").setText("");
                checkValidPasswordFields("CONFIRM NEW PASSWORD", true, manager);
            }
        };

    }

    /** Checks the length of a field in a form
     * @param minLength - the minimum allowed amount of characters
     * @param maxLength - the maximum allowed amount of characters
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param manipulation - the way the form data is being applied to with the model (eg: EDIT, ADD)
     */
    protected ChangeListener<String> lengthCheck(int minLength, int maxLength, String field,
                                                 String fieldName, String model,
                                                 String manipulation){
        return (obs, oldText, newText) -> {
            if (newText.isEmpty() || newText.length() < minLength || newText.length() >maxLength) {
                currentText.get(field).setText(fieldName + " must be between " + minLength
                        + " and " + maxLength + " characters long");
                checkFields(model, field, false, manipulation);
                }
            else {
                currentText.get(field).setText("");
                checkFields(model, field, true, manipulation);
            }
        };
    }

    private void checkFields(String model, String field, Boolean value, String manipulation){
        switch (model) {
            case "COURSE":
                checkValidCourseFields(field, value, manipulation);
                break;
            case "MODULE":
                checkValidModuleFields(field, value, manipulation);
                break;
        }
    }

    /** Checks that a field in a form is within a number range
     * @param minValue - the minimum number value for the field
     * @param maxValue - the maximum number value for the field
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param manipulation - the way the form data is being applied to with the model (eg: EDIT, ADD)
     */
    protected ChangeListener<String> rangeCheck(int minValue, int maxValue, String field,
                                                 String fieldName, String model,
                                                 String manipulation){
        return (obs, oldText, newText) -> {
            if (newText.isEmpty()
                    || !(newText.matches("\\d+"))
                    || Integer.parseInt(newText) < minValue
                    || Integer.parseInt(newText) >maxValue) {
                currentText.get(field).setText(fieldName + " must be between " + minValue
                        + " and " + maxValue);
                checkFields(model, field, false, manipulation);
            }
            else {
                currentText.get(field).setText("");
                checkFields(model, field, true, manipulation);
            }
        };
    }

    /** Checks that a field in a form is within the mark format
     * @param minValue - the minimum mark value for that field
     * @param maxValue - the maximum mark value for that field
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param markType - the type of mark being set (eg: "EXAM", "LAB")
     */
    protected ChangeListener<String> markCheck(double minValue, double maxValue, String field,
                                                String fieldName, String model,
                                                String markType){
        return (obs, oldText, newText) -> {
            if (newText.isEmpty()
                    || !(newText.matches("\\d+"))
                    || Double.parseDouble(newText) < minValue
                    || Double.parseDouble(newText) >maxValue) {
                currentText.get(field).setText(fieldName + " must be between " + minValue
                        + " and " + maxValue);
                if (model.equals("MARK")) {
                    checkValidMarkFields(field, false, markType);
                }
            }
            else {
                currentText.get(field).setText("");
                if (model.equals("MARK")) {
                    checkValidMarkFields(field, true, markType);
                }
            }
        };
    }

    private void checkValidPasswordFields(String type, Boolean value, Boolean manager){
        boolean disabled;
        validFields.put(type, value);
        disabled = !validFields.get("NEW PASSWORD") || !validFields.get("CONFIRM NEW PASSWORD");

        if(manager){
            currentButtons.get("RESET USER PASSWORD").setDisable(disabled);
        }else{
            currentButtons.get("CHANGE PASSWORD").setDisable(disabled);
        }

    }

    private void checkValidModuleFields(String type, Boolean value, String manipulation){
        boolean disabled;
        validFields.put(type, value);
        disabled = !validFields.get(manipulation + " CODE")
                || !validFields.get(manipulation + " NAME")
                || !validFields.get(manipulation + " DESCRIPTION")
                || !validFields.get(manipulation + " CREDITS")
        ;
        currentButtons.get(manipulation).setDisable(disabled);
    }
    private void checkValidCourseFields(String type, Boolean value, String manipulation){
        boolean disabled;
       validFields.put(type, value);
        disabled = !validFields.get(manipulation + " CODE")
                || !validFields.get(manipulation + " NAME")
                || !validFields.get(manipulation + " DESCRIPTION")
                || !validFields.get(manipulation + " LEVEL OF STUDY")
                || !validFields.get(manipulation + " LENGTH OF COURSE")
        ;
        currentButtons.get(manipulation).setDisable(disabled);
    }

    private void checkValidMarkFields(String type, Boolean value, String markType){
        boolean disabled;
        validFields.put(type, value);
        disabled = !validFields.get( markType + " MARK");
        currentButtons.get("ASSIGN " + markType +  " MARK").setDisable(disabled);
    }

    /* LAYOUT */

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

    /* Panel layouts */
    private void panelLayout(Pane actionPanel, HBox toolbar){

        BorderPane root = new BorderPane(actionPanel);
        root.setTop(toolbar);
        BorderPane.setMargin(toolbar, new Insets(15));
        BorderPane.setMargin(actionPanel, new Insets(15));

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }


    protected void singlePanelLayout(VBox main, String title){
        HBox toolbar = makeToolbar(title);

        HBox actionPanel = new HBox(main);

        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        panelLayout(actionPanel, toolbar);

    }

    protected void twoPanelLayout(VBox left, VBox right, String title){

        HBox toolbar = makeToolbar(title);

        HBox actionPanel = new HBox(left, right);

        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        panelLayout(actionPanel, toolbar);
    }

    protected void threePanelLayout(VBox left, VBox right, VBox top, String title){

        HBox toolbar = makeToolbar(title);

        HBox topActionPanel = new HBox(top);
        HBox bottomActionPanel = new HBox(left, right);

        topActionPanel.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(topActionPanel, Priority.ALWAYS);

        bottomActionPanel.setAlignment(Pos.CENTER);
        bottomActionPanel.setSpacing(20.0);
        HBox.setHgrow(bottomActionPanel, Priority.ALWAYS);

        VBox actionPanel = new VBox(topActionPanel, bottomActionPanel);

        actionPanel.setSpacing(10);

        panelLayout(actionPanel, toolbar);
    }

    /**
        Module Elements
     */

    protected HBox makeModuleListButton(String id, String name, String credit) {
        HBox yearsDisplay = listDetail("CREDITS" , credit);
        Text nameDisplay = new Text(name);

        VBox courseDetails = new VBox(nameDisplay, yearsDisplay);
        courseDetails.setSpacing(5.0);
        return makeListButton(id, new FontIcon(FontAwesomeSolid.CHALKBOARD), courseDetails);
    }

    protected VBox moduleDetailDisplay(Map<String, String> tempModule) {
        Text idTitle = new Text(tempModule.get("Name"));
        idTitle.getStyleClass().add("info-box-title");

        String creditsValue = tempModule.get("Credit");
        HBox col1 = new HBox (
                listDetail("NAME", tempModule.get("Name")),
                listDetail("CREDITS", creditsValue)
        );
        VBox col2 = infoDetailLong("DESCRIPTION", tempModule.get("Description"));

        VBox col3 = infoDetailLong("LECTURER(S)", tempModule.get("Lecturers"));

        col1.setSpacing(5);
        col2.setSpacing(5);
        return new VBox(idTitle, col1, col2, col3);
    }

    //used by both managers and lecturers
    protected VBox editModule(Map<String, String> currentModule) {
        VBox setCode = setTextAndField("EDIT CODE", currentModule.get("Id"),
                lengthCheck(1,5,"EDIT CODE", "Code", "MODULE", "EDIT"));
        VBox setName = setTextAndField("EDIT NAME", currentModule.get("Name"),
                lengthCheck(1,50,"EDIT NAME", "Name", "MODULE", "EDIT"));
        VBox setDesc = setLongTextAndField("EDIT DESCRIPTION", currentModule.get("Description"),
                lengthCheck(1,100,"EDIT DESCRIPTION", "Description", "MODULE", "EDIT"));
        VBox setCredit = setTextAndField("EDIT CREDITS", currentModule.get("Credit"),
                rangeCheck(10, 60,"EDIT CREDITS", "Credits", "MODULE", "EDIT"));

        return new VBox(setCode, setName, setDesc, setCredit);
    }


    //Course Elements

    protected VBox courseDetailDisplay(Map<String, String> tempCourse) {
        Text idTitle = new Text(tempCourse.get("Name"));
        idTitle.getStyleClass().add("info-box-title");

        VBox col1 = infoDetailLong("DESCRIPTION", tempCourse.get("Description"));


        HBox col2 = new HBox(
                listDetail("LEVEL", tempCourse.get("Level")),
                listDetail("COURSE LENGTH", tempCourse.get("Years"))
        );

        HBox col3 = new HBox(listDetail("Department", tempCourse.get("Department")));

        col1.setSpacing(5);
        col2.setSpacing(5);
        return new VBox(idTitle, col1, col2, col3);
    }

    //user elements



    protected VBox userDetailDisplay(String userID, String managerID, String forename, String surname, String email,
                             String dob, String gender, String userType, String activated) {
        Text idTitle = new Text(userID);
        idTitle.getStyleClass().add("info-box-title");

        VBox row1 = new VBox(
                listDetail("FULL NAME", (forename + " " + surname)),
                listDetail("EMAIL", email),
                listDetail("DOB", dob),
                listDetail("GENDER", gender)

        );

        VBox row2 = new VBox(
                listDetail("USER TYPE", userType),
                listDetail("STATUS", activated),
                listDetail("MANAGED BY", managerID)
        );

        row1.setSpacing(5);
        row2.setSpacing(5);
        HBox rows = new HBox(row1, row2);
        return new VBox(idTitle, rows);
    }


    //student mark elements


    protected VBox studentMarkDisplay(String userID, String fname, String lname, String labMark,
                                      String examMark) {
        Text idTitle = new Text(userID);
        idTitle.getStyleClass().add("info-box-title");

        VBox row = new VBox(
                listDetail("FULL NAME", (fname + " " + lname)),
                listDetail("LAB MARK", labMark),
                listDetail("EXAM MARK", examMark)
        );

        row.setSpacing(5);
        return new VBox(idTitle, row);
    }

    //mark and decision elements
    protected HBox makeMarkList(String moduleID, String lab, String exam, String attempt,
                                String grade) {
        HBox examDisplay = listDetail("EXAM" , exam);
        HBox labDisplay = listDetail("LAB" , lab);

        HBox markDetails = new HBox(labDisplay, examDisplay);
        markDetails.setSpacing(5.0);

        HBox attemptDisplay = listDetail("ATTEMPT", attempt);
        HBox gradeDisplay = new HBox();
        if (grade.equals("PASS")) {
            gradeDisplay.getChildren().add(activeDetail(grade, true));
        } else {
            gradeDisplay.getChildren().add(activeDetail(grade, false));
        }

        HBox decisionDetails = new HBox(attemptDisplay, gradeDisplay);
        decisionDetails.setSpacing(5.0);

        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.AWARD);

        VBox studentMarkDetails = new VBox(markDetails, decisionDetails);
        studentMarkDetails.setSpacing(5.0);

        return makeListButton(moduleID, appGraphic, studentMarkDetails);
    }

    protected VBox decisionDisplay(String decision) {
        Text title = new Text("DECISION: ");
        Text decisionMade = new Text(decision);
        decisionMade.getStyleClass().add("decision-text");
        title.getStyleClass().add("decision-text");

        switch (decision) {
            case "AWARD" -> decisionMade.getStyleClass().add("decision-award");
            case "WITHDRAWAL" -> decisionMade.getStyleClass().add("decision-withdraw");
            case "RESIT" -> decisionMade.getStyleClass().add("decision-resit");
            default -> decisionMade.getStyleClass().add("decision-na");
        }

        HBox decisionDisplay = new HBox(title, decisionMade);
        decisionDisplay.setSpacing(5);
        return new VBox(decisionDisplay);

    }

    //PDF viewer

    public void displayPDF(File file, String type){
        resetCurrentValues();

        Document pdf = showPage();

        int amount = pdf.getNumberOfPages()-1;

        Text pageNo = paginationText("PAGE NO");

        currentText.get("PAGE NO").setText("1");
        pdfImg = new ImageView(pdfToImg(pdf, 0));

        Text pageDisplay = new Text("PAGE");
        pageDisplay.getStyleClass().add("pagination-text");
        HBox paginationDisplay = new HBox(pageDisplay, pageNo);
        paginationDisplay.setSpacing(5);

        Button backBtn = inputButton("<-");
        backBtn.getStyleClass().add("panel-button-1");

        Button forwardBtn = inputButton("->");
        forwardBtn.getStyleClass().add("panel-button-2");

        backBtn.setOnAction(event -> backPage(pdf));
        forwardBtn.setOnAction(event -> forwardPage(pdf, amount));

        VBox pdfDisplay = new VBox(new ScrollPane(pdfImg));
        BorderPane root = new BorderPane(pdfDisplay);
        HBox toolbar = makeToolbar(type);

        root.setTop(toolbar);
        root.setBottom(paginationDisplay);

        root.setLeft(backBtn);
        root.setRight(forwardBtn);

        BorderPane.setMargin(toolbar, new Insets(15));
        BorderPane.setMargin(pdfDisplay, new Insets(15));

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }


    protected void backPage(Document pdf){
        int page = Integer.parseInt(currentText.get("PAGE NO").getText());
        if(page>1){
            pdfImg.setImage(pdfToImg(pdf, page-2));
            currentText.get("PAGE NO").setText(String.valueOf(page-1));
        }

    }

    protected void forwardPage(Document pdf, int amount){
        int page = Integer.parseInt(currentText.get("PAGE NO").getText());

        if(page <= amount){
            pdfImg.setImage(pdfToImg(pdf, page));
            currentText.get("PAGE NO").setText(String.valueOf(page+1));
        }
    }

    protected Text paginationText(String text){
        Text inputText = new Text();
        inputText.getStyleClass().add("pagination-text");
        currentText.put(text, inputText);
        return inputText;
    }







    protected Document showPage() {


        Document currentDocument = new Document();
        try {
            File file = new File("src/main/resources/CS308_Coursework.pdf");
            currentDocument.setFile(file.getAbsolutePath());
            return currentDocument;


        } catch (PDFException | PDFSecurityException | IOException ex) {
        }


        return null;
    }

    protected WritableImage pdfToImg(Document currentDocument, int page){
        float scale = 1f;

        try {
            BufferedImage image = (BufferedImage) currentDocument.getPageImage(page,
                    GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0f, scale);

            return SwingFXUtils.toFXImage(image, null);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }





    protected void createDashboard(Button[] mngBtns, HBox toolbar){
        mngBtns = stylePanelActions(mngBtns);

        VBox mainActionPanel = makePanel(new VBox(mngBtns));
        mainActionPanel.setAlignment(Pos.CENTER);

        HBox actionPanel = new HBox(mainActionPanel);
        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        panelLayout(actionPanel, toolbar);
    }




    protected Map<String, Dialog> currentModals;

    public Map<String, Dialog> getCurrentModals(){return currentModals;}

    /*
    Modal
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

    public void resetCurrentValues(){
        currentFields = new HashMap<>();
        currentText = new HashMap<>();
        currentButtons = new HashMap<>();
        currentModals = new HashMap<>();
        currentValues =  new HashMap<>();
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

    protected VBox makePanelWithAction(VBox content, Button action) {
        content.setPadding(new Insets(20));


        return makePanelButtons(null, content, action);
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

        return makePanelButtons(content, null, action);
    }



    protected VBox makeScrollablePart(VBox content) {
        ScrollPane scrollPart = new ScrollPane(content);
        scrollPart.setPadding(new Insets(20));
        scrollPart.fitToHeightProperty().set(true);
        scrollPart.fitToWidthProperty().set(true);
        return new VBox(scrollPart);
    }










    /*
     List
     */







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
