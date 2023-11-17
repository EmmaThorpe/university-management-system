package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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
                currentText.get(field).setText(fieldName + " must be between " + String.valueOf(minLength)
                        + " and " + String.valueOf(maxLength) + " characters long");
                switch (model) {
                    case "COURSE":
                        checkValidCourseFields(field, false, manipulation);
                        break;
                    case "MODULE":
                        checkValidModuleFields(field, false, manipulation);
                        break;
                }
                }
            else {
                currentText.get(field).setText("");
                switch (model) {
                    case "COURSE":
                        checkValidCourseFields(field, true, manipulation);
                        break;
                    case "MODULE":
                        checkValidModuleFields(field, true, manipulation);
                        break;
                }
            }
        };
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
                currentText.get(field).setText(fieldName + " must be between " + String.valueOf(minValue)
                        + " and " + String.valueOf(maxValue));
                switch (model) {
                    case "COURSE":
                        checkValidCourseFields(field, false, manipulation);
                        break;
                    case "MODULE":
                        checkValidModuleFields(field, false, manipulation);
                        break;
                }
            }
            else {
                currentText.get(field).setText("");
                switch (model) {
                    case "COURSE":
                        checkValidCourseFields(field, true, manipulation);
                        break;
                    case "MODULE":
                        checkValidModuleFields(field, true, manipulation);
                        break;
                }
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
                currentText.get(field).setText(fieldName + " must be between " + String.valueOf(minValue)
                        + " and " + String.valueOf(maxValue));
                switch (model) {
                    case "MARK":
                        checkValidMarkFields(field, false, markType);
                }
            }
            else {
                currentText.get(field).setText("");
                switch (model) {
                    case "MARK":
                        checkValidMarkFields(field, true, markType);
                }
            }
        };
    }

    /** Checks that a field in a form has been filled out
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param manipulation - the way the form data is being applied to with the model (eg: EDIT, ADD)
     */
    protected ChangeListener<String> presenceCheck(String field,
                                                String fieldName, String model,
                                                String manipulation){
        return (obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                currentText.get(field).setText(fieldName + " is required");
                switch (model) {
                    case "COURSE":
                        checkValidCourseFields(field, false, manipulation);
                        break;
                    case "MODULE":
                        checkValidModuleFields(field, false, manipulation);
                        break;
                }
            }
            else {
                currentText.get(field).setText("");
                switch (model) {
                    case "COURSE":
                        checkValidCourseFields(field, true, manipulation);
                        break;
                    case "MODULE":
                        checkValidModuleFields(field, true, manipulation);
                        break;
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

    protected void singlePanelLayout(VBox main, String title){

        HBox toolbar = makeToolbar(title);

        HBox actionPanel = new HBox(main);

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

        BorderPane root = new BorderPane(actionPanel);
        root.setTop(toolbar);
        BorderPane.setMargin(toolbar, new Insets(15));
        BorderPane.setMargin(actionPanel, new Insets(15));

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }

    /**
        Module Elements
     */

    protected HBox makeModuleListButton(String id, String name, String credit) {
        HBox yearsDisplay = listDetail("CREDITS" , credit);
        Text nameDisplay = new Text(name);

        VBox courseDetails = new VBox(nameDisplay, yearsDisplay);
        courseDetails.setSpacing(5.0);
        HBox listButton = makeListButton(id, new FontIcon(FontAwesomeSolid.CHALKBOARD), courseDetails);
        return listButton;
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
                presenceCheck("EDIT CODE", "Code", "MODULE", "EDIT"));
        VBox setName = setTextAndField("EDIT NAME", currentModule.get("Name"),
                presenceCheck("EDIT NAME", "Name", "MODULE", "EDIT"));
        VBox setDesc = setLongTextAndField("EDIT DESCRIPTION", currentModule.get("Description"),
                presenceCheck("EDIT DESCRIPTION", "Description", "MODULE", "EDIT"));
        VBox setCredit = setTextAndField("EDIT CREDITS", currentModule.get("Credit"),
                rangeCheck(10, 60,"EDIT CREDITS", "Credits", "MODULE", "EDIT"));

        VBox container = new VBox(setCode, setName, setDesc, setCredit);
        return container;
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

        col1.setSpacing(5);
        col2.setSpacing(5);
        return new VBox(idTitle, col1, col2);
    }

    //user elements

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

        HBox listButton = makeListButton(userID, appGraphic, userDetails);
        return listButton;
    }

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

    protected HBox makeStudentMarkListButton(String userID, String fname, String lname, String labMark,
                                      String examMark) {
        Text nameDisplay = new Text(fname + " " + lname);

        HBox examDisplay = listDetail("EXAM" , examMark);
        HBox labDisplay = listDetail("LAB" , labMark);

        HBox markDetails = new HBox(labDisplay, examDisplay);
        markDetails.setSpacing(5.0);

        VBox userDetails = new VBox(nameDisplay, markDetails);
        userDetails.setSpacing(5.0);

        HBox listButton = makeListButton(userID, new FontIcon(FontAwesomeSolid.USER), userDetails);
        return listButton;
    }
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

        HBox listButton = makeListButton(moduleID, appGraphic, studentMarkDetails);
        return listButton;
    }

    protected VBox decisionDisplay(String decision) {
        Text title = new Text("DECISION: ");
        Text decisionMade = new Text(decision);
        decisionMade.getStyleClass().add("decision-text");
        title.getStyleClass().add("decision-text");

        if (decision.equals("AWARD")) {
            decisionMade.getStyleClass().add("decision-award");
        } else if (decision.equals("WITHDRAWAL")) {
            decisionMade.getStyleClass().add("decision-withdraw");
        } else if (decision.equals("RESIT")) {
            decisionMade.getStyleClass().add("decision-resit");
        } else {
            decisionMade.getStyleClass().add("decision-na");
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

            WritableImage pdfImage = SwingFXUtils.toFXImage(image, null);

            return pdfImage;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }



    protected HBox makeWeekButton(int weekNo) {
        Text nameDisplay = new Text("Week " + weekNo);

        VBox weekDetails = new VBox(nameDisplay);
        weekDetails.setSpacing(5.0);
        HBox listButton = makeListButton(null, new FontIcon(FontAwesomeSolid.CHALKBOARD), weekDetails);
        return listButton;
    }




}
