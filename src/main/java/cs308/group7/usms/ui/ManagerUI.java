package cs308.group7.usms.ui;

import cs308.group7.usms.model.Student;
import cs308.group7.usms.model.User;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.controlsfx.control.BreadCrumbBar;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Map;

import static java.awt.ComponentOrientation.RIGHT_TO_LEFT;

public class ManagerUI {

    Map<String, String> currentInfo;

    /** Gets the current info on the stage currently
     * @return Current info from page
     */
    public Map<String, String> getCurrentInfo(){
        return currentInfo;
    }


    /**Sets the current info being shown on stage
     * @param curr Current info from page
     */
    public void setCurrentInfo(Map<String, String> curr){
        currentInfo = curr;
    }

    public Scene dashboard() {
        HBox toolbar = makeToolbar();

        Button mngModuleBtn = new Button("MANAGE MODULES");
        Button mngCourseBtn = new Button("MANAGE COURSES");
        Button mngSignupBtn = new Button("MANAGE SIGN-UP WORKFLOW");
        Button mngAccountsBtn = new Button("MANAGE ACCOUNTS");
        Button mngRulesBtn = new Button("ADD BUSINESS RULES");

        Button[] mngBtns = {mngModuleBtn, mngCourseBtn, mngSignupBtn, mngAccountsBtn, mngRulesBtn};
        mngBtns = stylePanelActions(mngBtns);

        VBox mainActionPanel = makePanel(mngBtns);
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

        return new Scene(root);
    }

    private VBox makePanel(VBox content) {
        VBox panel = new VBox(content);
        panel.setPadding(new Insets(20));
        panel.getStyleClass().add("panel");

        return panel;
    }


    public Scene accounts(List<User> accountList) {
        HBox toolbar = makeToolbar();

        Text accountText = new Text();
        Button studentDecisionBtn = new Button("ISSUE STUDENT DECISION");
        Button passResetBtn = new Button("PASSWORD RESET");
        Button activatedBtn = new Button("ACTIVATED");
        Button deactivatedBtn = new Button("DEACTIVATED");

        VBox rightActionPanel = makePanel(new VBox(accountText, studentDecisionBtn, passResetBtn, activatedBtn, deactivatedBtn));
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = userButtons(accountList, rightActionPanel, accountText, activatedBtn, deactivatedBtn);

        HBox actionPanel = new HBox(leftActionPanel, rightActionPanel);

        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        BorderPane root = new BorderPane(actionPanel);
        root.setTop(toolbar);
        BorderPane.setMargin(toolbar, new Insets(15));
        BorderPane.setMargin(actionPanel, new Insets(15));

        root.setPadding(new Insets(10));

        return new Scene(root);
    }

    private VBox userButtons(List<User> accountList, VBox rightPanel, Text accText, Button activated, Button deactivated){
        VBox panel = new VBox();
        User tempUser;
        Button tempButton;
        for(int i=0;i<accountList.size();i++){
            tempUser =accountList.get(i);
            tempButton = new Button(tempUser.getUserID()+"- "+tempUser.getActivated());
            tempButton.setOnAction(pickUser(tempUser, rightPanel, accText, activated, deactivated));
            panel.getChildren().add(tempButton);
        }


        return makePanel(panel);
    }

    private EventHandler pickUser(User tempUser, VBox rightPanel, Text accText, Button activated, Button deactivated){
        return new EventHandler() {
            @Override
            public void handle(Event event) {
                accText.setText(tempUser.getUserID());
                if(tempUser.getActivated()){
                   activated.setVisible(false);
                   deactivated.setVisible(true);
                }else{
                    activated.setVisible(true);
                    deactivated.setVisible(false);

                }
                rightPanel.setVisible(true);
            }
        };
    }



    private HBox makeToolbar() {
        StackPane iconStack = new StackPane();
        Circle appGraphicBack = new Circle(25);
        appGraphicBack.getStyleClass().add("toolbar-back");
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        appGraphic.getStyleClass().add("toolbar-graphic");

        iconStack.getChildren().addAll(appGraphicBack, appGraphic);

        Text title = new Text("TITLE");
        title.setTranslateY(5.0);
        title.getStyleClass().add("toolbar-title");

        HBox titleContainer = new HBox(iconStack, title);
        titleContainer.setPadding(new Insets(10));
        titleContainer.setSpacing(10);

        BreadCrumbBar<String> breadcrumbBar = new BreadCrumbBar<>();

        TreeItem<String> breadcrumbBarOptions = BreadCrumbBar.buildTreeModel("Hello", "World", "This", "is", "cool");
        breadcrumbBar.setSelectedCrumb(breadcrumbBarOptions);

        VBox breadcrumbContainer = new VBox(breadcrumbBar);
        breadcrumbContainer.setAlignment(Pos.BASELINE_CENTER);

        Button logoutBtn = new Button("LOG OUT");
        logoutBtn.getStyleClass().add("logout-btn");
        //logoutBtn.setOnAction(hide);
        HBox.setMargin(logoutBtn, new Insets(10));

        HBox logoutContainer = new HBox(logoutBtn);
        logoutContainer.setAlignment(Pos.CENTER);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox container = new HBox(titleContainer, breadcrumbContainer, region, logoutContainer);
        container.setPadding(new Insets(15));
        container.setSpacing(50);
        container.getStyleClass().add("toolbar-bar");

        return container;
    }

    private VBox makePanel(Node[] content) {
        VBox panel = new VBox(content);
        panel.setPadding(new Insets(20));
        panel.setSpacing(20.0);
        panel.getStyleClass().add("panel");

        return panel;
    }

    private Button[] stylePanelActions (Button[] btns) {
        int i = 0;
        for (Button btn: btns) {
            if (i % 2 == 0) {
                btn.getStyleClass().add("panel-button-1");
            } else {
                btn.getStyleClass().add("panel-button-2");
            }
            i++;
        }
        return btns;
    }
}
