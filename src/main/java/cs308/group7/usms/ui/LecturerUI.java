package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerUI extends UIElements{

    public void dashboard() {
        resetCurrentValues();
        HBox toolbar = makeToolbar("Lecturer");

        Button viewModuleBtn = inputButton("VIEW MODULE");
        Button giveMarkBtn = inputButton("GIVE MARK");
        Button addMaterialBtn = inputButton("CHECK MATERIAL");
        Button passwordBtn = inputButton("CHANGE PASSWORD");

        makeModal(passwordBtn, "CHANGE PASSWORD", resetPassUser(), true);

        Button[] mngBtns = {viewModuleBtn, giveMarkBtn, addMaterialBtn, passwordBtn};
        createDashboard(mngBtns, toolbar);
    }

    /* Module dashboard */
    public void module(Map<String, String> lecturerModule){
        resetCurrentValues();
        currentValues = new HashMap<>();
        currentValues.put("ID", lecturerModule.get("Id"));

        Button update = inputButton("UPDATE MODULE INFORMATION");
        makeModal(update, "EDIT", editModule(lecturerModule),  false);
        VBox modulePanel = makePanelWithAction(
                new VBox(infoContainer(moduleDetailDisplay(lecturerModule))),
                update
        );
        singlePanelLayout(modulePanel, "Module");
    }

    /**
     * Mark Dashboard
     **/

    public void mark (List<Map<String, String>> students){
        resetCurrentValues();

        Button lab = inputButton("SET LAB MARK");
        Button exam = inputButton("SET EXAM MARK");

        makeModal( lab, "ASSIGN LAB MARK", new VBox(),  false);
        makeModal( exam, "ASSIGN EXAM MARK", new VBox(),  false);

        VBox accountDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, accountDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = markButtons(students, rightActionPanel, accountDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Accounts");
    }

    private VBox markButtons(List<Map<String, String>> accountList, VBox rightPanel, VBox details){
        VBox panel = new VBox();
        HBox tempButton;
        for (Map<String, String> account : accountList) {
            tempButton = makeStudentMarkListButton(
                    account.get("userID"),
                    account.get("forename"),
                    account.get("surname"),
                    account.get("labMark"),
                    account.get("examMark")
            );
            tempButton.setOnMouseClicked(pickStudent(account, rightPanel, details));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane accountListPanel = new ScrollPane(panel);
        return makeScrollablePanel(accountListPanel);
    }

    private EventHandler<Event> pickStudent(Map<String, String> user, VBox rightPanel, VBox accDetails){
        return event -> {
            currentValues = new HashMap<>();
            currentValues.put("StudentID", user.get("userID"));
            currentValues.put("AttemptNo", user.get("attemptNo"));

            accDetails.getChildren().set(0, infoContainer(studentMarkDisplay(
                    user.get("userID"),
                    user.get("forename"),
                    user.get("surname"),
                    user.get("labMark"),
                    user.get("examMark")
            )));

            ArrayList<Button> studentBtnsList = new ArrayList<>();

            studentBtnsList.add(currentButtons.get("SET LAB MARK"));
            studentBtnsList.add(currentButtons.get("SET EXAM MARK"));

            Button[] studentBtns = studentBtnsList.toArray(new Button[0]);
            stylePanelActions(studentBtns);
            VBox studentBtnView = new VBox(studentBtns);

            studentBtnView.setAlignment(Pos.CENTER);
            studentBtnView.setSpacing(20.0);
            studentBtnView.setPadding(new Insets(10));

            VBox studentActionsDisplay = makeScrollablePart(studentBtnView);

            setModalContent("ASSIGN LAB MARK", setLabMark(user));
            setModalContent("ASSIGN EXAM MARK", setExamMark(user));

            rightPanel.getChildren().set(0, accDetails);
            rightPanel.getChildren().set(1, studentActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    /**
     * Mark Dashboard - modals
     **/

    private VBox setLabMark(Map<String, String> currentStudent) {
        return setTextAndField("LAB MARK", currentStudent.get("labMark"),
                markCheck(
                        0.0,
                        100.0,
                        "LAB MARK",
                        "Lab mark",
                        "MARK",
                        "LAB")
        );
    }

    private VBox setExamMark(Map<String, String> currentStudent) {
        return setTextAndField("EXAM MARK", currentStudent.get("examMark"),
                markCheck(
                        0.0,
                        100.0,
                        "EXAM MARK",
                        "Exam mark",
                        "MARK",
                        "EXAM")
        );
    }

    public void materials(String moduleID, List<Map<String, Boolean>> materialList, String semesters){
        resetCurrentValues();
        currentValues.put("ID",moduleID);

        inputButton("VIEW LECTURE MATERIAL");
        inputButton("VIEW LAB MATERIAL");

        inputButton("CHANGE LECTURE MATERIAL").setOnAction(event -> uploadFile());
        inputButton("CHANGE LAB MATERIAL");

        VBox materialDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, materialDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel;

        if(semesters.equals("1&2")){
            leftActionPanel = weekButtons2Sem(materialList, rightActionPanel, materialDetails);
        }else{
            leftActionPanel = weekButtons(Integer.parseInt(semesters), materialList, rightActionPanel, materialDetails);
        }

        twoPanelLayout(leftActionPanel, rightActionPanel, "Modules");


    }


    private VBox weekButtons(int semNo, List<Map<String, Boolean>> materialList, VBox rightPanel, VBox materialDetails){
        VBox panel = new VBox();
        HBox tempButton;
        for (int i=1; i<=2;i++) {
            tempButton = makeWeekButton(i);
            tempButton.setOnMouseClicked(pickWeek(i, semNo, materialList.get(i-1), rightPanel, materialDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanel(courseListPanel);
    }



    private VBox weekButtons2Sem(List<Map<String, Boolean>> materialList, VBox rightPanel, VBox materialDetails){
        ToggleGroup semSelected = new ToggleGroup();
        ToggleButton sem1 = setToggleOption(semSelected, "Semester 1");
        ToggleButton sem2 = setToggleOption(semSelected, "Semester 2");
        sem1.setSelected(true);

        HBox.setHgrow(sem1, Priority.ALWAYS);
        HBox.setHgrow(sem2, Priority.ALWAYS);
        HBox semOptions = new HBox(sem1, sem2);

        VBox panelSem1 = new VBox();
        VBox panelSem2 = new VBox();

        HBox tempButton;
        for (int sem=1; sem<=2;sem++) {
            for (int week=1; week<=10;week++) {
                tempButton = makeWeekButton(week);
                tempButton.setOnMouseClicked(pickWeek(week, sem, materialList.get((sem-1)*10 +(week-1)), rightPanel, materialDetails));

                if(sem==1){
                    panelSem1.getChildren().add(tempButton);
                }else{
                    panelSem2.getChildren().add(tempButton);
                }


            }
        }


        panelSem1.setSpacing(20.0);
        panelSem1.setPadding(new Insets(10, 2, 10, 2));

        panelSem2.setSpacing(20.0);
        panelSem2.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane weekListPanel = new ScrollPane(panelSem1);

        semSelected.selectedToggleProperty().addListener(toggleSem(semSelected, weekListPanel, panelSem1, panelSem2, rightPanel));


        return new VBox(semOptions, makeScrollablePanel(weekListPanel));
    }



    private EventHandler<Event> pickWeek(int weekNo, int semNo, Map<String, Boolean> materials, VBox rightPanel, VBox materialDetails){
        return event -> {
            materialDetails.getChildren().set(0, new VBox(new Text(String.valueOf(weekNo))));

            currentValues.put("WEEK", String.valueOf(weekNo));
            currentValues.put("SEMESTER", String.valueOf(semNo));

            VBox courseActionsDisplay;

            ArrayList<Button> materialBtnsList = new ArrayList<>();

            if(materials.get("Lab") && materials.get("Lecture")){
                materialBtnsList.add(currentButtons.get("VIEW LECTURE MATERIAL"));
                materialBtnsList.add(currentButtons.get("VIEW LAB MATERIAL"));
                currentButtons.get("CHANGE LECTURE MATERIAL").setText("UPDATE LECTURE MATERIAL");
                currentButtons.get("CHANGE LAB MATERIAL").setText("UPDATE LAB MATERIAL");

            }else if(materials.get("Lab")) {
                materialBtnsList.add(currentButtons.get("VIEW LAB MATERIAL"));
                currentButtons.get("CHANGE LAB MATERIAL").setText("UPDATE LAB MATERIAL");
                currentButtons.get("CHANGE LECTURE MATERIAL").setText("ADD LECTURE MATERIAL");

            }else if(materials.get("Lecture")){
                materialBtnsList.add(currentButtons.get("VIEW LECTURE MATERIAL"));
                currentButtons.get("CHANGE LECTURE MATERIAL").setText("UPDATE LECTURE MATERIAL");
                currentButtons.get("CHANGE LAB MATERIAL").setText("ADD LAB MATERIAL");
            }else{
                currentButtons.get("CHANGE LECTURE MATERIAL").setText("ADD LECTURE MATERIAL");
                currentButtons.get("CHANGE LAB MATERIAL").setText("ADD LAB MATERIAL");
            }

            materialBtnsList.add(currentButtons.get("CHANGE LECTURE MATERIAL"));
            materialBtnsList.add(currentButtons.get("CHANGE LAB MATERIAL"));

            courseActionsDisplay = makeScrollablePart(createButtonsVBox(materialBtnsList));



            rightPanel.getChildren().set(0, materialDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    public File uploadFile(){
        FileChooser fileChooser = new FileChooser();
        List<File> listOfFiles = fileChooser.showOpenMultipleDialog(currentStage);
        return listOfFiles.get(0);
    }

}
