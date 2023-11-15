package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserUI extends MainUI{

    private ImageView pdfImg;
    protected String currentID;

    public String getID(){
        return currentID;
    }


    protected VBox resetPass(Boolean manager) {
        validFields = new HashMap<>();
        VBox setPass = textAndField("NEW PASSWORD", passwordCheck(manager));
        VBox confirmPass = textAndField("CONFIRM NEW PASSWORD", confirmPasswordCheck(manager));

        return new VBox(setPass, confirmPass);
    }

    public VBox resetPassUser() {
        VBox oldPass = inputField("OLD PASSWORD", true);
        return new VBox(oldPass, resetPass(false));
    }


    protected ChangeListener<String> passwordCheck(Boolean manager){
        return (obs, oldText, newText) -> {
            if (newText.length() <8 || newText.length()>20) {
                currentText.get("NEW PASSWORD").setText("Passwords must be between 8 and 20 characters long");
            } else if (!validPassword(newText, currentText.get("NEW PASSWORD"))) {
                checkValidPasswordFields("NEW PASSWORD", false, manager);
            }else{
                currentText.get("NEW PASSWORD").setText("");
                checkValidPasswordFields("NEW PASSWORD", true, manager);
            }
        };

    }

    protected ChangeListener<String> confirmPasswordCheck(Boolean manager){
        return (obs, oldText, newText) -> {
            TextField newPassField = (TextField) currentFields.get("NEW PASSWORD");
            if (!newText.equals(newPassField.getText())) {
                currentText.get("CONFIRM NEW PASSWORD").setText("Passwords must match");
                checkValidPasswordFields("CONFIRM NEW PASSWORD", false, manager);
            } else {
                currentText.get("CONFIRM NEW PASSWORD").setText("");
                checkValidPasswordFields("CONFIRM NEW PASSWORD", true, manager);
            }
        };

    }


    private void checkValidPasswordFields(String type, Boolean value, Boolean manager){
        boolean disabled;
        validFields.put(type, value);
        disabled = !validFields.get("NEW PASSWORD") || !validFields.get("CONFIRM NEW PASSWORD");

        if(manager){
            currentButtons.get("RESET USER PASSWORD").setDisable(disabled);
        }else{
            currentButtons.get("CHANGE PASSWORD").setDisable(disabled);
        }

    }


    protected HBox makeToolbar(String role) {
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        StackPane iconStack = makeCircleIcon(25, "toolbar-back" ,appGraphic, "toolbar-graphic");

        Text title = new Text(role);
        title.setTranslateY(5.0);
        title.getStyleClass().add("toolbar-title");

        HBox titleContainer = new HBox(iconStack, title);
        titleContainer.setPadding(new Insets(10));
        titleContainer.setSpacing(10);


        Button homeBtn = inputButton("HOME");
        homeBtn.getStyleClass().add("toolbar-btn");
        Button logoutBtn = inputButton("LOG OUT");
        logoutBtn.getStyleClass().add("toolbar-btn");
        HBox.setMargin(logoutBtn, new Insets(10));

        HBox logoutContainer = new HBox(homeBtn, logoutBtn);
        logoutContainer.setAlignment(Pos.CENTER);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox container = new HBox(titleContainer, region, logoutContainer);
        container.setPadding(new Insets(15));
        container.setSpacing(50);
        container.getStyleClass().add("toolbar-bar");

        return container;
    }

    protected void twoPanelLayout(VBox left, VBox right, String title){

        HBox toolbar = makeToolbar(title);

        HBox actionPanel = new HBox(left, right);

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


    //Module Elements

    protected HBox makeModuleListButton(String id, String name, String credit) {
        HBox yearsDisplay = listDetail("CREDITS" , credit);
        Text nameDisplay = new Text(name);

        VBox courseDetails = new VBox(nameDisplay, yearsDisplay);
        courseDetails.setSpacing(5.0);
        HBox listButton = makeListButton(id, new FontIcon(FontAwesomeSolid.CHALKBOARD), courseDetails);
        return listButton;
    }


    protected VBox moduleDetailDisplay(Map<String, String> tempModule) {
        Text idTitle = new Text(tempModule.get("Name"));
        idTitle.getStyleClass().add("info-box-title");

        String creditsValue = tempModule.get("Credit");
        HBox col1 = new HBox (
                listDetail("NAME", tempModule.get("Name")),
                listDetail("CREDITS", creditsValue)
        );
        VBox col2 = infoDetailLong("DESCRIPTION", tempModule.get("Description"));



        VBox col3 = infoDetailLong("LECTURER(S)", tempModule.get("Lecturers"));

        col1.setSpacing(5);
        col2.setSpacing(5);
        return new VBox(idTitle, col1, col2, col3);
    }



    public void displayPDF(File file, String type){
        resetCurrentValues();

        Document pdf = showPage();

        int amount = pdf.getNumberOfPages()-1;
        Text pageNo = inputText("PAGE NO");

        currentText.get("PAGE NO").setText("1");
        pdfImg = new ImageView(pdfToImg(pdf, 0));

        Button backBtn = inputButton("<-");
        Button forwardBtn = inputButton("->");

        backBtn.setOnAction(event -> backPage(pdf));
        forwardBtn.setOnAction(event -> forwardPage(pdf, amount));

        BorderPane root = new BorderPane();
        HBox toolbar = makeToolbar(type);

        root.setTop(toolbar);
        root.setLeft(backBtn);
        root.setRight(forwardBtn);
        root.setBottom(pageNo);

        root.setCenter(new ScrollPane(pdfImg));
        currScene = new Scene(root);

    }


    protected void backPage(Document pdf){
        int page = Integer.parseInt(currentText.get("PAGE NO").getText());
        if(page>=1){
            pdfImg.setImage(pdfToImg(pdf, page-2));
            currentText.get("PAGE NO").setText(String.valueOf(page-1));
        }

    }

    protected void forwardPage(Document pdf, int amount){
        int page = Integer.parseInt(currentText.get("PAGE NO").getText());

        if(page <= amount){
            pdfImg.setImage(pdfToImg(pdf, page));
            currentText.get("PAGE NO").setText(String.valueOf(page+1));
        }
    }







    protected Document showPage() {


        Document currentDocument = new Document();
        try {
            File file = new File("src/main/resources/CS308_Coursework.pdf");
            currentDocument.setFile(file.getAbsolutePath());
            return currentDocument;


        } catch (PDFException | PDFSecurityException | IOException ex) {
        }


        return null;
    }

    protected WritableImage pdfToImg(Document currentDocument, int page){
        float scale = 1f;

        try {
            BufferedImage image = (BufferedImage) currentDocument.getPageImage(page,
                    GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0f, scale);

            WritableImage pdfImage = SwingFXUtils.toFXImage(image, null);

            return pdfImage;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}
