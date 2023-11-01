package cs308.group7.usms.ui;

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

public class LayoutUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = dashboard(stage);

        String css = this.getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.show();
    }

    public Scene dashboard(Stage stage) {
        HBox toolbar = makeToolbar();

        Button[] btns = new Button[2];
        btns[0] = new Button("Hello");
        btns[1] = new Button("world");

        VBox buttonPanel = makeContainer(btns);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setLeft(buttonPanel);

        root.setPadding(new Insets(10, 0, 10, 0));

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

    private VBox makeContainer(Button[] btns) {
        VBox con = new VBox(btns);
        con.setPadding(new Insets(10));
        con.setSpacing(10);
        return con;
    }
}
