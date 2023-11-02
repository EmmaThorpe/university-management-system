package cs308.group7.usms.ui;

import cs308.group7.usms.model.Student;
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

public class ManagerUI {

    public Scene dashboard() {
        HBox toolbar = makeToolbar();

        VBox mainActionPanel = makePanel(new Text[]{new Text("hi\nhello\ngoodbye!")});
        VBox subActionPanel = makePanel(new Text[]{new Text("hi\nhello\ngoodbye!")});

        HBox actionPanel = new HBox(mainActionPanel, subActionPanel);
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

        HBox container = new HBox(titleContainer, breadcrumbContainer);
        container.setPadding(new Insets(15, 10, 15, 10));
        container.setSpacing(40);
        container.getStyleClass().add("toolbar-bar");

        return container;
    }

    private VBox makePanel(Node[] content) {
        VBox panel = new VBox(content);
        panel.setPadding(new Insets(20));
        panel.getStyleClass().add("panel");

        return panel;
    }
}
