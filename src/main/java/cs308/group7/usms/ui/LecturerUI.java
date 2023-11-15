package cs308.group7.usms.ui;

import cs308.group7.usms.model.Student;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerUI extends UserUI{

    public void dashboard() {
        resetCurrentValues();
        HBox toolbar = makeToolbar("Lecturer");

        Button viewModuleBtn = inputButton("VIEW MODULE");
        Button giveMarkBtn = inputButton("GIVE MARK");
        Button addMaterialBtn = inputButton("ADD MATERIAL");
        Button passwordBtn = inputButton("CHANGE PASSWORD");

        makeModal(passwordBtn, "CHANGE PASSWORD", resetPassUser(), true);

        Button[] mngBtns = {viewModuleBtn, giveMarkBtn, addMaterialBtn, passwordBtn};
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

    /* Module dashboard */
    public void module(Map<String, String> lecturerModule){
        Button update = inputButton("UPDATE MODULE INFORMATION");
        makeModal(update, "edit", editModule(lecturerModule),  false);
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

    private EventHandler pickStudent(Map<String, String> user, VBox rightPanel, VBox accDetails){
        return event -> {
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

            setModalContent(currentModals.get("ASSIGN LAB MARK"), setLabMark(user));
            setModalContent(currentModals.get("ASSIGN EXAM MARK"), setExamMark(user));

            rightPanel.getChildren().set(0, accDetails);
            rightPanel.getChildren().set(1, studentActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    /**
     * Mark Dashboard - modals
     **/

    private VBox setLabMark(Map<String, String> currentStudent) {
        VBox setMark = inputFieldSetValue("Lab Mark", currentStudent.get("labMark"));
        return setMark;
    }

    private VBox setExamMark(Map<String, String> currentStudent) {
        VBox setMark = inputFieldSetValue("Exam Mark", currentStudent.get("examMark"));
        return setMark;
    }
}
