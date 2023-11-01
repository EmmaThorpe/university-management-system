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

        BorderPane root = new BorderPane(new Text("hello"));
        root.setTop(toolbar);

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

        HBox container = new HBox(iconStack, title);
        container.setPadding(new Insets(15, 10, 15, 10));
        container.setSpacing(10);
        container.getStyleClass().add("toolbar-bar");

        return container;
    }
}
