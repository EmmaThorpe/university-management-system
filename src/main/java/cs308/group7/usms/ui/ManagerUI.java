package cs308.group7.usms.ui;

import cs308.group7.usms.model.*;
import cs308.group7.usms.model.Module;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerUI extends UserUI{

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
        Button passwordBtn = inputButton("CHANGE PASSWORD");

        Button[] mngBtns = {mngModuleBtn, mngCourseBtn, mngSignupBtn, mngAccountsBtn, mngRulesBtn, passwordBtn};
        mngBtns = stylePanelActions(mngBtns);

        VBox mainActionPanel = makePanel(new VBox(mngBtns));
        mainActionPanel.setAlignment(Pos.CENTER);

        HBox actionPanel = new HBox(mainActionPanel);
        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        makeModal(passwordBtn, "CHANGE PASSWORD", resetPassUser(), true);

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

    public void accounts(List<HashMap<String, String>> accountList,  List<Map<String, String>> coursesList, List<Map<String, String>> moduleList)  {
        resetCurrentValues();

        ArrayList<Button> accountBtnsList = new ArrayList<Button>();

        accountBtnsList.add(inputButton("ISSUE STUDENT DECISION"));
        Button assign = inputButton("ASSIGN LECTURER TO MODULE");
        Button enrol = inputButton("ENROL STUDENT INTO COURSE");
        accountBtnsList.add(enrol);
        accountBtnsList.add(assign);
        Button reset = inputButton("RESET PASSWORD");
        accountBtnsList.add(reset);
        accountBtnsList.add(inputButton("ACTIVATE"));
        accountBtnsList.add(inputButton("DEACTIVATE"));

        stylePanelActions(accountBtnsList.toArray(new Button[0]));

        makeModal(assign,"ASSIGN LECTURER MODULE MODAL",  assignModule(moduleList),  false);
        makeModal(reset,"RESET USER PASSWORD",  resetPass(true), true);
        makeModal(enrol,"ENROL STUDENT MODAL", enrolCourse(coursesList), false);

        VBox accountDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, accountDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = userButtons(accountList, rightActionPanel, accountDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Accounts");

    }

    private VBox userButtons(List<HashMap<String, String>> accountList, VBox rightPanel, VBox details){
        VBox panel = new VBox();
        HBox tempButton;
        for (HashMap<String, String> account : accountList) {
            tempButton = makeUserListButton(
                    account.get("userID"),
                    account.get("forename"),
                    account.get("surname"),
                    account.get("userType"),
                    account.get("activated")
            );
            tempButton.setOnMouseClicked(pickUser(account, rightPanel, details));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane accountListPanel = new ScrollPane(panel);
        return makeScrollablePanel(accountListPanel);
    }


    private EventHandler pickUser(HashMap<String, String> user, VBox rightPanel, VBox accDetails){
        return event -> {
            accDetails.getChildren().set(0, infoContainer(userDetailDisplay(
                    user.get("userID"),
                    user.get("managerID"),
                    user.get("forename"),
                    user.get("surname"),
                    user.get("email"),
                    user.get("DOB"),
                    user.get("gender"),
                    user.get("userType"),
                    user.get("activated")
            )));
            ArrayList<Button> accountBtnsList = new ArrayList<>();

            if (user.get("userType").equals("STUDENT")) {
                accountBtnsList.add(currentButtons.get("ISSUE STUDENT DECISION"));
                accountBtnsList.add(currentButtons.get("ENROL STUDENT INTO COURSE"));
            }
            if (user.get("userType").equals("LECTURER")) {
                accountBtnsList.add(currentButtons.get("ASSIGN LECTURER TO MODULE"));
            }

            accountBtnsList.add(currentButtons.get("RESET PASSWORD"));
            accountBtnsList.add(currentButtons.get("ACTIVATE"));
            accountBtnsList.add(currentButtons.get("DEACTIVATE"));

            if (user.get("activated").equals("ACTIVATED")) {
                currentButtons.get("ACTIVATE").setDisable(true);
                currentButtons.get("DEACTIVATE").setDisable(false);
            } else {
                currentButtons.get("ACTIVATE").setDisable(false);
                currentButtons.get("DEACTIVATE").setDisable(true);
            }

            Button[] accountBtns = accountBtnsList.toArray(new Button[0]);
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

    /**
     * Account dashboard - modals
     **/

    private VBox enrolCourse(List<Map<String, String>> courses) {
        List<String> courseNames = new ArrayList<String>();

        for (Map<String, String> c : courses) {
            courseNames.add(c.get("Name"));
        }
        VBox setCourse = dropdownField("Course to enrol to",
                courseNames);

        VBox container = new VBox(setCourse);
        return container;
    }

    private VBox assignModule(List<Map<String, String>> modules) {
        List<String> moduleNames = new ArrayList<String>();

        for (Map<String, String> m : modules) {
            moduleNames.add(m.get("Name"));
        }
        VBox setCourse = dropdownField("Module to assign to",
                moduleNames);

        VBox container = new VBox(setCourse);
        return container;
    }

    /**
     * Account dashboard - pages
     **/

    /** WIP
    public void studentDecision(Student currStudent, List<Module> moduleList, List<Mark> markList) throws SQLException {
        resetCurrentValues();
        HBox toolbar = makeToolbar("Accounts: Issue Student Decision");

        VBox panel = new VBox();

        for (Module module : moduleList) {
            List<Student> moduleSem1 = module.getStudents(true, false, currStudent.getYearOfStudy());
            List<Student> moduleSem2 =module.getStudents(false, true, currStudent.getYearOfStudy());
            if (moduleSem1.contains(currStudent) || moduleSem2.contains(currStudent)) {
                panel.getChildren().add(makeMarkList(currStudent.getMark(module.getModuleID(), latestAttempt) ,
                        module.getName()));
            }
        }

        VBox mainActionPanel = makePanel(panel);
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

    public makeMarkList (Mark mark) {
            Double examValue = mark.getExamMark();
            String examMark = examValue.toString() + "%";

            Double labValue = mark.getLabMark();
            String labMark = labValue.toString() + "%";

            Integer attemptValue = mark.getAttemptNo();
            String attemptNo = attemptValue.toString();

            HBox examDisplay = listDetail("EXAM", examMark);
            HBox labDisplay = listDetail("LAB", labMark);
            HBox attemptDisplay = listDetail("ATTEMPT NUMBER", attemptNo);

            VBox markDetails = new VBox(examDisplay, labDisplay, attemptDisplay);

            markDetails.setSpacing(5.0);
            HBox listButton = makeListButton(mark.getModuleID(), new FontIcon(FontAwesomeSolid.AWARD), markDetails);
            return listButton;
    }
     */

    /**
     * Course Dashboard
     **/

    public void courses(List<Map<String, String>> courseList, List<Map<String, String>> moduleList) {
        resetCurrentValues();

        Button add = inputButton("ADD COURSE");
        Button edit = inputButton("EDIT COURSE");
        Button assign = inputButton("ASSIGN MODULE TO COURSE");

        makeModal( add, "add", addCourse(),  false);
        makeModal( edit, "edit", new VBox(),  false);
        makeModal(assign, "assign", assignCourseModule(moduleList), false);

        VBox courseDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, courseDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = courseButtons(courseList, add, rightActionPanel, courseDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Courses");
    }

    private VBox courseButtons(List<Map<String, String>> courseList, Button addBtn, VBox rightPanel, VBox courseDetails){
        VBox panel = new VBox();
        HBox tempButton;
        for (Map<String, String> course : courseList) {
            tempButton = makeCourseListButton(
                    course.get("Name"),
                    course.get("Level"),
                    course.get("Years")
            );
            tempButton.setOnMouseClicked(pickCourse(course, rightPanel, courseDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanelWithAction(courseListPanel, addBtn);
    }

    private HBox makeCourseListButton(String name, String level, String years) {
        HBox yearsDisplay = listDetail("YEARS" , years);
        HBox levelDisplay = listDetail("LEVEL" , level);

        VBox courseDetails = new VBox(levelDisplay, yearsDisplay);
        courseDetails.setSpacing(5.0);
        HBox listButton = makeListButton(name, new FontIcon(FontAwesomeSolid.SCHOOL), courseDetails);
        return listButton;
    }

    private EventHandler pickCourse(Map<String, String> tempCourse, VBox rightPanel, VBox courseDetails){
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

            setModalContent(currentModals.get("edit"), editCourse(tempCourse));

            rightPanel.getChildren().set(0, courseDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }



    /**
     * Course Dashboard - modals
     **/
    private VBox addCourse() {
        VBox setCode = inputField("Code", false);
        VBox setName = inputField("Name", false);
        VBox setDesc = inputFieldLong("Description");
        VBox setLevel = inputField("Level of Study", false);
        VBox setYears = inputField("Length of course", false);

        VBox container = new VBox(setCode, setName, setDesc, setLevel, setYears);
        return container;
    }

    private VBox editCourse(Map<String, String> currentCourse) {
        VBox setCode = inputFieldSetValue("Code", currentCourse.get("Id"));
        VBox setName = inputFieldSetValue("Edit Name", currentCourse.get("Name"));
        VBox setDesc = inputFieldLongSetValue("Edit Description", currentCourse.get("Description"));
        VBox setLevel = inputFieldSetValue("Edit Level of Study", currentCourse.get("Level"));
        VBox setYear = inputFieldSetValue("Edit Length of Course", currentCourse.get("Years"));

        VBox container = new VBox(setCode, setName, setDesc, setLevel, setYear);
        return container;
    }

    private VBox assignCourseModule(List<Map<String, String>> modules) {
        List<String> moduleNames = new ArrayList<String>();

        for (Map<String, String> m : modules) {
            moduleNames.add(m.get("Name"));
        }
        VBox setCourse = dropdownField("Module to assign to",
                moduleNames);

        VBox container = new VBox(setCourse);
        return container;
    }

    /**
     * Modules Dashboard
     **/

    public void modules(List<Map<String, String>> moduleList, List<Map<String, String>> lecturerList) {
        resetCurrentValues();

        Button add = inputButton("ADD MODULE");
        Button assign = inputButton("ASSIGN MODULE TO LECTURER");
        Button edit = inputButton("UPDATE MODULE INFORMATION");

        makeModal(add, "add", addModule(), false);
        makeModal( edit, "edit", new VBox(), false);
        makeModal( assign, "assign", assignModuleLecturers(lecturerList), false);

        VBox moduleDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, moduleDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = moduleButtons(moduleList, add, rightActionPanel, moduleDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Modules");
    }

    private VBox moduleButtons(List<Map<String, String>> moduleList, Button addBtn, VBox rightPanel, VBox moduleDetails){
        VBox panel = new VBox();
        HBox tempButton;
        for (Map<String, String> module : moduleList) {
            tempButton = makeModuleListButton(
                    module.get("Id"),
                    module.get("Name"),
                    module.get("Credit")
            );
            tempButton.setOnMouseClicked(pickModule(module, rightPanel, moduleDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanelWithAction(courseListPanel, addBtn);
    }

    private EventHandler pickModule(Map<String, String> tempModule, VBox rightPanel, VBox moduleDetails){
        return event -> {
            moduleDetails.getChildren().set(0, infoContainer(moduleDetailDisplay(tempModule)));

            ArrayList<Button> moduleBtnsList = new ArrayList<>();

            moduleBtnsList.add(currentButtons.get("ASSIGN MODULE TO LECTURER"));
            moduleBtnsList.add(currentButtons.get("UPDATE MODULE INFORMATION"));

            Button[] moduleBtns = moduleBtnsList.toArray(new Button[0]);
            moduleBtns = stylePanelActions(moduleBtns);

            VBox moduleBtnView = new VBox(moduleBtns);

            moduleBtnView.setAlignment(Pos.CENTER);
            moduleBtnView.setSpacing(20.0);
            moduleBtnView.setPadding(new Insets(10));

            setModalContent(currentModals.get("edit"), editModule(tempModule));

            VBox courseActionsDisplay = makeScrollablePart(moduleBtnView);

            rightPanel.getChildren().set(0, moduleDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    /**
     * Module Dashboard - modals
     **/
    private VBox addModule() {
        VBox setCode = inputField("Code", false);
        VBox setName = inputField("Name", false);
        VBox setDesc = inputFieldLong("Description");
        VBox setCredits = inputField("Credits", false);

        VBox container = new VBox(setCode, setName, setDesc, setCredits);
        return container;
    }

    private VBox assignModuleLecturers(List<Map<String, String>> lecturers) {
        List<String> lecturersList = new ArrayList<String>();

        for (Map<String, String> lec : lecturers) {
            lecturersList.add(lec.get("Name"));
        }
        VBox setCourse = dropdownField("Lecturer to assign to",
                lecturersList);

        VBox container = new VBox(setCourse);
        return container;
    }


}
