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

        Button mngModuleBtn = inputButton("VIEW DECISION");
        Button mngCourseBtn = inputButton("VIEW COURSE");
        Button mngSignupBtn = inputButton("VIEW MODULES");
        Button passwordBtn = inputButton("CHANGE PASSWORD");
        Button fileBtn = inputButton("OPEN FILE");

        passwordBtn.getStyleClass().add("toolbar-btn");
        makeModal(passwordBtn, "CHANGE PASSWORD", resetPassUser(), true);


        Button[] mngBtns = {mngModuleBtn, mngCourseBtn, mngSignupBtn, passwordBtn, fileBtn};
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
            currentID = id;

            VBox courseActionsDisplay = makeScrollablePart(moduleBtnView);

            rightPanel.getChildren().set(0, moduleDetails);
            rightPanel.getChildren().set(1, courseActionsDisplay);
            rightPanel.setVisible(true);
        };
    }


    public void materials(List<Map<String,String>> mater, boolean twoSems){
        if(twoSems){

        }else{

        }

    }







}
