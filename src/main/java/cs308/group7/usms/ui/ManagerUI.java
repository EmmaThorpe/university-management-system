package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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

import java.util.*;

public class ManagerUI extends UserUI{


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

        makeModal(assign,"ASSIGN LECTURER",  assignModule(moduleList),  false);
        makeModal(reset,"RESET USER PASSWORD",  resetPass(true), true);
        makeModal(enrol,"ENROL STUDENT", enrolCourse(coursesList), false);

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
            currentValues = new HashMap<>();
            currentValues.put("UserID", user.get("userID"));

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
        VBox setCourse = dropdownField("COURSE TO ENROL TO",
                courseNames);

        VBox container = new VBox(setCourse);
        return container;
    }

    private VBox assignModule(List<Map<String, String>> modules) {
        List<String> moduleNames = new ArrayList<>();

        for (Map<String, String> m : modules) {
            moduleNames.add(m.get("Name"));
        }
        VBox setCourse = dropdownField("MODULE TO ASSIGN", moduleNames);

        VBox container = new VBox(setCourse);
        return container;
    }

    /**
     * Account dashboard - pages
     **/

    public void studentDecision(Map<String, String> currStudent, List<Map<String, String>> markList,
                                String decisionRec, String decisionReason) {

        Button issueDecBtn = inputButton("ISSUE DECISION");

        makeModal(issueDecBtn, "ISSUE STUDENT DECISION",
                decisionAction(currStudent, decisionRec, decisionReason),
                false);

        VBox DecisionActionPanel = makePanelWithAction(
                markListDisplay(markList),
                issueDecBtn
        );

        singlePanelLayout(DecisionActionPanel, "Accounts: Student Decision");
    }

    private VBox markListDisplay(List<Map<String, String>> markList) {
        VBox markListItems = new VBox();

        for (Map<String, String> mark : markList) {
            HBox listItem = makeMarkList(
                    mark.get("moduleID"),
                    mark.get("lab"),
                    mark.get("exam"),
                    mark.get("attempt"),
                    mark.get("grade")
            );
            markListItems.getChildren().add(listItem);
        }

        markListItems.setSpacing(20.0);
        markListItems.setPadding(new Insets(10, 2, 10, 2));

        return markListItems;
    }

    private VBox decisionAction(Map<String, String> currStudent, String decisionRec, String decisionReason) {
        List<String> awardOptions = new ArrayList<>();
        awardOptions.add("AWARD");
        awardOptions.add("RESIT");
        awardOptions.add("WITHDRAWAL");

        VBox decisionInfo = infoContainer(decisionDisplay(currStudent.get("Id"), decisionRec, decisionReason));

        VBox setDecision = dropdownField("DECISION TO ISSUE", awardOptions);

        VBox container = new VBox(decisionInfo, setDecision);
        return container;
    }

    protected VBox decisionDisplay(String studentID, String decisionRec, String decisionReason) {
        Text idTitle = new Text(studentID);
        idTitle.getStyleClass().add("info-box-title");

        VBox row = new VBox(
                listDetail("RECOMMENDED DECISION", decisionRec)
        );

        if (!(decisionReason.isEmpty())) {
            row.getChildren().add(infoDetailLong("REASON", decisionReason));
        }

        row.setSpacing(5);
        return new VBox(idTitle, row);
    }

    /**
     * Course Dashboard
     **/

    public void courses(List<Map<String, String>> courseList, List<Map<String, String>> moduleList) {
        resetCurrentValues();

        Button add = inputButton("ADD COURSE");
        Button edit = inputButton("EDIT COURSE");
        Button assign = inputButton("ASSIGN MODULE TO COURSE");

        makeModal( add, "ADD", addCourse(),  true);
        makeModal( edit, "EDIT", new VBox(),  false);
        makeModal(assign, "ASSIGN", assignCourseModule(moduleList), false);

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

            currentValues.put("ID",tempCourse.get("Id"));

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

            setModalContent(currentModals.get("EDIT"), editCourse(tempCourse));

            rightPanel.getChildren().set(0, courseDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }



    /**
     * Course Dashboard - modals
     **/
    private VBox addCourse() {
        VBox setCode = textAndField("ADD CODE",
                lengthCheck(1, 5,"ADD CODE", "Code", "COURSE", "ADD"));
        VBox setName = textAndField("ADD NAME",
                lengthCheck(1,50,"ADD NAME", "Name", "COURSE", "ADD"));
        VBox setDesc = longTextAndField("ADD DESCRIPTION",
                lengthCheck(1,100,"ADD DESCRIPTION", "Description", "COURSE", "ADD"));
        VBox setLevel = textAndField("ADD LEVEL OF STUDY",
                lengthCheck(1,20,"ADD LEVEL OF STUDY", "Level of study", "COURSE", "ADD"));
        VBox setYears = textAndField("ADD LENGTH OF COURSE",
                rangeCheck(1, 5, "ADD LENGTH OF COURSE", "Length of course", "COURSE", "ADD"));

        VBox container = new VBox(setCode, setName, setDesc, setLevel, setYears);
        return container;
    }

    private VBox editCourse(Map<String, String> currentCourse) {
        VBox setCode = setTextAndField("ADD CODE", currentCourse.get("Id"),
                lengthCheck(1, 5, "ADD CODE", "Code", "COURSE", "ADD"));
        VBox setName = setTextAndField("ADD NAME", currentCourse.get("Name"),
                lengthCheck(1, 50,"EDIT NAME", "Name", "COURSE", "EDIT"));
        VBox setDesc = setLongTextAndField("EDIT DESCRIPTION", currentCourse.get("Description"),
                lengthCheck(1, 100, "EDIT DESCRIPTION", "Description", "COURSE", "EDIT"));
        VBox setLevel = setTextAndField("EDIT LEVEL OF STUDY", currentCourse.get("Level"),
                lengthCheck(1, 20, "EDIT LEVEL OF STUDY", "Level of study", "COURSE", "EDIT"));
        VBox setYears = setTextAndField("EDIT LENGTH OF COURSE", currentCourse.get("Years"),
                rangeCheck(1, 5, "EDIT LENGTH OF COURSE", "Length of course", "COURSE", "EDIT"));

        VBox container = new VBox(setCode, setName, setDesc, setLevel, setYears);
        return container;
    }

    private VBox assignCourseModule(List<Map<String, String>> modules) {
        List<String> moduleNames = new ArrayList<String>();

        for (Map<String, String> m : modules) {
            moduleNames.add(m.get("Name"));
        }
        VBox setCourse = dropdownField("MODULE TO ASSIGN TO",
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

        makeModal(add, "ADD", addModule(), true);
        makeModal( edit, "EDIT", new VBox(), false);
        makeModal( assign, "ASSIGN", assignModuleLecturers(lecturerList), false);

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

            currentValues.put("ID",tempModule.get("Id"));

            ArrayList<Button> moduleBtnsList = new ArrayList<>();

            moduleBtnsList.add(currentButtons.get("ASSIGN MODULE TO LECTURER"));
            moduleBtnsList.add(currentButtons.get("UPDATE MODULE INFORMATION"));

            Button[] moduleBtns = moduleBtnsList.toArray(new Button[0]);
            moduleBtns = stylePanelActions(moduleBtns);

            VBox moduleBtnView = new VBox(moduleBtns);

            moduleBtnView.setAlignment(Pos.CENTER);
            moduleBtnView.setSpacing(20.0);
            moduleBtnView.setPadding(new Insets(10));

            setModalContent(currentModals.get("EDIT"), editModule(tempModule));

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
        VBox setCode = textAndField("ADD CODE",
                lengthCheck(1, 5, "ADD CODE", "Code", "MODULE", "ADD"));
        VBox setName = textAndField("ADD NAME",
                lengthCheck(1, 50, "ADD NAME", "Name", "MODULE", "ADD"));
        VBox setDesc = longTextAndField("ADD DESCRIPTION",
                lengthCheck(1, 100, "ADD DESCRIPTION", "Description", "MODULE", "ADD"));
        VBox setCredits = textAndField("ADD CREDITS",
                rangeCheck(10, 60,"ADD CREDITS", "Credits", "MODULE", "ADD"));

        VBox container = new VBox(setCode, setName, setDesc, setCredits);
        return container;
    }

    private VBox assignModuleLecturers(List<Map<String, String>> lecturers) {
        List<String> lecturersList = new ArrayList<String>();

        for (Map<String, String> lec : lecturers) {
            lecturersList.add(lec.get("Name"));
        }
        VBox setCourse = dropdownField("LECTURER TO ASSIGN TO", lecturersList);

        VBox container = new VBox(setCourse);
        return container;
    }

    /**
     * Signup workflow Dashboard
     **/

    public void signups(List<HashMap<String, String>> accountList)  {
        resetCurrentValues();

        ArrayList<Button> accountBtnsList = new ArrayList<Button>();

        accountBtnsList.add(inputButton("APPROVE SIGN UP"));

        stylePanelActions(accountBtnsList.toArray(new Button[0]));

        VBox accountDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, accountDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = signupButtons(accountList, rightActionPanel, accountDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Sign Up Workflow");

    }

    private VBox signupButtons(List<HashMap<String, String>> accountList, VBox rightPanel, VBox details){
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
            tempButton.setOnMouseClicked(pickSignup(account, rightPanel, details));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane accountListPanel = new ScrollPane(panel);
        return makeScrollablePanel(accountListPanel);
    }


    private EventHandler pickSignup(HashMap<String, String> user, VBox rightPanel, VBox accDetails){
        return event -> {

            currentValues.put("ID", user.get("userID"));
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
            accountBtnsList.add(currentButtons.get("APPROVE SIGN UP"));

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





    public void manageBusinessRules(List<Map<String, String>> activatedBusinessRules, Map<String, List<String>> associatedOfRules){
        resetCurrentValues();
        Button addRuleBtn = inputButton("ADD BUSINESS RULE");

        VBox actionPanel = makePanelWithAction(
                listOfRules(activatedBusinessRules, associatedOfRules),
                addRuleBtn
        );

        singlePanelLayout(actionPanel, "MANAGE BUSINESS RULES");
    }



    private VBox listOfRules(List<Map<String, String>> activatedBusinessRules, Map<String, List<String>> associatedOfRules){

        VBox panelActivated = new VBox();

        HBox tempRule;

        for (Map<String, String> rule:activatedBusinessRules) {
            tempRule = makeRuleSection(rule.get("Type"), rule.get("Value"), associatedOfRules.get(rule.get("Id")));
            panelActivated.getChildren().add(tempRule);
        }

        panelActivated.setSpacing(20.0);
        panelActivated.setPadding(new Insets(10, 2, 10, 2));

        return panelActivated;
    }


    private HBox makeRuleSection(String type, String value, List<String> associated){
        Text typeDisplay = new Text(type + ": " + value);

        String assoc ="Applied to: ";

        if(associated!=null){
            for(String val : associated){
                assoc = assoc +" " + val;
            }
        }else{
            assoc = assoc +" Nothing";
        }


        Text associatedDisplay = new Text(assoc);

        HBox activatedDisplay = new HBox();
        activatedDisplay.getChildren().add(activeDetail("Activated", true));

        VBox activatedDetails = new VBox(typeDisplay, associatedDisplay, activatedDisplay);

        activatedDetails.setSpacing(5.0);

        HBox listButton = new HBox(activatedDetails);
        listButton.setAlignment(Pos.CENTER);
        listButton.setSpacing(20.0);
        listButton.setPadding(new Insets(10));
        listButton.getStyleClass().add("list-button");

        HBox.setHgrow(activatedDetails, Priority.ALWAYS);

        return listButton;
    }

    public void addBusinessRule(Map<String, Map<String, Boolean>> courseList, Map<String, Boolean> moduleList){
        resetCurrentValues();

        Button setCourseBtn = inputButton("SET COURSE RULE");
        Button setModuleBtn = inputButton("SET MODULE RULE");

        VBox actionPanel = rulesForm(courseList, moduleList, setCourseBtn, setModuleBtn);
        actionPanel.setAlignment(Pos.CENTER);

        singlePanelLayout(actionPanel, "ADD BUSINESS RULES");
    }

    private VBox rulesForm(Map<String, Map<String, Boolean>> courseList, Map<String, Boolean> moduleList, Button course, Button module){

        HBox ruleSelector = new HBox();

        ToggleGroup rulesSelected = new ToggleGroup();
        ToggleButton rules1 = setToggleOption(rulesSelected, "Course Rules");
        ToggleButton rules2 = setToggleOption(rulesSelected, "Module Rules");
        rules1.setSelected(true);
        ruleSelector.getChildren().add(rules1);
        ruleSelector.getChildren().add(rules2);

        HBox.setHgrow(rules1, Priority.ALWAYS);
        HBox.setHgrow(rules2, Priority.ALWAYS);

        List types = new ArrayList<>();
        types.add("Max Number Of Resits");
        types.add("Number of Compensated Classes");
        VBox setRules = dropdownField("RULE TYPE", types);

        VBox courseValue = textAndField("COURSE VALUE", valueCheck());
        VBox moduleValue = textAndField("MODULE VALUE", valueCheck());

        VBox courseDropdown = new VBox(courseDropdown("COURSE 1", courseList));
        VBox moduleDropdown = new VBox(moduleDropdown("MODULE 1", moduleList));
        currentValues.put("AMOUNT OF COURSE", "1");
        currentValues.put("AMOUNT OF MODULE", "1");


        Button removeCourse = inputButton("REMOVE COURSE");
        removeCourse.setOnAction(event -> removeDropdown(courseDropdown, "COURSE"));


        Button addCourse = inputButton("ADD COURSE");
        addCourse.setOnAction(event -> addDropdown(courseDropdown, courseList, moduleList, "COURSE"));

        removeCourse.setDisable(true);

        HBox courseButtons = new HBox(addCourse, removeCourse);
        VBox coursePanel = new VBox(courseDropdown, courseButtons);

        Button removeModule = inputButton("REMOVE MODULE");
        removeModule.setOnAction(event -> removeDropdown(moduleDropdown, "MODULE"));

        Button addModule = inputButton("ADD MODULE");
        addModule.setOnAction(event -> addDropdown(moduleDropdown, courseList, moduleList, "MODULE"));

        removeModule.setDisable(true);

        HBox moduleButtons = new HBox(addModule, removeModule);
        VBox modulePanel = new VBox(moduleDropdown, moduleButtons);


        VBox panelCourse = new VBox(setRules, courseValue, coursePanel, new VBox(inputText("COURSE CHECK"), course));

        VBox panelModule = new VBox(setRules, moduleValue, modulePanel, new VBox(inputText("MODULE CHECK"), module));

        panelCourse.setSpacing(20.0);
        panelCourse.setPadding(new Insets(10, 2, 10, 2));

        panelModule.setSpacing(20.0);
        panelModule.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane rulePanel = new ScrollPane(panelCourse);

        rulesSelected.selectedToggleProperty().addListener(toggleRules(rulePanel, panelCourse, panelModule));

        return makePanel(new VBox( ruleSelector, rulePanel));
    }

    private VBox courseDropdown(String name, Map<String, Map<String, Boolean>> courseList){
        ArrayList<String> fields = new ArrayList<>();
        for(String course: courseList.keySet()){
            fields.add(course);
        }

        VBox dropdown = dropdownField(name, fields);
        Label dropdownLabel = (Label) dropdown.getChildren().get(0);
        String dropdownFieldName = dropdownLabel.getText();
        ComboBox dropdownBox = (ComboBox) dropdown.getChildren().get(1);
        dropdownBox.valueProperty().addListener(onDropdownChange(dropdownFieldName, "COURSE"));
        return dropdown;
    }



    private VBox moduleDropdown(String name, Map<String, Boolean> moduleList){
        ArrayList<String> fields = new ArrayList<>();
        for(String module: moduleList.keySet()){
            fields.add(module);
        }
        VBox dropdown = dropdownField(name, fields);
        Label dropdownLabel = (Label) dropdown.getChildren().get(0);
        String dropdownFieldName = dropdownLabel.getText();
        ComboBox dropdownBox = (ComboBox) dropdown.getChildren().get(1);
        dropdownBox.valueProperty().addListener(onDropdownChange(dropdownFieldName, "MODULE"));
        return dropdown;
    }

    private void addDropdown(VBox panel, Map<String, Map<String, Boolean>> courseList, Map<String, Boolean> moduleList, String type){
        String newVal = String.valueOf(Integer.parseInt(currentValues.get("AMOUNT OF "+type))+1);

        if(type.equals("COURSE")){
            panel.getChildren().add(courseDropdown(type+ " " +newVal, courseList));
        }else{
            panel.getChildren().add(moduleDropdown(type+ " " +newVal, moduleList));
        }

        currentButtons.get("REMOVE "+type).setDisable(false);

        currentValues.put("AMOUNT OF "+type, newVal);

        checkRulesValidity(type);
    }

    private void removeDropdown(VBox panel, String type){
        int val = Integer.parseInt(currentValues.get("AMOUNT OF "+type));

        panel.getChildren().remove(val-1);
        currentFields.remove(type+val);

        if(val-1 ==1){
            currentButtons.get("REMOVE "+type).setDisable(true);
        }


        currentValues.put("AMOUNT OF "+type, String.valueOf(val-1));

        checkRulesValidity(type);
    }

    private void checkRulesValidity(String type){
        int val = Integer.parseInt(currentValues.get("AMOUNT OF "+type));
        ArrayList<String> valuesChecked = new ArrayList<>();
        String currentCheck;
        for(int i=1; i<=val; i++) {
            currentCheck = ((ComboBox) currentFields.get(type + " " + i)).getValue().toString();
            if (valuesChecked.contains(currentCheck)) {
                currentText.get(type + " CHECK").setText("ERROR: CANNOT HAVE REPEATING " + type + "S");
                currentButtons.get("SET " + type + " RULE").setDisable(true);
                return;
            }
            valuesChecked.add(currentCheck);
        }

        currentText.get(type+" CHECK").setText("");
        currentButtons.get("SET COURSE RULE").setDisable(false);

    }

    protected ChangeListener<Toggle> toggleRules(ScrollPane ruleContent, VBox courseContent, VBox moduleContent){
        return (observableValue, previousToggle, newToggle) -> {
            if (newToggle == null) {
                previousToggle.setSelected(true);
            } else if (newToggle != null && previousToggle != null) {
                if (newToggle.getUserData() == "Course Rules"){
                    ruleContent.setContent(courseContent);
                } else {
                    ruleContent.setContent(moduleContent);
                }
            }

        };

    }

    protected ChangeListener<String> onDropdownChange(String fieldChanged, String type){
        return (observableValue, previousSelect, newSelect) -> {
            ComboBox dropdown = (ComboBox) currentFields.get(fieldChanged);
            dropdown.setValue(newSelect);
            currentFields.replace(fieldChanged, dropdown);
            checkRulesValidity(type);
        };
    }


    public List<String> getRulesAppliedTo(String type){
        List<String> appliedTo = new ArrayList<>();
        int val = Integer.parseInt(currentValues.get("AMOUNT OF "+type));
        for(int i=1; i<=val; i++){
            appliedTo.add(((ComboBox) currentFields.get(type +" " + val)).getValue().toString());
        }
        return appliedTo;
    }

    private ChangeListener valueCheck(){
        return new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {

            }
        };
    }
}
