package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Update1Controller implements Initializable {

    @FXML private JFXButton update;
    @FXML private JFXTextField firstName;
    @FXML private JFXTextField lastName;
    @FXML private JFXTextField email;
    @FXML private JFXTextField phoneNo;
    @FXML private JFXPasswordField password;
    @FXML private JFXDatePicker birthDate;
    @FXML Label nameE;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CustomerHomeController contHome=AppController.customerHome.getController();

        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT firstname,lastname,email,phonenumber,bdate from customer where usernamec='"+contHome.username+"'";
            ResultSet rs1 =st.executeQuery(SQL);
            while(rs1.next()){
                phoneNo.setText(String.valueOf(rs1.getLong(4)));
               firstName.setText(rs1.getString(1));
               lastName.setText(rs1.getString(2));
               email.setText(rs1.getString(3));
               birthDate.setValue(rs1.getDate(5).toLocalDate());
            }
            con.close();

            update.setOnAction(event -> {
                ProfileController cont = contHome.profileLoader.getController();
                try {
                    if(email.getText().isEmpty()||firstName.getText().isEmpty()||lastName.getText().isEmpty()||password.getText().isEmpty()) {
                        nameE.setVisible(true);
                        return;
                    }
                    else {

                        con = DriverManager.getConnection(url, "main", "123456");
                        st = con.createStatement();
                        String number =phoneNo.getText();
                        String sql = "Update customer set email=?, pass=?,phonenumber=?,bdate=To_Date('" + birthDate.getValue().toString() + "','yyyy-mm-dd'),firstname=?,lastname=? where usernamec='" + contHome.username + "'";
                        PreparedStatement st = null;
                        st = con.prepareStatement(sql);
                        st.setString(1, email.getText());
                        st.setString(2, password.getText());
                        if (!phoneNo.getText().isEmpty())
                            st.setString(3, number);
                        else
                            st.setNull(3, Types.BIGINT);
                        st.setString(4, firstName.getText());
                        st.setString(5, lastName.getText());
                        st.executeUpdate();
                        con.commit();
                        con.close();
                        cont.update1D.close();
                        DialogMaker.sucsessDialog("Updated sucsessfully..",cont.stackPane);
                    }} catch (SQLException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,cont.stackPane);
                }

            });

        }catch(Exception e){
            e.printStackTrace();
        }


        Utilite.addTextLimiter(firstName,16);
        Utilite.addAlphabetsOnly(firstName);
        Utilite.addTextLimiter(lastName,16);
        Utilite.addAlphabetsOnly(lastName);
        Utilite.addTextLimiter(email,50);
        Utilite.addTextLimiter(phoneNo,14);
        Utilite.addNumericOnly(phoneNo);


    }
}
