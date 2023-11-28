package cs308.group7.usms.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

    /**
     * Dashboard lecturers see when they log in
     */
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



    /* Module dashboard components*/

    /** Lecturer module dashboard
     * @param lecturerModule - map containing information on the module the lecturer runs
     */
    public void module(Map<String, String> lecturerModule){
        resetCurrentValues();
        currentValues = new HashMap<>();
        currentValues.put("ID", lecturerModule.get("Id"));

        Button update = inputButton("UPDATE MODULE INFORMATION");
        makeModal(update, "EDIT", editModule(lecturerModule),  false);
        VBox modulePanel;
        if (lecturerModule.isEmpty()) {
            modulePanel = makePanel(emptyModelContent("module"));
        } else {
            modulePanel = makePanelWithAction(
                    new VBox(infoContainer(moduleDetailDisplay(lecturerModule))),
                    update
            );
        }
        singlePanelLayout(modulePanel, "Module");
    }



     // Mark Dashboard Elements



    /** Mark  Dashboard
     * @param students - List of student info of people who take the module the lecturer runs
     */
    public void mark(List<Map<String, String>> students){
        resetCurrentValues();

        Button newMark = inputButton("SET NEW MARK");
        Button updatedMark = inputButton("UPDATE MARK");

        if (students.isEmpty()) {
            VBox mainPanel = makePanel(emptyModelContent("students"));
            singlePanelLayout(mainPanel, "Marks");
        } else {
            makeModal(newMark, "ASSIGN MARK", setMark("ASSIGN", "", "", false),  true);
            makeModal(updatedMark, "CHANGE MARK", new VBox(),  false);

            VBox accountDetails = new VBox(new VBox());

            VBox rightActionPanel = makePanel(new VBox());
            rightActionPanel.getChildren().add(0, accountDetails);
            rightActionPanel.setVisible(false);

            VBox leftActionPanel = markButtons(students, rightActionPanel, accountDetails);
            twoPanelLayout(leftActionPanel, rightActionPanel, "Accounts");
        }

    }


    /** Creates the buttons for students that shows their mark
     * @param accountList - a list of student details
     * @param rightPanel - the panel on the right hand side of the screen that gets filled when the buttons are pressed
     * @param details - the panel that will be filled with user details based on the button that is clicked
     * @return - a vbox containing the buttons
     */
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


    /** generates the event that handles a student being picked
     * @param user - map of user details
     * @param rightPanel - the panel on the right hand side of the screen that gets filled when the buttons are pressed
     * @param accDetails - the panel on account details that gets filled when the buttons are pressed
     * @return - the event handler that handles the button click event
     */
    private EventHandler<Event> pickStudent(Map<String, String> user, VBox rightPanel, VBox accDetails){
        return event -> {
            currentValues = new HashMap<>();
            currentValues.put("StudentID", user.get("userID"));

            ArrayList<Button> studentBtnsList = new ArrayList<>();

            if(user.get("labMark")!=null || user.get("examMark") != null){  //if at least 1 mark exists than display that mark and allow it to be updated
                currentValues.put("AttemptNo", user.get("attemptNo"));

                accDetails.getChildren().set(0, infoContainer(studentMarkDisplay(
                        user.get("userID"),
                        user.get("forename"),
                        user.get("surname"),
                        user.get("labMark"),
                        user.get("examMark")
                )));



                studentBtnsList.add(currentButtons.get("SET NEW MARK"));
                studentBtnsList.add(currentButtons.get("UPDATE MARK"));

                setModalContent("CHANGE MARK", setMark("CHANGE", user.get("labMark"), user.get("examMark"), true));

            }else{
                currentValues.put("AttemptNo", "0");
                studentBtnsList.add(currentButtons.get("SET NEW MARK"));
            }

            VBox studentActionsDisplay = makeScrollablePart(createButtonsVBox(studentBtnsList));


            rightPanel.getChildren().set(0, accDetails);
            rightPanel.getChildren().set(1, studentActionsDisplay);
            rightPanel.setVisible(true);
        };
    }




    /** Creates the set mark fields
     * @param type - the type of action the field represents (either assigning or changing)
     * @param labMark - the lab mark to be set to the field
     * @param examMark - the exam mark to be set to the field
     * @param valid - boolean of whether to set the field to be valid or not when it starts
     * @return - VBox containing the mark fields
     */
    private VBox setMark(String type, String labMark, String examMark, boolean valid) {
        VBox lab = setTextAndField(type+" LAB MARK",
                String.valueOf(labMark),
                markCheck(
                        0.0,
                        100.0,
                        type+" LAB MARK",
                        "Lab mark",
                        "MARK",
                        type), valid
        );

        VBox exam = setTextAndField(type+" EXAM MARK",
                String.valueOf(examMark),
                markCheck(
                        0.0,
                        100.0,
                        type+" EXAM MARK",
                        "Exam mark",
                        "MARK",
                        type), valid
        );


        return new VBox(lab, exam);
    }



    // Material dashboard


    /** Creates the materials dashboard
     * @param moduleID - The id of the module the material is for
     * @param materialList - a list mapping if the materials exist or not
     */
    public void materials(String moduleID, List<Map<String, Boolean>> materialList){
        resetCurrentValues();
        currentValues.put("ID",moduleID);

        inputButton("VIEW LECTURE MATERIAL");
        inputButton("VIEW LAB MATERIAL");

        inputButton("CHANGE LECTURE MATERIAL").setOnAction(event -> uploadFile());
        inputButton("CHANGE LAB MATERIAL");

        if(materialList.isEmpty()) {
            VBox mainPanel = makePanel(emptyModelContent("materials"));
            singlePanelLayout(mainPanel, "Materials");
        } else {

            VBox materialDetails = new VBox(new VBox());

            VBox rightActionPanel = makePanel(new VBox());
            rightActionPanel.getChildren().add(0, materialDetails);
            rightActionPanel.setVisible(false);

            VBox leftActionPanel;

            leftActionPanel = weekButtons2Sem(
                    materialList, rightActionPanel, materialDetails,
                    getSemWeekButtons(1, materialList, rightActionPanel, materialDetails),
                    getSemWeekButtons(2, materialList, rightActionPanel, materialDetails));

            twoPanelLayout(leftActionPanel, rightActionPanel, "Materials");
        }
    }


    /** Creates the week and semester and week buttons
     * @param sem - The semester to show
     * @param materialList - The list containing whether material exists or not for a given week
     * @param rightPanel - The right panel that will be shown
     * @param materialDetails - The panel where the materials section will be displayed
     * @return - VBox containing the week and semesters buttons
     */
    private VBox getSemWeekButtons(int sem, List<Map<String, Boolean>> materialList, VBox rightPanel,
                                   VBox materialDetails) {
        HBox tempButton;
        VBox semWeekButtonList = new VBox();
        for (int week=1; week<=12;week++) {
            tempButton = makeWeekButton(week);
            tempButton.setOnMouseClicked(pickWeek(week, sem, materialList.get((sem-1)*12 +(week-1)), rightPanel, materialDetails));
            semWeekButtonList.getChildren().add(tempButton);
        }
        return semWeekButtonList;
    }



    /** Creates the event that handles when the week buttons are pressed
     * @param weekNo - the week number selected
     * @param semNo - the semester selected
     * @param materials - the list of whether the material exists or not
     * @param rightPanel  - The right panel that will be shown
     * @param materialDetails - The panel where the materials section will be displayed
     * @return - An event handler that handles the event of clicking on a week button
     */
    private EventHandler<Event> pickWeek(int weekNo, int semNo, Map<String, Boolean> materials, VBox rightPanel, VBox materialDetails){
        return event -> {
            materialDetails.getChildren().set(0, listDetail("Materials for week " + String.valueOf(weekNo) + ", " +
                            "Semester " + String.valueOf(semNo),
                    ""));


            currentValues.put("WEEK", String.valueOf(weekNo));
            currentValues.put("SEMESTER", String.valueOf(semNo));

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

            VBox materialActionsDisplay = makeScrollablePart(createButtonsVBox(materialBtnsList));

            rightPanel.getChildren().set(0, materialDetails);
            rightPanel.getChildren().set(1, materialActionsDisplay);
            rightPanel.setVisible(true);
        };
    }


    /** Uploads a file button
     * @return File - the file the user picks
     */
    public File uploadFile(){
        FileChooser fileChooser = new FileChooser();
        List<File> listOfFiles = fileChooser.showOpenMultipleDialog(currentStage);
        return listOfFiles.get(0);
    }

}
