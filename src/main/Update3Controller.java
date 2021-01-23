package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Update3Controller implements Initializable {


    @FXML private JFXButton update;
    @FXML private Label nameE;
    @FXML private JFXTextField cardName;
    @FXML private JFXTextField cardNo;
    @FXML private JFXTextField expM;
    @FXML private JFXTextField expY;
    @FXML private JFXTextField cvv;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CustomerHomeController contHome=AppController.customerHome.getController();
        ProfileController cont = contHome.profileLoader.getController();

            update.setOnAction(event -> {
                try {
                    if(cvv.getText().isEmpty()||cardNo.getText().isEmpty()||cardName.getText().isEmpty()||expY.getText().isEmpty()||expM.getText().isEmpty()) {
                        nameE.setVisible(true);
                        return;
                    }
                    else {
                        con = DriverManager.getConnection(url, "main", "123456");
                        st = con.createStatement();
                        String sql = "Update customer set ccv=?, ccnumber=?,ccholdername=?,ccexpyear=?,ccexpmonth=? where usernamec='" + contHome.username + "'";
                        PreparedStatement st = null;
                        st = con.prepareStatement(sql);
                        st.setInt(1, Integer.parseInt(cvv.getText()));
                        st.setString(3, cardName.getText());
                        st.setLong(2 , Long.parseLong(cardNo.getText()));
                        st.setInt(4, Integer.parseInt(expY.getText()));
                        st.setInt(5, Integer.parseInt(expM.getText()));
                        st.executeUpdate();
                        con.commit();
                        con.close();
                        cont.update3D.close();
                        DialogMaker.sucsessDialog("Updated sucsessfully..",cont.stackPane);
                    }} catch (SQLException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,cont.stackPane);
                }

            });


        Utilite.addTextLimiter(cardName,20);
        Utilite.addAlphabetsOnly(cardName);
        Utilite.addTextLimiter(cvv,3);
        Utilite.addTextLimiter(expM,2);
        Utilite.addTextLimiter(expY,2);
        Utilite.addTextLimiter(cardNo,16);
        Utilite.addNumericOnly(expM);
        Utilite.addNumericOnly(expY);
        Utilite.addNumericOnly(cvv);
        Utilite.addNumericOnly(cardNo);


    }
}
