package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Update2Controller implements Initializable {


    @FXML private JFXButton update;
    @FXML private Label biofill;
    @FXML private Label nameE;
    @FXML private JFXComboBox<String> contryBox;
    @FXML private JFXTextField state;
    @FXML private JFXTextField city;
    @FXML private JFXTextField street;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CustomerHomeController contHome=AppController.customerHome.getController();
        for(int i=0;i<Countrys.countryNames.length;i++){
            contryBox.getItems().add(Countrys.countryNames[i]);
        }
        ProfileController cont = contHome.profileLoader.getController();

        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT country,state,city,street from customer where usernamec='"+contHome.username+"'";
            ResultSet rs1 =st.executeQuery(SQL);
            while(rs1.next()){
                contryBox.setValue(rs1.getString(1));
                state.setText(rs1.getString(2));
                city.setText(rs1.getString(3));
                street.setText(rs1.getString(4));
            }
            con.close();

            update.setOnAction(event -> {
                try {
                    if(city.getText().isEmpty()||street.getText().isEmpty()||state.getText().isEmpty()) {
                        nameE.setVisible(true);
                        return;
                    }
                    else {
                        con = DriverManager.getConnection(url, "main", "123456");
                        st = con.createStatement();
                        String sql = "Update customer set country=?, state=?,city=?,street=? where usernamec='" + contHome.username + "'";
                        PreparedStatement st = null;
                        st = con.prepareStatement(sql);
                        st.setString(1, contryBox.getValue());
                        st.setString(2, state.getText());
                        st.setString(3, city.getText());
                        st.setString(4, street.getText());
                        st.executeUpdate();
                        con.commit();
                        con.close();
                        cont.update2D.close();
                        DialogMaker.sucsessDialog("Updated sucsessfully..",cont.stackPane);
                    }} catch (SQLException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,cont.stackPane);
                }

            });


            Utilite.addTextLimiter(state,16);
            Utilite.addAlphabetsOnly(state);
            Utilite.addTextLimiter(city,16);
            Utilite.addAlphabetsOnly(city);
            Utilite.addTextLimiter(street,25);


        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont.stackPane);        }




    }
}
