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
        VBox setCode = inputFieldSetValue("EDIT CODE", currentModule.get("Id"));
        VBox setName = inputFieldSetValue("EDIT NAME", currentModule.get("Name"));
        VBox setDesc = inputFieldLongSetValue("EDIT DESCRIPTION", currentModule.get("Description"));
        VBox setCredit = inputFieldSetValue("EDIT CREDITS", currentModule.get("Credit"));

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
        Text pageNo = inputText("PAGE NO");

        currentText.get("PAGE NO").setText("1");
        pdfImg = new ImageView(pdfToImg(pdf, 0));

        Button backBtn = inputButton("<-");
        Button forwardBtn = inputButton("->");

        backBtn.setOnAction(event -> backPage(pdf));
        forwardBtn.setOnAction(event -> forwardPage(pdf, amount));

        BorderPane root = new BorderPane();
        HBox toolbar = makeToolbar(type);

        root.setTop(toolbar);
        root.setLeft(backBtn);
        root.setRight(forwardBtn);
        root.setBottom(pageNo);

        root.setCenter(new ScrollPane(pdfImg));
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
