package main;

import animatefx.animation.SlideInRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class AdminDashboardController implements Initializable {

    @FXML JFXHamburger ham;
    HamburgerBasicCloseTransition burger;
    @FXML JFXDrawer drawer;
    @FXML JFXButton addPainting,orders;
    @FXML Label sold,shipped,made;
    @FXML ImageView hourse;
    @FXML StackPane stackPane;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new SlideInRight(hourse).play();

        burger =new HamburgerBasicCloseTransition(ham);
      burger.setRate(-1);
        try {
            drawer.setSidePane((AnchorPane)FXMLLoader.load(getClass().getResource("../resources/views/drawerContant.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        addPainting.setOnAction(event -> {
            try {
                Main.control.setSenceAddPainting();
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


        try{
            LocalDate date=LocalDate.now().minusDays(30);
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT count(*) " +
                    "FROM painting " +
                    "WHERE purchasedate >To_date('"+date.toString()+"','yyyy-mm-dd') ";

            //ResultSet
            ResultSet rs =st.executeQuery(SQL);
            if(rs.next()){
                int count=rs.getInt(1);
                if(count==1)
                sold.setText(count+" Painting were sold in last month.");
                else
                    sold.setText(count+" Paintings were sold in last month.");
            }
            con.close();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }

        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT count(*) " +
                    "FROM orders " +
                    "WHERE status='Paid' ";

            //ResultSet
            ResultSet rs =st.executeQuery(SQL);
            if(rs.next()){
                int count=rs.getInt(1);
                if(count==1)
                    shipped.setText(count+" Order is waiting to be shipped.");
                else
                    shipped.setText(count+" Orders are waiting to be shipped.");
            }
            con.close();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT totalprice FROM orders";
            ResultSet rs =st.executeQuery(SQL);
            Long count=Long.valueOf(0);
            while(rs.next()){
                count+=rs.getInt(1);
                    made.setText(count+" ILS Earned last month.");
            }
            con.close();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
    }
    @FXML
    public void hamEvent(){
        burger.setRate(burger.getRate()*-1);
        burger.play();
        if(drawer.isOpened())
            drawer.close();
        else
            drawer.open();
    }
}
