package cs308.group7.usms.ui;

import cs308.group7.usms.model.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class ManagerUI extends MainUI{

    String selectedVal;

    public String getSelectedVal(){
        return selectedVal;
    }

    public void dashboard() {
        resetCurrentValues();
        HBox toolbar = makeToolbar("Manager");

        Button mngModuleBtn = inputButton("MANAGE MODULES");
        Button mngCourseBtn = inputButton("MANAGE COURSES");
        Button mngSignupBtn = inputButton("MANAGE SIGN-UP WORKFLOW");
        Button mngAccountsBtn = inputButton("MANAGE ACCOUNTS");
        Button mngRulesBtn = inputButton("MANAGE BUSINESS RULES");

        Button[] mngBtns = {mngModuleBtn, mngCourseBtn, mngSignupBtn, mngAccountsBtn, mngRulesBtn};
        mngBtns = stylePanelActions(mngBtns);

        VBox mainActionPanel = makePanel(new VBox(mngBtns));
        mainActionPanel.setAlignment(Pos.CENTER);

        HBox actionPanel = new HBox(mainActionPanel);
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

    public void accounts(List<User> accountList, List<Course> coursesList, List<String> moduleList) {
        resetCurrentValues();

        inputButton("ISSUE STUDENT DECISION");
        Button assign = inputButton("ASSIGN LECTURER TO MODULE");
        Button reset = inputButton("RESET PASSWORD");
        inputButton("ACTIVATE");
        inputButton("DEACTIVATE");
        Button enrol = inputButton("ENROL STUDENT INTO COURSE");

        makeModal(assign, "assign to module", assignModule(moduleList), false, false);
        makeModal(reset, "reset password", resetPass(), false, false);
        makeModal(enrol, "enrol to course", enrolCourse(coursesList), false, false);

        Text accountText = new Text();

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, accountText);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = userButtons(accountList, rightActionPanel, accountText);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Accounts");
    }

    private VBox userButtons(List<User> accountList, VBox rightPanel, Text accText){
        VBox panel = new VBox();
        User tempUser;
        HBox tempButton;
        for (User user : accountList) {
            tempUser = user;
            tempButton = makeUserListButton(tempUser.getUserID(), tempUser.getForename(), tempUser.getSurname(),
                    tempUser.getType(), tempUser.getActivated());
            tempButton.setOnMouseClicked(pickUser(tempUser, rightPanel, accText));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane accountListPanel = new ScrollPane(panel);
        return makeScrollablePanel(accountListPanel);
    }

    private HBox makeUserListButton(String userID, String fname, String lname, User.UserType userType,
                                  boolean activated) {
            Text nameDisplay = new Text(fname.concat(" ".concat(lname)));

            HBox activatedDisplay = activeDetail(
                    (activated ? "ACTIVATED" : "DEACTIVATED"),
                    activated
            );

            FontIcon appGraphic;
            if (userType.equals(User.UserType.STUDENT)) {
                appGraphic =  new FontIcon(FontAwesomeSolid.USER);
            } else if (userType.equals(User.UserType.LECTURER)) {
                appGraphic =  new FontIcon(FontAwesomeSolid.CHALKBOARD_TEACHER);
            } else {
                appGraphic = new FontIcon(FontAwesomeSolid.USER_TIE);
            }

        VBox userDetails = new VBox(nameDisplay, activatedDisplay);
            userDetails.setSpacing(5.0);

            HBox listButton = makeListButton(userID, appGraphic, userDetails);
            return listButton;
    }

    private EventHandler pickUser(User tempUser, VBox rightPanel, Text accText){
        return event -> {
            accText.setText(tempUser.getUserID());

            ArrayList<Button> accountBtnsList = new ArrayList<>();

            if (tempUser.getType().equals(User.UserType.STUDENT)) {
                accountBtnsList.add(currentButtons.get("ISSUE STUDENT DECISION"));
                accountBtnsList.add(currentButtons.get("ENROL STUDENT INTO COURSE"));
            }
            if (tempUser.getType().equals(User.UserType.LECTURER)) {
                accountBtnsList.add(currentButtons.get("ASSIGN LECTURER TO MODULE"));
            }

            accountBtnsList.add(currentButtons.get("RESET PASSWORD"));
            accountBtnsList.add(currentButtons.get("ACTIVATE"));
            accountBtnsList.add(currentButtons.get("DEACTIVATE"));

            Button[] accountBtns = accountBtnsList.toArray(new Button[0]);
            accountBtns = stylePanelActions(accountBtns);

            if (tempUser.getActivated()) {
                currentButtons.get("ACTIVATE").setDisable(true);
                currentButtons.get("DEACTIVATE").setDisable(false);
            } else {
                currentButtons.get("ACTIVATE").setDisable(false);
                currentButtons.get("DEACTIVATE").setDisable(true);
            }

            VBox accountBtnView = new VBox(accountBtns);

            accountBtnView.setAlignment(Pos.CENTER);
            accountBtnView.setSpacing(20.0);
            accountBtnView.setPadding(new Insets(10));

            rightPanel.getChildren().set(0, accText);
            rightPanel.getChildren().set(1, accountBtnView);
            rightPanel.setVisible(true);
        };
    }

    public void courses(List<Course> courseList, List<String> moduleList) {
        resetCurrentValues();

        Button add = inputButton("ADD COURSE");
        Button edit = inputButton("EDIT COURSE");
        Button assign = inputButton("ASSIGN MODULE TO COURSE");

        makeModal(add, "add", new VBox(), false, false);
        makeModal(edit, "edit", new VBox(), false, false);
        makeModal(assign, "assign", new VBox(), false, false);


        Text courseText = new Text();

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, courseText);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = courseButtons(courseList, add, rightActionPanel, courseText);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Courses");
    }

    private VBox courseButtons(List<Course> courseList, Button addBtn, VBox rightPanel, Text accText){
        VBox panel = new VBox();
        Course tempCourse;
        HBox tempButton;
        for (Course course : courseList) {
            tempCourse = course;
            tempButton = makeCourseListButton(
                    tempCourse.getName(),
                    tempCourse.getLevel(),
                    tempCourse.getLength()
            );
            tempButton.setOnMouseClicked(pickCourse(tempCourse, rightPanel, accText));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanelWithAction(courseListPanel, addBtn);
    }

    private HBox makeCourseListButton(String name, String level, Integer years) {
        HBox yearsDisplay = listDetail("YEARS" , years.toString());
        HBox levelDisplay = listDetail("LEVEL" , level);

        VBox courseDetails = new VBox(levelDisplay, yearsDisplay);
        courseDetails.setSpacing(5.0);
        HBox listButton = makeListButton(name, new FontIcon(FontAwesomeSolid.SCHOOL), courseDetails);
        return listButton;
    }

    private EventHandler pickCourse(Course tempCourse, VBox rightPanel, Text accText){
        return event -> {

            ArrayList<Button> accountBtnsList = new ArrayList<>();

            accountBtnsList.add(currentButtons.get("EDIT COURSE"));
            accountBtnsList.add(currentButtons.get("ASSIGN MODULE TO COURSE"));

            Button[] accountBtns = accountBtnsList.toArray(new Button[0]);
            accountBtns = stylePanelActions(accountBtns);

            VBox accountBtnView = new VBox(accountBtns);

            accountBtnView.setAlignment(Pos.CENTER);
            accountBtnView.setSpacing(20.0);
            accountBtnView.setPadding(new Insets(10));

            rightPanel.getChildren().set(0, accText);
            rightPanel.getChildren().set(1, accountBtnView);
            rightPanel.setVisible(true);
        };
    }

    private VBox resetPass() {
        VBox setPass = inputField("New password", false);
        VBox confirmPass = inputField("Confirm new password", false);

        VBox container = new VBox(setPass, confirmPass);
        return container;
    }

    private VBox enrolCourse(List<Course> courses) {
        List<String> courseNames = new ArrayList<String>();

        for (Course c : courses) {
            courseNames.add(c.getName());
        }
        VBox setCourse = dropdownField("Course to enrol to",
                courseNames);

        VBox container = new VBox(setCourse);
        return container;
    }

    private VBox assignModule(List<String> modules) {
        VBox setModule = dropdownField("Module to assign to",
                modules);
        VBox container = new VBox(setModule);
        return container;
    }

    private VBox editCourse(Course currentCourse) {
        Integer curYears = currentCourse.getLength();

        VBox setDesc = inputFieldSetValue("Edit Description", currentCourse.getDescription());
        VBox setLevel = inputFieldSetValue("Edit Level", currentCourse.getLevel());
        VBox setYear = inputFieldSetValue("Edit Length", curYears.toString());

        VBox container = new VBox(setDesc, setLevel, setYear);
        return container;
    }

}
