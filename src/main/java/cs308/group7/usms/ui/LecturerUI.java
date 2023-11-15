package cs308.group7.usms.ui;

import cs308.group7.usms.model.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LecturerUI extends UserUI{

    public void dashboard() {
        resetCurrentValues();
        HBox toolbar = makeToolbar("Lecturer");

        Button viewModuleBtn = inputButton("VIEW MODULE");
        Button giveMarkBtn = inputButton("GIVE MARK");
        Button addMaterialBtn = inputButton("ADD MATERIAL");
        Button passwordBtn = inputButton("CHANGE PASSWORD");

        passwordBtn.getStyleClass().add("toolbar-btn");
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

}
