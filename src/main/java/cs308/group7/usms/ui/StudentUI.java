package cs308.group7.usms.ui;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that handles the StudentUI
 */
public class StudentUI extends UIElements{

    /**
     * The dashboard students see when they log in
     */
    public void dashboard() {
        resetCurrentValues();
        HBox toolbar = makeToolbar("Student");

        Button viewDecisionBtn = inputButton("VIEW DECISION");
        Button viewCourseBtn = inputButton("VIEW COURSE");
        Button viewModuleBtn = inputButton("VIEW MODULES");
        Button passwordBtn = inputButton("CHANGE PASSWORD");

        makeModal(passwordBtn, "CHANGE PASSWORD", resetPassUser(), true);


        Button[] mngBtns = {viewDecisionBtn, viewCourseBtn, viewModuleBtn, passwordBtn};
        createDashboard(mngBtns, toolbar);
    }



    // Decision Dashboard Elements


    /** Creates the decision dashboard
     * @param moduleList - The list of modules a student is taking
     * @param markList - The list of markss a student has
     * @param decision - The decision a student has obtained
     */
    public void decision(List<Map<String, String>> moduleList, List<Map<String,String>> markList, String decision) {
        resetCurrentValues();

        if (moduleList.isEmpty() || markList.isEmpty()) {
            VBox mainPanel = makePanel(decisionDisplay(decision));
            singlePanelLayout(mainPanel, "Decision");
        } else {
            VBox topPanel = makeTopPanel(decisionDisplay(decision));

            VBox rightActionPanel = makeScrollableBottomPanel(new ScrollPane());
            rightActionPanel.setVisible(false);

            VBox leftActionPanel = decisionButtons(moduleList, markList, rightActionPanel);
            threePanelLayout(leftActionPanel, rightActionPanel, topPanel, "Decision");
        }
    }


    /** Creates the decision buttons
     * @param moduleList - The list of modules a student has taken
     * @param markList - the list of marks a student has obtained
     * @param rightPanel - the right panel in which will be filled when the button is clicked
     * @return VBox containing the decision buttons
     */
    private VBox decisionButtons(List<Map<String, String>> moduleList, List<Map<String,String>> markList,
                                 VBox rightPanel){
        VBox panel = new VBox();
        HBox tempButton;
        for (Map<String, String> module : moduleList) {
            tempButton = makeModuleListButton(
                    module.get("Id"),
                    module.get("Name"),
                    module.get("Credit")
            );
            tempButton.setOnMouseClicked(event->pickModuleMark(module.get("Id"), markList, rightPanel));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane moduleListPanel = new ScrollPane(panel);
        return makeScrollableBottomPanel(moduleListPanel);
    }


    /** Loads the appropriate panel on the right hand side based on the button clicks
     * @param id - The id of the module selected
     * @param markList - the list of marks a student has recieved
     * @param rightPanel - the panel that the content will fill
     */
    private void pickModuleMark(String id, List<Map<String, String>> markList,
                                               VBox rightPanel){

        VBox markListItems = new VBox();
        for (Map<String, String> mark : markList) {
            if (mark.get("moduleID").equals(id)) {
                HBox listItem = makeMarkList(
                        mark.get("moduleID"),
                        mark.get("lab"),
                        mark.get("exam"),
                        mark.get("attempt"),
                        mark.get("grade")
                );
                markListItems.getChildren().add(listItem);
            }
        }
        markListItems.setSpacing(20.0);
        markListItems.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane markListPanel = new ScrollPane(markListItems);
        rightPanel.getChildren().set(0, (markListPanel));
        rightPanel.setVisible(true);
    }





    //Modules Elements


    /**Creates the module dashboard
     * @param moduleList - The list of modules a student is in
     */
    public void modules(List<Map<String, String>> moduleList) {
        resetCurrentValues();

        inputButton("VIEW MATERIALS");

        if (moduleList.isEmpty()) {
            VBox mainPanel = makePanel(emptyModelContent("modules"));
            singlePanelLayout(mainPanel, "Modules");
        } else {

            VBox moduleDetails = new VBox(new VBox());

            VBox rightActionPanel = makePanel(new VBox());
            rightActionPanel.getChildren().add(0, moduleDetails);
            rightActionPanel.setVisible(false);

            VBox leftActionPanel = moduleButtons(moduleList, rightActionPanel, moduleDetails);
            twoPanelLayout(leftActionPanel, rightActionPanel, "Modules");
        }
    }


    /** Creates the module buttons
     * @param moduleList - The list of modules the student takes
     * @param rightPanel - The panel that the content will fill
     * @param moduleDetails - The panel that will be filled with the module details of the selected module
     * @return - A VBox containing the module buttons
     */
    private VBox moduleButtons(List<Map<String, String>> moduleList, VBox rightPanel, VBox moduleDetails){
        VBox panel = new VBox();
        HBox tempButton;
        for (Map<String, String> module : moduleList) {
            tempButton = makeModuleListButton(
                    module.get("Id"),
                    module.get("Name"),
                    module.get("Credit")
            );
            tempButton.setOnMouseClicked(event->pickModule(module.get("Id"), module, rightPanel, moduleDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanel(courseListPanel);
    }


    /** The event that will occur when a module button is picked
     * @param id - the id of the selected module
     * @param tempModule - the details of the selected module
     * @param rightPanel - the right hand panel that will be filled with details
     * @param moduleDetails - the module panel that will be filled with module details
     */
    private void pickModule(String id, Map<String, String> tempModule, VBox rightPanel, VBox moduleDetails){

        moduleDetails.getChildren().set(0, infoContainer(moduleDetailDisplay(tempModule)));

        ArrayList<Button> moduleBtnsList = new ArrayList<>();

        moduleBtnsList.add(currentButtons.get("VIEW MATERIALS"));

        currentText = new HashMap<>();
        currentValues.put("ID", id);

        VBox courseActionsDisplay = makeScrollablePart(createButtonsVBox(moduleBtnsList));

        rightPanel.getChildren().set(0, moduleDetails);
        rightPanel.getChildren().set(1, courseActionsDisplay);
        rightPanel.setVisible(true);
    }


    //Course Dashboard Elements


    /** Creates the dashboard for a student viewing their courses
     * @param studentCourse - A map containing info on the student's course
     */
    public void course(Map<String, String> studentCourse) {
        VBox courseDetails;
        if (studentCourse.isEmpty()) {
            courseDetails = makePanel(emptyModelContent("course"));
        } else {
            courseDetails = makePanel(new VBox(infoContainer(courseDetailDisplay(studentCourse))));
        }
        singlePanelLayout(courseDetails, "Course");
    }



    // Materials

    public final static int WEEKS_PER_SEM = 12;

    /** Creates the materials dashboard
     * @param moduleID - The module id of the materials being looked at
     * @param materialList - The list of materials
     * @param twoSems - Boolean of whether two sems are being checked or not
     */
    public void materials(String moduleID, List<Map<String, Boolean>> materialList, boolean twoSems){
        resetCurrentValues();
        currentValues.put("ID",moduleID);

        inputButton("VIEW LECTURE MATERIAL");
        inputButton("VIEW LAB MATERIAL");

        if (materialList.isEmpty()) {
            VBox mainPanel = makePanel(emptyModelContent("materials"));
            singlePanelLayout(mainPanel, "Modules");
        } else {
            VBox materialDetails = new VBox(new VBox());

            VBox rightActionPanel = makePanel(new VBox());
            rightActionPanel.getChildren().add(0, materialDetails);
            rightActionPanel.setVisible(false);


            VBox leftActionPanel;

            if (twoSems) {
                leftActionPanel = weekButtons2Sem(
                        materialList, rightActionPanel, materialDetails,
                        getSemWeekButtons(1, materialList, rightActionPanel, materialDetails),
                        getSemWeekButtons(2, materialList, rightActionPanel, materialDetails));
            } else {
                leftActionPanel = weekButtons(materialList, rightActionPanel, materialDetails);
            }

            twoPanelLayout(leftActionPanel, rightActionPanel, "Modules");
        }

    }


    /** Creates the semesters and week buttons
     * @param semester - the semester
     * @param materialList - the list of materials
     * @param rightPanel - the right hand panel
     * @param materialDetails - the panel that will be filled with material details
     * @return - A VBox containing the semesters and week buttons
     */
    private VBox getSemWeekButtons(int semester, List<Map<String, Boolean>> materialList, VBox rightPanel,
                                   VBox materialDetails) {
        HBox tempButton;
        VBox semWeekButtonList = new VBox();
        for (int sem=1; sem<=2;sem++) {
            for (int week=1; week<=WEEKS_PER_SEM;week++) {
                if(sem==semester){
                    tempButton = makeWeekButton(week);
                    tempButton.setOnMouseClicked(pickWeek(week, materialList.get((sem-1)*12 +(week-1)), rightPanel, materialDetails));
                    semWeekButtonList.getChildren().add(tempButton);
                }
            }
        }
        return semWeekButtonList;
    }




    /** Creates the week buttons
     * @param materialList - The list of whether the materials exist or not
     * @param rightPanel - The right hand panel which will be filled by the button click
     * @param materialDetails - The panel in which the material details will be entered
     * @return - A VBox containing the week buttons
     */
    private VBox weekButtons(List<Map<String, Boolean>> materialList, VBox rightPanel, VBox materialDetails){
        VBox panel = new VBox();
        HBox tempButton;
        for (int i=1; i<=WEEKS_PER_SEM;i++) {
            tempButton = makeWeekButton(i);
            tempButton.setOnMouseClicked(pickWeek(i, materialList.get(i-1), rightPanel, materialDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanel(courseListPanel);
    }


    /** Creates an event handler that handles clicking the week buttons
     * @param weekNo - The week number that was selected
     * @param materials - The list of if the materials exist or not
     * @param rightPanel - The right hand panel which will be filled by the button click
     * @param materialDetails - The panel in which the material details will be entered
     * @return - An event handler that handles clicking the week buttons
     */
    private EventHandler<Event> pickWeek(int weekNo, Map<String, Boolean> materials, VBox rightPanel, VBox materialDetails){
        return event -> {
            materialDetails.getChildren().set(0, new VBox(new Text("Week " + weekNo)));

            currentValues.put("WEEK", String.valueOf(weekNo));

            VBox courseActionsDisplay;

            if(materials.get("Lab") && materials.get("Lecture")){
                ArrayList<Button> materialBtnsList = new ArrayList<>();

                materialBtnsList.add(currentButtons.get("VIEW LECTURE MATERIAL"));
                materialBtnsList.add(currentButtons.get("VIEW LAB MATERIAL"));

                courseActionsDisplay = makeScrollablePart(createButtonsVBox(materialBtnsList));

            }else if(materials.get("Lecture")){
                ArrayList<Button> materialBtnsList = new ArrayList<>();
                materialBtnsList.add(currentButtons.get("VIEW LECTURE MATERIAL"));
                courseActionsDisplay = makeScrollablePart(createButtonsVBox(materialBtnsList));

            }else if(materials.get("Lab")){
                ArrayList<Button> materialBtnsList = new ArrayList<>();
                materialBtnsList.add(currentButtons.get("VIEW LAB MATERIAL"));
                courseActionsDisplay = makeScrollablePart(createButtonsVBox(materialBtnsList));

            }else{
                courseActionsDisplay = makeScrollablePart(new VBox(new Text("No Material to Show!")));
            }

            rightPanel.getChildren().set(0, materialDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

}
