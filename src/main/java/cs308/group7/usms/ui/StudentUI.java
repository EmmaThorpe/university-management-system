package cs308.group7.usms.ui;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentUI extends UIElements{

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

    /**
     * Decision Dashboard
     **/

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





    /**
     * Modules Dashboard
     **/

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


    /**
     * Course Dashboard
     **/

    public void course(Map<String, String> studentCourse) {
        VBox courseDetails;
        if (studentCourse.isEmpty()) {
            courseDetails = makePanel(emptyModelContent("course"));
        } else {
            courseDetails = makePanel(new VBox(infoContainer(courseDetailDisplay(studentCourse))));
        }
        singlePanelLayout(courseDetails, "Course");
    }

    /**
     * Materials
     **/

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

    private VBox getSemWeekButtons(int semester, List<Map<String, Boolean>> materialList, VBox rightPanel,
                                   VBox materialDetails) {
        HBox tempButton;
        VBox semWeekButtonList = new VBox();
        for (int sem=1; sem<=2;sem++) {
            for (int week=1; week<=10;week++) {
                if(sem==semester){
                    tempButton = makeWeekButton(week);
                    tempButton.setOnMouseClicked(pickWeek(week, materialList.get((sem-1)*10 +(week-1)), rightPanel, materialDetails));
                    semWeekButtonList.getChildren().add(tempButton);
                }
            }
        }
        return semWeekButtonList;
    }

    private VBox weekButtons(List<Map<String, Boolean>> materialList, VBox rightPanel, VBox materialDetails){
        VBox panel = new VBox();
        HBox tempButton;
        for (int i=1; i<=12;i++) {
            tempButton = makeWeekButton(i);
            tempButton.setOnMouseClicked(pickWeek(i, materialList.get(i-1), rightPanel, materialDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanel(courseListPanel);
    }

    private EventHandler<Event> pickWeek(int weekNo, Map<String, Boolean> materials, VBox rightPanel, VBox materialDetails){
        return event -> {
            materialDetails.getChildren().set(0, new VBox(new Text("Week " + String.valueOf(weekNo))));

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
