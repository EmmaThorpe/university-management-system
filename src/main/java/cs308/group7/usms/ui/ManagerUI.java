package cs308.group7.usms.ui;

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

public class ManagerUI extends MainUI{

    public void dashboard() {
        resetCurrentValues();
        HBox toolbar = makeToolbar();

        Button mngModuleBtn = inputButton("MANAGE MODULES");
        Button mngCourseBtn = inputButton("MANAGE COURSES");
        Button mngSignupBtn = inputButton("MANAGE SIGN-UP WORKFLOW");
        Button mngAccountsBtn = inputButton("MANAGE ACCOUNTS");
        Button mngRulesBtn = inputButton("MANAGE BUSINESS RULES");

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

        currScene = new Scene(root);
    }

    public void accounts(List<User> accountList) {
        resetCurrentValues();
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

        currScene = new Scene(root);
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

            FontIcon appGraphic;
            if (userType.equals(User.UserType.STUDENT)) {
                appGraphic =  new FontIcon(FontAwesomeSolid.USER);
            } else if (userType.equals(User.UserType.LECTURER)) {
                appGraphic =  new FontIcon(FontAwesomeSolid.CHALKBOARD_TEACHER);
            } else {
                appGraphic = new FontIcon(FontAwesomeSolid.USER_TIE);
            }
            StackPane iconStack = makeCircleIcon(25, "list-back" ,appGraphic, "list-graphic");

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
                Button activatedBtn = new Button("ACTIVATE");
                Button deactivatedBtn = new Button("DEACTIVATE");

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

                accountBtnView.setAlignment(Pos.CENTER);
                accountBtnView.setSpacing(20.0);
                accountBtnView.setPadding(new Insets(10));

                rightPanel.getChildren().set(0, accText);
                rightPanel.getChildren().set(1, accountBtnView);
                rightPanel.setVisible(true);
            }
        };
    }


}
