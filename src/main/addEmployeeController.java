package main;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ResourceBundle;


public class addEmployeeController implements Initializable {

    @FXML private StackPane stackPane;
    @FXML private JFXTextField firstName;
    @FXML private JFXTextField lastName;
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private JFXTextField position;
    @FXML private JFXTextField salary;
    @FXML private JFXTextField bankName;
    @FXML private JFXTextField iban;
    @FXML private JFXTextField cvText;
    @FXML private JFXButton add;
    @FXML private JFXButton loadCV;
    @FXML private Label error;
    @FXML private Label usernameFill;
    @FXML private Label salaryFill;
    @FXML private Label cvFill;
    @FXML private JFXCheckBox access;
    @FXML private Label positionFill;
    @FXML private Label passFill;
    @FXML private Label nameFill;
    public FileChooser chooser;
    public FileChooser.ExtensionFilter filter;
    private File cv, jarDir;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utilite.addTextLimiter(username,16);
        Utilite.addTextLimiter(position,16);
        Utilite.addTextLimiter(firstName,16);
        Utilite.addTextLimiter(lastName,16);
        Utilite.addTextLimiter(salary,8);
        Utilite.addNumericOnly(salary);
        Utilite.addTextLimiter(iban,34);
        Utilite.addTextLimiter(bankName,16);
        Utilite.addPasswordLimiter(password,20);
        EmployeesController cont=AppController.emplyeesLoder.getController();

        add.setOnAction(event -> {
            if (firstName.getText().isEmpty()||lastName.getText().isEmpty()) {
                nameFill.setVisible(true);
                return;
            } else {
                nameFill.setVisible(false);
                    }
            if (username.getText().isEmpty()) {
                usernameFill.setVisible(true);
                return;
            } else {
                usernameFill.setVisible(false);
            }
            if (cvText.getText().isEmpty()) {
                cvFill.setVisible(true);
                return;
            } else {
                cvFill.setVisible(false);
            }
            if (position.getText().isEmpty()) {
                positionFill.setVisible(true);
                return;
            } else {
                positionFill.setVisible(false);
            }

            if (salary.getText().isEmpty()) {
                salaryFill.setVisible(true);
                return;
            } else {
                salaryFill.setVisible(false);
            }
            if (password.getText().isEmpty()) {
                passFill.setVisible(true);
                return;
            } else
                passFill.setVisible(false);
            try {

                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String sql = String.format("SELECT usernamee,usernamec  FROM employee,customer where usernamee ='%s' or usernamec ='%s'", username.getText(),username.getText());
                ResultSet rs = st.executeQuery(sql);
                if (rs.next()) {
                    error.setVisible(true);
                    return;
                } else {
                    error.setVisible(false);

                }

                String name=String.format("%s.pdf",username.getText());
                File f= new File(jarDir,name);
                try {
                    Files.copy(Paths.get(cv.toString()),Paths.get(f.toString()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }
                if(access.isSelected())
                    sql = "insert into employee (usernamee, pass, position,firstname,lastname,salary,cvurl,iban,bankname,admin) values ('"+username.getText()+"','"+password.getText()+"','"+position.getText()+"','"+firstName.getText()+"','"+lastName.getText()+"',"+Double.valueOf(salary.getText())+",'"+f.toString()+"','"+iban.getText()+"','"+bankName.getText()+"',1)";
                else{
                    sql = "insert into employee (usernamee, pass, position,firstname,lastname,salary,cvurl,iban,bankname,admin) values ('"+username.getText()+"','"+password.getText()+"','"+position.getText()+"','"+firstName.getText()+"','"+lastName.getText()+"',"+Double.valueOf(salary.getText())+",'"+f.toString()+"','"+iban.getText()+"','"+bankName.getText()+"',0)";
                }
                st.executeUpdate(sql);
                con.commit();
                con.close();
                cont.loadEmployee();
                cont.addDialog.close();

            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }

            }
            );

        loadCV.setOnAction(event -> {
                chooser = new FileChooser();
                filter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
                chooser.getExtensionFilters().add(filter);
                cv = chooser.showOpenDialog(stackPane.getScene().getWindow());
                cvText.setText(cv.toString());

                if (cv != null) {
                    jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "CVs");
                    jarDir.mkdir();

                }


        });

    }
}
