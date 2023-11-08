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

import java.util.ArrayList;
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

        return new Scene(root);
    }

    private VBox makePanel(VBox content) {
        content.setPadding(new Insets(20));
        content.setSpacing(20.0);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("panel");

        return panel;
    }

    private VBox makeScrollablePanel(ScrollPane content) {
        content.setPadding(new Insets(20));
        content.fitToHeightProperty().set(true);
        content.fitToWidthProperty().set(true);
        VBox panel = new VBox(content);
        panel.getStyleClass().add("panel");

        return panel;
    }

    public Scene accounts(List<User> accountList) {
        HBox toolbar = makeToolbar();

        Text accountText = new Text();

        VBox rightActionPanel = makePanel(new VBox());
        rightActionPanel.getChildren().add(0, accountText);
        rightActionPanel.setVisible(false);

        VBox leftActionPanel = userButtons(accountList, rightActionPanel, accountText);

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

    private VBox userButtons(List<User> accountList, VBox rightPanel, Text accText){
        VBox panel = new VBox();
        User tempUser;
        HBox tempButton;
        for(int i=0;i<accountList.size();i++){
            tempUser =accountList.get(i);
            tempButton = makeUserListButton(tempUser.getUserID(), tempUser.getForename(), tempUser.getSurname(),
                    tempUser.getType(), tempUser.getActivated());
            tempButton.setOnMouseClicked(pickUser(tempUser, rightPanel, accText));
            panel.getChildren().add(tempButton);
        }

        panel.setSpacing(20.0);
        panel.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane accountListPanel = new ScrollPane(panel);
        return makeScrollablePanel(accountListPanel);
    }

    private HBox makeUserListButton(String userID, String fname, String lname, User.UserType userType,
                                  boolean activated) {
            Text nameDisplay = new Text(fname.concat(" ".concat(lname)));

            StackPane iconStack = new StackPane();
            Circle appGraphicBack = new Circle(25);
            appGraphicBack.getStyleClass().add("list-back");
            FontIcon appGraphic;
            if (userType.equals(User.UserType.STUDENT)) {
                appGraphic =  new FontIcon(FontAwesomeSolid.USER);
            } else {
                appGraphic =  new FontIcon(FontAwesomeSolid.CHALKBOARD_TEACHER);
            }
            appGraphic.getStyleClass().add("list-graphic");

            iconStack.getChildren().addAll(appGraphicBack, appGraphic);

            Text IDdisplay = new Text(userID);
            IDdisplay.getStyleClass().add("list-id");

            HBox activatedDisplay = new HBox();
            if (activated) {
                activatedDisplay.getChildren().add(new Text("ACTIVATED"));
                activatedDisplay.getStyleClass().add("list-active");
            } else {
                activatedDisplay.getChildren().add(new Text("DEACTIVATED"));
                activatedDisplay.getStyleClass().add("list-inactive");
            }

            VBox userDetails = new VBox(nameDisplay, activatedDisplay);
            userDetails.setSpacing(5.0);

            HBox listButton = new HBox(IDdisplay, iconStack, userDetails);
            listButton.setAlignment(Pos.CENTER);
            listButton.setSpacing(20.0);
            listButton.setPadding(new Insets(10));
            listButton.getStyleClass().add("list-button");

            HBox.setHgrow(userDetails, Priority.ALWAYS);
            return listButton;
    }

    private EventHandler pickUser(User tempUser, VBox rightPanel, Text accText){
        return new EventHandler() {
            @Override
            public void handle(Event event) {
                accText.setText(tempUser.getUserID());

                ArrayList<Button> accountBtnsList = new ArrayList<Button>();

                if (tempUser.getType().equals(User.UserType.STUDENT)) {
                    Button studentDecisionBtn = new Button("ISSUE STUDENT DECISION");
                    accountBtnsList.add(studentDecisionBtn);
                    Button enrolBtn = new Button("ENROL STUDENT INTO COURSE");
                    accountBtnsList.add(enrolBtn);
                }
                if (tempUser.getType().equals(User.UserType.LECTURER)) {
                    Button assignModuleBtn = new Button("ASSIGN LECTURER TO MODULE");
                    accountBtnsList.add(assignModuleBtn);
                }

                Button passResetBtn = new Button("PASSWORD RESET");
                Button activatedBtn = new Button("ACTIVATED");
                Button deactivatedBtn = new Button("DEACTIVATED");

                accountBtnsList.add(passResetBtn);
                accountBtnsList.add(activatedBtn);
                accountBtnsList.add(deactivatedBtn);

                Button[] accountBtns = accountBtnsList.toArray(new Button[0]);
                accountBtns = stylePanelActions(accountBtns);

                if (tempUser.getActivated()) {
                    activatedBtn.setDisable(true);
                    deactivatedBtn.setDisable(false);
                } else {
                    activatedBtn.setDisable(false);
                    deactivatedBtn.setDisable(true);
                }

                VBox accountBtnView = new VBox(accountBtns);
                accountBtnView.setSpacing(10.0);
                accountBtnView.setPadding(new Insets(10.0));

                rightPanel.getChildren().set(0, accText);
                rightPanel.getChildren().set(1, accountBtnView);
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

        Button logoutBtn = new Button("LOG OUT");
        logoutBtn.getStyleClass().add("logout-btn");
        //logoutBtn.setOnAction(hide);
        HBox.setMargin(logoutBtn, new Insets(10));

        HBox logoutContainer = new HBox(logoutBtn);
        logoutContainer.setAlignment(Pos.CENTER);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox container = new HBox(titleContainer, region, logoutContainer);
        container.setPadding(new Insets(15));
        container.setSpacing(50);
        container.getStyleClass().add("toolbar-bar");

        return container;
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
