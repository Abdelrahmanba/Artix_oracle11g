
package main;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class ExploreEmployeeController implements Initializable {

        @FXML private Label name;
        @FXML private JFXButton x;
        @FXML private Label username;
        @FXML private Label position;
        @FXML private Label salary;
        @FXML private Label bankName;
        @FXML private Label iban;
        @FXML private Label isAdmin;
        @FXML private JFXButton cv;
        String CVUrl;
        private Connection con;
        private Statement st;
        String url="jdbc:oracle:thin:@localhost:1521:orcl";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EmployeesController cont2 = AppController.emplyeesLoder.getController();
        EmployeesController.Employee data = cont2.table.getSelectionModel().getSelectedItem().getValue();

        name.setText(data.getName());
        position.setText(data.getPosition());
        username.setText(data.getUsername());

        try{

            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL1 = "SELECT cvurl,salary,iban,bankname,admin from employee where usernamee='"+data.getUsername()+"'";
            //ResultSet
            ResultSet rs1 =st.executeQuery(SQL1);
            while(rs1.next()){
                 salary.setText(rs1.getString("salary"));
                CVUrl=rs1.getString("cvurl");
                bankName.setText(rs1.getString("bankname"));
                iban.setText(rs1.getString("iban"));
                if(rs1.getInt("admin")==1){
                    isAdmin.setText("Yes");
                }
                else{
                    isAdmin.setText("NO");
                }
            }
            con.close();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont2.stackPane);
        }

        cv.setOnAction(event -> {
            Desktop desktop = Desktop.getDesktop();
            File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "CVs");
            File file =new File(jarDir,""+data.getUsername()+""+".pdf");
            if(file.exists()){
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        x.setOnAction(event -> cont2.explore.close());


    }




}




