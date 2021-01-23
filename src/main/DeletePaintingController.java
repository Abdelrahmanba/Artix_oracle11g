package main;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class DeletePaintingController implements Initializable {

    @FXML
    private JFXButton yes;

    @FXML
    private JFXButton no;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InventoryController cont=AppController.InventroyAdmin.getController();
        UpdatePaintingController contupdate= cont.updateLoader.getController();

        yes.setOnAction(event ->{
            contupdate.confirmDelete=true;
            contupdate.confirm.close();
        });
        no.setOnAction(event -> {
            contupdate.confirmDelete=false;
            contupdate.confirm.close();
        });

    }
}
