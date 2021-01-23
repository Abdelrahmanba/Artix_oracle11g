package main;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;


public class DeleteCustomerController implements Initializable {


    @FXML private JFXButton yes;
    @FXML private JFXButton no;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UsersAdminController cont = AppController.userAdmin.getController();
        yes.setOnAction(event -> {
            UsersAdminController.Users data = cont.table.getSelectionModel().getSelectedItem().getValue();
            String sql = String.format("DELETE from customer where usernamec='%s'", data.getUsername());
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                st.execute(sql);
                cont.delete.close();
                DialogMaker.sucsessDialog("The Customer was Deleted successfully..!",cont.stackPane);
                cont.loadcustomer();
                con.commit();
                con.close();

            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,cont.stackPane);            }
        });


        no.setOnAction(event -> cont.delete.close());

    }
}

