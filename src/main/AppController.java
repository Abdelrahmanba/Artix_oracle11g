package main;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML public HBox upperBar;
    @FXML public JFXButton exit,minimize;
    @FXML public AnchorPane anchorRoot;
    public static Stage root;
    public static FXMLLoader registerLoad,registerNextLoad,adminMainLoad,addPaintingLoader,artistAdmin,userAdmin,
            InventroyAdmin,reportsAdmin,ordersAdmin,customerHome,searchResults,paintingLoader,emplyeesLoder;
    public  Scene scene;
    public StackPane login;

    public static void setRoot(Stage root){ AppController.root=root;}

    public void setAnchor() throws IOException {
        root.setScene(scene);
        this.setSceneLogin();
    }

    public  void setSceneLogin() throws IOException {
        FXMLLoader f=new FXMLLoader(getClass().getResource("../resources/views/Login.fxml"));
        login=f.load();
        BorderPane loginpane= (BorderPane) login.getChildren().get(0);
        VBox vbox=(VBox)loginpane.getCenter();
        FadeInUp fadeIn =new FadeInUp(vbox);
        fadeIn.setSpeed(0.6);
        fadeIn.play();
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add(login);
        anchorRoot.getChildren().add(upperBar);
    }

    public void setSenceRegister() throws IOException {
         registerLoad=new FXMLLoader(AppController.class.getResource("../resources/views/register.fxml"));
          anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
          anchorRoot.getChildren().add((StackPane)(registerLoad.load()));
          anchorRoot.getChildren().add(upperBar);
    }
    public void setSenceRegisterNext() throws IOException {
        registerNextLoad=new FXMLLoader(AppController.class.getResource("../resources/views/registerNext.fxml"));
        StackPane pane=(StackPane)(anchorRoot.getChildren().get(0));
        BorderPane bPane =(BorderPane)pane.getChildren().get(0);
        bPane.setCenter(registerNextLoad.load());
    }
    public void setSenceAdminMain() throws IOException {
        adminMainLoad=new FXMLLoader(AppController.class.getResource("../resources/views/adminMain.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((adminMainLoad.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }
    public void setSenceAddPainting() throws IOException {
        addPaintingLoader=new FXMLLoader(AppController.class.getResource("../resources/views/addNewPainting.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((addPaintingLoader.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }
    public void setSenceArtistsAdmin() throws IOException {
        artistAdmin=new FXMLLoader(AppController.class.getResource("../resources/views/artistAdmin.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((artistAdmin.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }
    public void setSenceUserssAdmin() throws IOException {
        userAdmin=new FXMLLoader(AppController.class.getResource("../resources/views/userAdmin.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((userAdmin.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }
    public void setInventoryAdmin() throws IOException {
        InventroyAdmin=new FXMLLoader(AppController.class.getResource("../resources/views/Inventory.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((InventroyAdmin.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }
    public void setReportsAdmin() throws IOException {
        reportsAdmin=new FXMLLoader(AppController.class.getResource("../resources/views/ReportsAdmin.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((reportsAdmin.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }
    public void setOrdersAdmin() throws IOException {
        ordersAdmin=new FXMLLoader(AppController.class.getResource("../resources/views/orders.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((ordersAdmin.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }
    public void setCustomerHome() throws IOException {
        customerHome=new FXMLLoader(AppController.class.getResource("../resources/views/CustomerHome.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((customerHome.load()));
        anchorRoot.getChildren().add(upperBar);
    }
    public void setSearchResults() throws IOException {
        searchResults=new FXMLLoader(AppController.class.getResource("../resources/views/searchResults.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((searchResults.load()));
        anchorRoot.getChildren().add(upperBar);
    }

    public void setEmplyeesAdmin() throws IOException {
        emplyeesLoder=new FXMLLoader(AppController.class.getResource("../resources/views/Employees.fxml"));
        anchorRoot.getChildren().remove(0,anchorRoot.getChildren().size());
        anchorRoot.getChildren().add((emplyeesLoder.load()));
        new FadeIn(anchorRoot.getChildren().get(0)).play();
        anchorRoot.getChildren().add(upperBar);
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scene =this.getShadowScene(anchorRoot);
        //upperBar handling
        exit.setOnAction(event -> Platform.exit());
        minimize.setOnAction(event -> root.setIconified(true));
        //Drag and Drop for moving the window
        upperBar.setOnMousePressed(pressEvent -> {
            upperBar.setOnMouseDragged(dragEvent -> {
                root.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
               root.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }
    public Scene getShadowScene(Parent p) {
        Scene scene;
        VBox outer = new VBox();
        outer.getChildren().add( p );
        outer.setPadding(new Insets(10.0d));
        outer.setBackground( new Background(new BackgroundFill( Color.rgb(0,0,0,0), new CornerRadii(0), new
                Insets(0))));

        p.setEffect(new DropShadow());
        ((AnchorPane)p).setBackground( new Background(new BackgroundFill( Color.WHITE, new CornerRadii(0), new Insets(0)
        )));

        scene = new Scene( outer );
        scene.setFill( Color.rgb(0,255,0,0) );
        return scene;
    }



}
