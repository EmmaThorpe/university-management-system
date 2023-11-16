package cs308.group7.usms.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jpedal.PdfDecoderFX;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentUI extends UserUI{

    private final PdfDecoderFX pdf = new PdfDecoderFX();
    public void dashboard() {
        resetCurrentValues();
        HBox toolbar = makeToolbar("Student");

        Button viewDecisionBtn = inputButton("VIEW DECISION");
        Button viewCourseBtn = inputButton("VIEW COURSE");
        Button viewModuleBtn = inputButton("VIEW MODULES");
        Button passwordBtn = inputButton("CHANGE PASSWORD");
        Button fileBtn = inputButton("OPEN FILE");

        makeModal(passwordBtn, "CHANGE PASSWORD", resetPassUser(), true);


        Button[] mngBtns = {viewDecisionBtn, viewCourseBtn, viewModuleBtn, passwordBtn, fileBtn};
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
     * Decision Dashboard
     **/

    public void decision(List<Map<String, String>> moduleList, List<Map<String,String>> markList, String decision) {
        resetCurrentValues();

        VBox topPanel = makeTopPanel(decisionDisplay(decision));

        VBox rightActionPanel = makeScrollableBottomPanel(new ScrollPane());
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = decisionButtons(moduleList, markList, rightActionPanel);
        threePanelLayout(leftActionPanel, rightActionPanel, topPanel, "Decision");
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
            tempButton.setOnMouseClicked(pickModuleMark(module.get("Id"), markList, rightPanel));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane moduleListPanel = new ScrollPane(panel);
        return makeScrollableBottomPanel(moduleListPanel);
    }

    private EventHandler pickModuleMark(String id, List<Map<String, String>> markList,
                                        VBox rightPanel){
        return event -> {
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
        };
    }




    /**
     * Modules Dashboard
     **/

    public void modules(List<Map<String, String>> moduleList) {
        resetCurrentValues();

        inputButton("VIEW MATERIALS");

        VBox moduleDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, moduleDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = moduleButtons(moduleList, rightActionPanel, moduleDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Modules");

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
            tempButton.setOnMouseClicked(pickModule(module.get("Id"), module, rightPanel, moduleDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanel(courseListPanel);
    }

    private EventHandler pickModule(String id, Map<String, String> tempModule, VBox rightPanel, VBox moduleDetails){
        return event -> {
            moduleDetails.getChildren().set(0, infoContainer(moduleDetailDisplay(tempModule)));

            ArrayList<Button> moduleBtnsList = new ArrayList<>();

            moduleBtnsList.add(currentButtons.get("VIEW MATERIALS"));

            Button[] moduleBtns = moduleBtnsList.toArray(new Button[0]);
            moduleBtns = stylePanelActions(moduleBtns);

            VBox moduleBtnView = new VBox(moduleBtns);

            moduleBtnView.setAlignment(Pos.CENTER);
            moduleBtnView.setSpacing(20.0);
            moduleBtnView.setPadding(new Insets(10));

            currentText = new HashMap<>();
            currentValues.put("ID", id);

            VBox courseActionsDisplay = makeScrollablePart(moduleBtnView);

            rightPanel.getChildren().set(0, moduleDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }

    /**
     * Course Dashboard
     **/

    public void course(Map<String, String> studentCourse) {
        VBox courseDetails = makePanel(new VBox(infoContainer(courseDetailDisplay(studentCourse))));
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

        VBox materialDetails = new VBox(new VBox());

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, materialDetails);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = weekButtons(materialList, rightActionPanel, materialDetails);
        twoPanelLayout(leftActionPanel, rightActionPanel, "Modules");


    }


    private VBox weekButtons(List<Map<String, Boolean>> materialList, VBox rightPanel, VBox materialDetails){
        VBox panel = new VBox();
        HBox tempButton;
        for (int i=1; i<=2;i++) {
            tempButton = makeWeekButton(i);
            tempButton.setOnMouseClicked(pickWeek(i, materialList.get(i-1), rightPanel, materialDetails));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane courseListPanel = new ScrollPane(panel);
        return makeScrollablePanel(courseListPanel);
    }


    private EventHandler pickWeek(int weekNo, Map<String, Boolean> materials, VBox rightPanel, VBox materialDetails){
        return event -> {
            materialDetails.getChildren().set(0, new VBox(new Text(String.valueOf(weekNo))));

            currentValues.put("WEEK", String.valueOf(weekNo));

            VBox courseActionsDisplay;

            if(materials.get("Lab") && materials.get("Lecture")){
                ArrayList<Button> materialBtnsList = new ArrayList<>();

                materialBtnsList.add(currentButtons.get("VIEW LECTURE MATERIAL"));
                materialBtnsList.add(currentButtons.get("VIEW LAB MATERIAL"));

                Button[] materialBtns = materialBtnsList.toArray(new Button[0]);
                materialBtns = stylePanelActions(materialBtns);

                VBox materialBtnView = new VBox(materialBtns);

                materialBtnView.setAlignment(Pos.CENTER);
                materialBtnView.setSpacing(20.0);
                materialBtnView.setPadding(new Insets(10));

                courseActionsDisplay = makeScrollablePart(materialBtnView);
            }else{
                courseActionsDisplay = makeScrollablePart(new VBox(new Text("No Material to Show!")));
            }

            rightPanel.getChildren().set(0, materialDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }







}
