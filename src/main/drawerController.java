package main;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class drawerController implements Initializable {
    @FXML
    JFXButton dashboard,inventory,users,reports,orders,artists,signout,employee;
    public static StackPane stackPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signout.setOnAction(event -> {
            try {
                Main.control.setSceneLogin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dashboard.setOnAction(event -> {
            try {
                Main.control.setSenceAdminMain();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        artists.setOnAction(event -> {
            try {
                Main.control.setSenceArtistsAdmin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        users.setOnAction(event -> {
            try {
                Main.control.setSenceUserssAdmin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        inventory.setOnAction(event -> {
                    try {
                        Main.control.setInventoryAdmin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                );
        reports.setOnAction(event -> {
            try {
                Main.control.setReportsAdmin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        orders.setOnAction(event -> {
            try {
                Main.control.setOrdersAdmin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        employee.setOnAction(event -> {
            try {
                Main.control.setEmplyeesAdmin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
