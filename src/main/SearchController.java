package main;

import animatefx.animation.BounceInRight;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML private MaterialDesignIconView back;
    @FXML public VBox paintingV;
    @FXML public VBox artistV;
    @FXML public Text label;
    @FXML Label noresultsA,noresultsP;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        back.setOnMouseClicked(event -> {
            CustomerHomeController cont=AppController.customerHome.getController();
            BounceInRight r = new BounceInRight(cont.stackPane.getChildren().get(1));
            r.setOnFinished(event1 ->  cont.stackPane.getChildren().remove(0));
            r.play();


        });




    }



}

