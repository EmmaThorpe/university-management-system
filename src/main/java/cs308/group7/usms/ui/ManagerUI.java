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

    /**
     * Main Dashboard
     **/

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

    /**
     * Accounts Dashboard
     **/

    public void accounts(List<User> accountList, List<Course> coursesList, List<String> moduleList) {
        resetCurrentValues();

        inputButton("ISSUE STUDENT DECISION");
        Button assign = inputButton("ASSIGN LECTURER TO MODULE");
        Button reset = inputButton("RESET PASSWORD");
        inputButton("ACTIVATE");
        inputButton("DEACTIVATE");
        Button enrol = inputButton("ENROL STUDENT INTO COURSE");

        makeModal("ASSIGN LECTURER MODULE MODAL",assign, "assign to module", assignModule(moduleList), false, false);
        makeModal("RESET PASSWORD MODAL", reset, "reset password", resetPass(), false, false);
        makeModal("ENROL STUDENT MODAL", enrol, "enrol to course", enrolCourse(coursesList), false, false);

        VBox accountDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, accountDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = userButtons(accountList, rightActionPanel, accountDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Accounts");
    }

    private VBox userButtons(List<User> accountList, VBox rightPanel, VBox details){
        VBox panel = new VBox();
        User tempUser;
        HBox tempButton;
        for (User user : accountList) {
            tempUser = user;
            tempButton = makeUserListButton(tempUser.getUserID(), tempUser.getForename(), tempUser.getSurname(),
                    tempUser.getType(), tempUser.getActivated());
            tempButton.setOnMouseClicked(pickUser(tempUser, rightPanel, details));
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

    private EventHandler pickUser(User tempUser, VBox rightPanel, VBox accDetails){
        return event -> {
            accDetails.getChildren().set(0, infoContainer(userDetailDisplay(tempUser)));

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

            VBox accountActionsDisplay = makeScrollablePart(accountBtnView);

            rightPanel.getChildren().set(0, accDetails);
            rightPanel.getChildren().set(1, accountActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    private VBox userDetailDisplay(User tempUser) {
        Text idTitle = new Text(tempUser.getUserID());
        idTitle.getStyleClass().add("info-box-title");

        VBox row1 = new VBox(
            listDetail("FULL NAME", tempUser.getForename().concat(" ".concat(tempUser.getSurname()))),
            listDetail("EMAIL", tempUser.getEmail()),
            listDetail("USER TYPE", tempUser.getType().toString())
        );

        VBox row2 = new VBox(
            listDetail("DOB", tempUser.getDOB().toString()),
            listDetail("GENDER", tempUser.getGender()),
            listDetail("ACCOUNT STATUS",
                    (tempUser.getActivated() ? "Active" : "Inactive")
            )//,
            //listDetail("MANAGED BY", tempUser.getManager().getUserID())
        );

        row1.setSpacing(5);
        row2.setSpacing(5);
        HBox rows = new HBox(row1, row2);
        return new VBox(idTitle, rows);
    }

    /**
     * Account dashboard - modals
     **/

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

    /**
     * Course Dashboard
     **/

    public void courses(List<Course> courseList, List<String> moduleList) {
        resetCurrentValues();

        Button add = inputButton("ADD COURSE");
        Button edit = inputButton("EDIT COURSE");
        Button assign = inputButton("ASSIGN MODULE TO COURSE");

        makeModal("ADD COURSE MODAL", add, "add", addCourse(), false, false);
        makeModal("EDIT COURSE MODAL", edit, "edit", new VBox(), false, false);
        makeModal("ASSIGN MODULE MODAL", assign, "assign", new VBox(), false, false);

        VBox courseDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, courseDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = courseButtons(courseList, add, rightActionPanel, courseDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Courses");
    }

    private VBox courseButtons(List<Course> courseList, Button addBtn, VBox rightPanel, VBox courseDetails){
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
            tempButton.setOnMouseClicked(pickCourse(tempCourse, rightPanel, courseDetails));
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

    private EventHandler pickCourse(Course tempCourse, VBox rightPanel, VBox courseDetails){
        return event -> {
            courseDetails.getChildren().set(0, infoContainer(courseDetailDisplay(tempCourse)));

            ArrayList<Button> courseBtnsList = new ArrayList<>();

            courseBtnsList.add(currentButtons.get("EDIT COURSE"));
            courseBtnsList.add(currentButtons.get("ASSIGN MODULE TO COURSE"));

            Button[] courseBtns = courseBtnsList.toArray(new Button[0]);
            courseBtns = stylePanelActions(courseBtns);

            VBox courseBtnView = new VBox(courseBtns);

            courseBtnView.setAlignment(Pos.CENTER);
            courseBtnView.setSpacing(20.0);
            courseBtnView.setPadding(new Insets(10));

            VBox courseActionsDisplay = makeScrollablePart(courseBtnView);

            setModalContent(currentModals.get("EDIT COURSE MODAL"), editCourse(tempCourse));

            rightPanel.getChildren().set(0, courseDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    private VBox courseDetailDisplay(Course tempCourse) {
        Text idTitle = new Text(tempCourse.getName());
        idTitle.getStyleClass().add("info-box-title");

        VBox col1 = infoDetailLong("DESCRIPTION", tempCourse.getDescription());

        Integer courseLength = tempCourse.getLength();

        HBox col2 = new HBox(
                listDetail("LEVEL", tempCourse.getLevel()),
                listDetail("COURSE LENGTH", courseLength.toString())
        );

        col1.setSpacing(5);
        col2.setSpacing(5);
        return new VBox(idTitle, col1, col2);
    }

    /**
     * Course Dashboard - modals
     **/
    private VBox addCourse() {
        VBox setName = inputField("Name", false);
        VBox setDesc = inputFieldLong("Description");
        VBox setLevel = inputField("Level of Study", false);
        VBox setYears = inputField("Length of course", false);

        VBox container = new VBox(setName, setDesc, setLevel, setYears);
        return container;
    }

    private VBox editCourse(Course currentCourse) {
        Integer curYears = currentCourse.getLength();

        VBox setDesc = inputFieldLongSetValue("Edit Description", currentCourse.getDescription());
        VBox setLevel = inputFieldSetValue("Edit Level of Study", currentCourse.getLevel());
        VBox setYear = inputFieldSetValue("Edit Length of Course", curYears.toString());

        VBox container = new VBox(setDesc, setLevel, setYear);
        return container;
    }

}
