package main;

import animatefx.animation.FadeInDownBig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class ProfileController implements Initializable {

    @FXML private ImageView CustomerPicture;
    @FXML private Label Name;
    @FXML private Label username;
    @FXML private JFXTextField email;
    @FXML private JFXTextField phone;
    @FXML private MaterialDesignIconView HomeButton;
    @FXML private JFXButton update1;
    @FXML private JFXButton update2;
    @FXML private JFXButton update3;
    @FXML private JFXButton deleteB;
    @FXML public StackPane stackPane;
    JFXDialog update1D,update2D,update3D;
    String usernameString;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CustomerHomeController cont=AppController.customerHome.getController();
        usernameString=cont.username;
        username.setText("@"+usernameString);
        JFXDepthManager.setDepth(CustomerPicture,1);

        deleteB.setOnAction(event -> {
            JFXButton no,yes;
            yes=new JFXButton("    Yes    ");
            yes.setPrefHeight(40);
            yes.setStyle("-fx-text-fill: #0273a8");
            no=new JFXButton("     No    ");
            no.setStyle("-fx-text-fill: #0273a8");
            no.setPrefHeight(40);
            JFXDialog di=DialogMaker.confirmDialog(yes,no,"Delete Account","Are you sure you want to delete your account ?, this can't be undone.",stackPane);
            no.setOnAction(event1 -> {
                di.close();
                return;
            });
            yes.setOnAction(event2 -> {
                di.close();
                try {
                    con = DriverManager.getConnection(url, "main","123456");
                    st = con.createStatement();
                    String sql = String.format("DELETE from customer where usernamec='%s'",usernameString);
                    st.execute(sql);
                    con.commit();
                    con.close();
                    Main.control.setSceneLogin();
                } catch (SQLException | IOException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }
            });
        });

        update1.setOnAction(event -> {
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/update1.fxml"));
            Pane pane= null;
            try {
                pane = dialogLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
             update1D=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.CENTER,true);
            update1D.setContent(pane);
            update1D.show();


        });
        update2.setOnAction(event -> {
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/update2.fxml"));
            Pane pane= null;
            try {
                pane = dialogLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            update2D=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.CENTER,true);
            update2D.setContent(pane);
            update2D.show();
        });
        update3.setOnAction(event -> {
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/update3.fxml"));
            Pane pane= null;
            try {
                pane = dialogLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            update3D=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.CENTER,true);
            update3D.setContent(pane);
            update3D.show();
        });


        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL1 = "SELECT profilepicurl,firstname,lastname,phonenumber,email from customer where usernamec='"+usernameString+"'";
            ResultSet rs1 =st.executeQuery(SQL1);
            while(rs1.next()){
                String url=rs1.getString("profilepicurl");
                if(url!=null)
                    CustomerPicture.setImage(new Image("file:///"+url));
                String phoneN=rs1.getString("phonenumber");
                if(phoneN!=null)
                    phone.setText("+"+phoneN);
                else
                    phone.setText("Not Added");
                Name.setText(rs1.getString(2)+" " +rs1.getString(3));
                email.setText(rs1.getString(5));
            }
            con.close();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }

        HomeButton.setOnMouseClicked(event -> {
            FadeInDownBig z=new FadeInDownBig(cont.stackPane.getChildren().get(1));
            z.play();
            z.setOnFinished(event1 ->   cont.stackPane.getChildren().remove(0));
     });

    }


}
