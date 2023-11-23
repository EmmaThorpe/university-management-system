package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
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

        makeModal(passwordBtn, "CHANGE PASSWORD", resetPassUser(), true);

        Button[] mngBtns = {mngModuleBtn, mngCourseBtn, mngSignupBtn, mngAccountsBtn, mngRulesBtn, passwordBtn};
        createDashboard(mngBtns, toolbar);
    }

    /**
     * Accounts Dashboard
     **/

    public void accounts(List<HashMap<String, String>> accountList,  List<Map<String, String>> coursesList, List<Map<String, String>> moduleList)  {
        resetCurrentValues();

        ArrayList<Button> accountBtnsList = new ArrayList<>();

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

    private EventHandler<Event> pickUser(HashMap<String, String> user, VBox rightPanel, VBox accDetails){
        return event -> {
            currentValues = new HashMap<>();
            currentValues.put("UserID", user.get("userID"));

            setUserDetails(user, accDetails);
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


            VBox accountActionsDisplay = makeScrollablePart(createButtonsVBox(accountBtnsList));

            rightPanel.getChildren().set(0, accDetails);
            rightPanel.getChildren().set(1, accountActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    private void setUserDetails(HashMap<String, String> user, VBox accDetails) {
        accDetails.getChildren().set(0, infoContainer(userDetailDisplay(user.get("userID"), user.get("managerID"), user.get("forename"), user.get("surname"), user.get("email"), user.get("DOB"), user.get("gender"), user.get("userType"), user.get("activated"))));
    }

    /**
     * Account dashboard - modals
     **/

    private VBox enrolCourse(List<Map<String, String>> courses) {
        List<String> courseNames = new ArrayList<>();

        for (Map<String, String> c : courses) {
            courseNames.add(c.get("Name"));
        }
        VBox setCourse = dropdownField("COURSE TO ENROL TO",
                courseNames);

        return new VBox(setCourse);
    }

    private VBox assignModule(List<Map<String, String>> modules) {
        List<String> moduleNames = new ArrayList<>();

        for (Map<String, String> m : modules) {
            moduleNames.add(m.get("Name"));
        }
        VBox setCourse = dropdownField("MODULE TO ASSIGN", moduleNames);

        return new VBox(setCourse);
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

        return new VBox(decisionInfo, setDecision);
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

    public void courses(List<Map<String, String>> courseList, List<Map<String, String>> moduleList, List<String> departmentList) {
        resetCurrentValues();

        Button add = inputButton("ADD COURSE");
        Button edit = inputButton("EDIT COURSE");
        Button assign = inputButton("ASSIGN MODULE TO COURSE");

        makeModal( add, "ADD", addCourse(departmentList),  true);
        makeModal( edit, "EDIT", new VBox(),  false);
        makeModal(assign, "ASSIGN", new VBox(), false);

        VBox courseDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, courseDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = courseButtons(courseList, add, rightActionPanel, courseDetails, departmentList,
                moduleList);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Courses");
    }

    private VBox courseButtons(List<Map<String, String>> courseList, Button addBtn, VBox rightPanel,
                               VBox courseDetails, List<String> departmentList, List<Map<String, String>> moduleList){
        VBox panel = new VBox();
        HBox tempButton;
        for (Map<String, String> course : courseList) {
            tempButton = makeCourseListButton(
                    course.get("Name"),
                    course.get("Level"),
                    course.get("Years")
            );
            tempButton.setOnMouseClicked(pickCourse(course, rightPanel, courseDetails, departmentList, moduleList));
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
        return makeListButton(name, new FontIcon(FontAwesomeSolid.SCHOOL), courseDetails);
    }

    private EventHandler<Event> pickCourse(Map<String, String> tempCourse, VBox rightPanel, VBox courseDetails,
                                    List<String> departments, List<Map<String, String>> moduleList){
        return event -> {
            courseDetails.getChildren().set(0, infoContainer(courseDetailDisplay(tempCourse)));

            currentValues.put("ID",tempCourse.get("Id"));

            ArrayList<Button> courseBtnsList = new ArrayList<>();

            courseBtnsList.add(currentButtons.get("EDIT COURSE"));
            courseBtnsList.add(currentButtons.get("ASSIGN MODULE TO COURSE"));


            VBox courseActionsDisplay = makeScrollablePart(createButtonsVBox(courseBtnsList));

            setModalContent("EDIT", editCourse(tempCourse, departments));
            setModalContent("ASSIGN", assignCourseModule(moduleList,
                    Integer.parseInt(tempCourse.get("Years"))));

            rightPanel.getChildren().set(0, courseDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }



    /**
     * Course Dashboard - modals
     **/
    private VBox addCourse(List<String> department) {
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
        VBox setDept = dropdownField("ADD DEPARTMENT", department);

        return new VBox(setCode, setName, setDesc, setLevel, setYears, setDept);
    }

    private VBox editCourse(Map<String, String> currentCourse, List<String> department) {
        VBox setCode = setTextAndField("EDIT CODE", currentCourse.get("Id"),
                lengthCheck(1, 5, "EDIT CODE", "Code", "COURSE", "EDIT"));
        VBox setName = setTextAndField("EDIT NAME", currentCourse.get("Name"),
                lengthCheck(1, 50,"EDIT NAME", "Name", "COURSE", "EDIT"));
        VBox setDesc = setLongTextAndField("EDIT DESCRIPTION", currentCourse.get("Description"),
                lengthCheck(1, 100, "EDIT DESCRIPTION", "Description", "COURSE", "EDIT"));
        VBox setLevel = setTextAndField("EDIT LEVEL OF STUDY", currentCourse.get("Level"),
                lengthCheck(1, 20, "EDIT LEVEL OF STUDY", "Level of study", "COURSE", "EDIT"));
        VBox setYears = setTextAndField("EDIT LENGTH OF COURSE", currentCourse.get("Years"),
                rangeCheck(1, 5, "EDIT LENGTH OF COURSE", "Length of course", "COURSE", "EDIT"));

       department.remove(currentCourse.get("Department"));
       department.add(0, currentCourse.get("Department"));
       VBox setDept = dropdownField("EDIT DEPARTMENT", department);

        return new VBox(setCode, setName, setDesc, setLevel, setYears, setDept);
    }


    private VBox assignCourseModule(List<Map<String, String>> modules, int years) {
        List<String> moduleNames = new ArrayList<>();
        List<String> semOptions = new ArrayList<>();
        List<String> yearOptions = new ArrayList<>();

        for (Map<String, String> m : modules) {
            moduleNames.add(m.get("Name"));
        }
        VBox setCourse = dropdownField("MODULE TO ASSIGN TO",
                moduleNames);

        semOptions.add("Semester 1");
        semOptions.add("Semester 2");
        semOptions.add("Semester 1 and 2");
        VBox setSem = dropdownField("SET SEMESTER",
                semOptions);

        for (int i = 1; i <= years; i++) {
            yearOptions.add(String.valueOf(i));
        }
        VBox setYear = dropdownField("SET YEAR",
                yearOptions);

        return new VBox(setCourse, setSem, setYear);
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
        makeModal(edit, "EDIT", new VBox(), false);
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

    private EventHandler<Event> pickModule(Map<String, String> tempModule, VBox rightPanel, VBox moduleDetails){
        return event -> {
            moduleDetails.getChildren().set(0, infoContainer(moduleDetailDisplay(tempModule)));

            currentValues.put("ID",tempModule.get("Id"));

            ArrayList<Button> moduleBtnsList = new ArrayList<>();

            moduleBtnsList.add(currentButtons.get("ASSIGN MODULE TO LECTURER"));
            moduleBtnsList.add(currentButtons.get("UPDATE MODULE INFORMATION"));

            setModalContent("EDIT", editModule(tempModule));

            VBox courseActionsDisplay = makeScrollablePart(createButtonsVBox(moduleBtnsList));

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

        return new VBox(setCode, setName, setDesc, setCredits);
    }

    private VBox assignModuleLecturers(List<Map<String, String>> lecturers) {
        List<String> lecturersList = new ArrayList<>();

        for (Map<String, String> lec : lecturers) {
            lecturersList.add(lec.get("Name"));
        }
        VBox setCourse = dropdownField("LECTURER TO ASSIGN TO", lecturersList);

        return new VBox(setCourse);
    }

    /**
     * Signup workflow Dashboard
     **/

    public void signups(List<HashMap<String, String>> accountList)  {
        resetCurrentValues();

        ArrayList<Button> accountBtnsList = new ArrayList<>();

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


    private EventHandler<Event> pickSignup(HashMap<String, String> user, VBox rightPanel, VBox accDetails){
        return event -> {

            currentValues.put("ID", user.get("userID"));
            setUserDetails(user, accDetails);
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

        VBox actionPanel = listOfRules(activatedBusinessRules, associatedOfRules, addRuleBtn);

        singlePanelLayout(actionPanel, "MANAGE BUSINESS RULES");
    }



    private VBox listOfRules(List<Map<String, String>> activatedBusinessRules,
                             Map<String, List<String>> associatedOfRules, Button addBtn){

        VBox panelActivated = new VBox();

        HBox tempRule;

        for (Map<String, String> rule:activatedBusinessRules) {
            tempRule = makeRuleSection(rule.get("Type"), rule.get("Value"), associatedOfRules.get(rule.get("Id")));
            panelActivated.getChildren().add(tempRule);
        }

        panelActivated.setSpacing(20.0);
        panelActivated.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane rules = new ScrollPane(panelActivated);
        return makeScrollablePanelWithAction(rules, addBtn);
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

        VBox ruleDetails = new VBox(typeDisplay, associatedDisplay, activatedDisplay);

        ruleDetails.setSpacing(3.0);

        HBox listButton = new HBox(ruleDetails);
        listButton.setAlignment(Pos.CENTER);
        listButton.setSpacing(20.0);
        listButton.setPadding(new Insets(15));
        listButton.getStyleClass().add("list-button");

        return listButton;
    }

    public void addBusinessRule(Map<String, Map<String, Boolean>> courseList, Map<String, Boolean> moduleList){
        resetCurrentValues();

        Button setCourseBtn = inputButton("SET COURSE RULE");
        Button setModuleBtn = inputButton("SET MODULE RULE");

        setCourseBtn.setDisable(true);
        setCourseBtn.setDisable(true);

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
        ruleSelector.setSpacing(20.0);
        ruleSelector.setAlignment(Pos.BASELINE_CENTER);

        HBox.setHgrow(rules1, Priority.ALWAYS);
        HBox.setHgrow(rules2, Priority.ALWAYS);

        List<String> types = new ArrayList<>();
        types.add("Max Number Of Resits");
        types.add("Number of Compensated Classes");
        VBox setRules = dropdownField("RULE TYPE", types);

        VBox courseValue = textAndField("COURSE VALUE", (obs, oldVal, newVal)-> checkRulesValidity("COURSE"));
        VBox moduleValue = textAndField("MODULE VALUE", (obs, oldVal, newVal)-> checkRulesValidity("MODULE"));

        VBox courseDropdown = new VBox(dropdownCreator(true, "COURSE 1", courseList, null));
        VBox moduleDropdown = new VBox(dropdownCreator(false, "MODULE 1", null, moduleList));
        currentValues.put("AMOUNT OF COURSE", "1");
        currentValues.put("AMOUNT OF MODULE", "1");


        Button removeCourse = inputButton("REMOVE COURSE");
        removeCourse.getStyleClass().add("panel-button-1");
        removeCourse.setOnAction(event -> removeDropdown(courseDropdown, "COURSE"));

        Button addCourse = inputButton("ADD COURSE");
        addCourse.getStyleClass().add("panel-button-2");
        addCourse.setOnAction(event -> addDropdown(courseDropdown, courseList, moduleList, "COURSE"));

        removeCourse.setDisable(true);

        HBox courseButtons = new HBox(addCourse, removeCourse);
        courseButtons.setSpacing(10.0);
        courseButtons.setPadding(new Insets(5));
        VBox coursePanel = new VBox(courseDropdown, courseButtons);

        Button removeModule = inputButton("REMOVE MODULE");
        removeModule.getStyleClass().add("panel-button-1");
        removeModule.setOnAction(event -> removeDropdown(moduleDropdown, "MODULE"));

        Button addModule = inputButton("ADD MODULE");
        addModule.getStyleClass().add("panel-button-2");
        addModule.setOnAction(event -> addDropdown(moduleDropdown, courseList, moduleList, "MODULE"));

        removeModule.setDisable(true);

        HBox moduleButtons = new HBox(addModule, removeModule);
        moduleButtons.setSpacing(10.0);
        moduleButtons.setPadding(new Insets(5));
        VBox modulePanel = new VBox(moduleDropdown, moduleButtons);

        VBox panelCourse = new VBox(setRules, courseValue, coursePanel, new VBox(inputText("COURSE CHECK"), course));

        VBox panelModule = new VBox(new Text("Max Number of Resits"), moduleValue, modulePanel, new VBox(inputText("MODULE CHECK"), module));

        panelCourse.setSpacing(10.0);
        panelCourse.setPadding(new Insets(10, 5, 10, 5));

        panelModule.setSpacing(10.0);
        panelModule.setPadding(new Insets(10, 5, 10, 5));

        ScrollPane rulePanel = new ScrollPane(panelCourse);

        rulesSelected.selectedToggleProperty().addListener(toggleRules(rulePanel, panelCourse, panelModule));

        return makePanel(new VBox( ruleSelector, rulePanel));
    }





    private VBox dropdownCreator(Boolean course, String name, Map<String, Map<String, Boolean>> courseList, Map<String, Boolean> moduleList){
        ArrayList<String> fields;
        if(course){
            fields = new ArrayList<>(courseList.keySet());
        }else{
            fields = new ArrayList<>(moduleList.keySet());
        }

        VBox dropdown = dropdownField(name, fields);
        Label dropdownLabel = (Label) dropdown.getChildren().get(0);
        String dropdownFieldName = dropdownLabel.getText();
        ComboBox dropdownBox = (ComboBox) dropdown.getChildren().get(1);

        if(course){
            dropdownBox.valueProperty().addListener(onDropdownChangeCourse(dropdownFieldName, courseList));

        }else{
            dropdownBox.valueProperty().addListener(onDropdownChangeModule(dropdownFieldName, moduleList));

        }
        return dropdown;
    }

    private void addDropdown(VBox panel, Map<String, Map<String, Boolean>> courseList, Map<String, Boolean> moduleList, String type){
        String newVal = String.valueOf(Integer.parseInt(currentValues.get("AMOUNT OF "+type))+1);

        if(type.equals("COURSE")){
            panel.getChildren().add(dropdownCreator(true, type+ " " +newVal, courseList, null));
        }else{
            panel.getChildren().add(dropdownCreator(false, type+ " " +newVal, null, moduleList));
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

        String value = ((TextField)currentFields.get(type+" VALUE")).getText();

        if((value.isEmpty() || !(value.matches("\\d+")) || Integer.parseInt(value) < 0 || Integer.parseInt(value) >5)){
            currentText.get(type + " CHECK").setText("ERROR: VALUE MUST BE A NUMBER BETWEEN 1 AND 5");
            currentButtons.get("SET " + type + " RULE").setDisable(true);
            return;
        }

        currentText.get(type+" CHECK").setText("");
        currentButtons.get("SET "+type+" RULE").setDisable(false);

    }

    protected ChangeListener<Toggle> toggleRules(ScrollPane ruleContent, VBox courseContent, VBox moduleContent){
        return (observableValue, previousToggle, newToggle) -> {
            if (newToggle == null) {
                previousToggle.setSelected(true);
            } else if(previousToggle != null) {
                if (newToggle.getUserData() == "Course Rules"){
                    ruleContent.setContent(courseContent);
                } else {
                    ruleContent.setContent(moduleContent);
                }
            }

        };

    }

    protected ChangeListener<String> onDropdownChangeCourse(String fieldChanged, Map<String, Map <String,Boolean>> courseRules){
        return (observableValue, previousSelect, newSelect) -> {
            ComboBox dropdown = (ComboBox) currentFields.get(fieldChanged);
            dropdown.setValue(newSelect);
            currentFields.replace(fieldChanged, dropdown);

            String ruleType = ((ComboBox) currentFields.get("RULE TYPE")).getValue().toString();

            if(courseRules.get(newSelect).get(ruleType)){
                makeNotificationModal(null, "BE CAREFUL, THIS COURSE ALREADY HAS A RULE OF THE SAME TYPE.  \n " +
                        "REPLACING IT WILL DELETE THE ALREADY MADE RULE", false);
            }

            checkRulesValidity("COURSE");
        };
    }


    protected ChangeListener<String> onDropdownChangeModule(String fieldChanged, Map <String,Boolean> moduleRules){
        return (observableValue, previousSelect, newSelect) -> {
            ComboBox dropdown = (ComboBox) currentFields.get(fieldChanged);
            dropdown.setValue(newSelect);
            currentFields.replace(fieldChanged, dropdown);

            if(moduleRules.get(newSelect)){
                makeNotificationModal(null, "BE CAREFUL, THIS MODULE ALREADY HAS A RULE OF THE SAME TYPE.  \n " +
                        "REPLACING IT WILL DELETE THE ALREADY MADE RULE", false);
            }

            checkRulesValidity("COURSE");
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


}
