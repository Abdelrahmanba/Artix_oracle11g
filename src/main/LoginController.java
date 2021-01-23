package main;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;

public class LoginController {
    public Stage stage;
    public Hyperlink register;
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private Label error;
    @FXML private StackPane stackPane;

    private  Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    public void setStage(Stage stage){
        this.stage=stage;
    }

    @FXML
    //register button
    public void register() throws IOException {
        Main.control.setSenceRegister();
    }


    //login button
    @FXML
    public void login() throws IOException {
        if(username.getText().isEmpty()){
            error.setText("Please enter Your username first.");
            error.setVisible(true);
            return;
        }
        else {
            error.setVisible(false);
        }
        if(password.getText().isEmpty()){
            error.setText("Please enter Your password first.");
            error.setVisible(true);
            return;
        }
        else {
            error.setVisible(false);
        }
        //seraching for customers
        try {

            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQLcustomer="SELECT PASS FROM CUSTOMER WHERE USERNAMEC='"+username.getText()+"'";
            ResultSet rs=st.executeQuery(SQLcustomer);
            if(rs.next()){
                if(password.getText().equals(rs.getString(1))){
                    Main.control.setCustomerHome();
                    CustomerHomeController contHome=AppController.customerHome.getController();
                    contHome.username=username.getText();
                }
                else{
                    error.setText("Wrong Password.");
                    error.setVisible(true);
                }
            }
            else{
                String SQLadmin="SELECT PASS FROM EMPLOYEE WHERE USERNAMEE='"+username.getText()+"' AND ADMIN=1";
                ResultSet rs2=st.executeQuery(SQLadmin);
                if(rs2.next()) {
                    if(password.getText().equals(rs2.getString(1)))
                    Main.control.setSenceAdminMain();
                    else{
                        error.setText("Wrong Password.");
                        error.setVisible(true);
                        return;
                    }
                }
                error.setText("User not found.");
                error.setVisible(true);
            }
            con.close();
        }catch (SQLException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }



    }

}
