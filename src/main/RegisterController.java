package main;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
@FXML public JFXComboBox<String> countryCode,contryBox;
@FXML public JFXButton signup;
@FXML public JFXTextField firstName,lastName,username,email,state,city,street,phoneNo;
@FXML public JFXPasswordField password,passwordRep;
@FXML public JFXDatePicker birthDate;
@FXML public Text userNameHint;
@FXML public Label validEmail,notMatch;
@FXML public Hyperlink verifyEmail;
@FXML public MaterialDesignIconView validEmailIcon;
@FXML public StackPane stackPane;
@FXML public Label emailE,usernameE,nameE,addressE;
boolean nextUsername =false;
public JFXDialog vaidation;
public BorderPane signupPane;
private Connection con;
String url="jdbc:oracle:thin:@localhost:1521:orcl";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(int i=0;i<Countrys.countryNames.length;i++){
            countryCode.getItems().add(Countrys.countryNames[i]+" ( +"+Countrys.countryAreaCodes[i]+" )" );
            countryCode.getSelectionModel().selectFirst();
            contryBox.getItems().add(Countrys.countryNames[i]);
        }
        birthDate.setValue(LocalDate.parse("2000-01-01"));
        contryBox.setValue("Palestine");
        Utilite.addTextLimiter(firstName,16);
        Utilite.addAlphabetsOnly(firstName);
        Utilite.addTextLimiter(lastName,16);
        Utilite.addAlphabetsOnly(lastName);
        Utilite.addTextLimiter(email,50);
        Utilite.hintfocuse(email);
        Utilite.addTextLimiter(phoneNo,9);
        Utilite.addNumericOnly(phoneNo);
        Utilite.hintfocuse(username);
        Utilite.addTextLimiter(username,16);
        Utilite.addPasswordLimiter(password,20);
        Utilite.addPasswordLimiter(passwordRep,20);
        Utilite.addTextLimiter(state,16);
        Utilite.addAlphabetsOnly(state);
        Utilite.addTextLimiter(city,16);
        Utilite.addAlphabetsOnly(city);
        Utilite.addTextLimiter(street,25);

    }

    @FXML
    public void verify() throws IOException {
        email.setEditable(false);
        FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/dialog.fxml"));
        Pane pane= dialogLoader.load();
        vaidation=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,false);
        vaidation.setContent(pane);
        vaidation.show();

    }

    @FXML
    public void signupNext() throws IOException {
        boolean nextM=true,nextN=true,nextE=true,nextU=true,nextA=true,nextP,nextV=false;

        if(!password.getText().equals(passwordRep.getText())){
            notMatch.setText("Password didn't match!");
            notMatch.setVisible(true);
            nextM=false;
        }
        else{
            notMatch.setVisible(false);
            nextM=true;
        }
        if(email.getText().isEmpty()) {
            emailE.setText("Email can't be Empty");
            emailE.setVisible(true);
            nextE = false;
        }
        else{
            emailE.setVisible(false);
            nextE=true;
        }
        if(validEmail.getText().equals("Verified!")){
            nextV=true;
            emailE.setVisible(false);
        }
        else{
            nextV=false;
            emailE.setText("Please Verify Your email");
            emailE.setVisible(true);
        }

        if(firstName.getText().isEmpty()||lastName.getText().isEmpty()) {
            nameE.setVisible(true);
            nextN = false;
        }
        else{
            nameE.setVisible(false);
            nextN=true;
        }

        if(username.getText().isEmpty()) {
            usernameE.setText("Username can't be Empty");
            usernameE.setVisible(true);
            nextU = false;
        }
        else {
            if(nextUsername)
            usernameE.setVisible(false);
            nextU = true;
        }
            if(state.getText().isEmpty()||city.getText().isEmpty()||street.getText().isEmpty()){
            addressE.setVisible(true);
            nextA=false;
        }
        else {
            addressE.setVisible(false);
            nextA=true;
        }
        if(password.getText().isEmpty()) {
            notMatch.setText("Password Can't be Empty");
            notMatch.setVisible(true);
            nextP=false;
        }
        else {
            if(nextM)
            notMatch.setVisible(false);
            nextP=true;

        }
        if(nextP&&nextA&&nextE&&nextM&&nextN&&nextU&&nextUsername&nextV){
            try {
                con = DriverManager.getConnection(url, "main","123456");
                String sql;
                    String number = "00" + Countrys.countryAreaCodes[countryCode.getSelectionModel().getSelectedIndex()] + phoneNo.getText();
                    sql = "insert into customer (usernameC, email, pass,phonenumber,bdate,firstname,lastname,country,state,city,street) values (?,?,?,?,To_Date('"+birthDate.getValue().toString()+"','yyyy-mm-dd'),?,?,?,?,?,?)";
                    PreparedStatement st=con.prepareStatement(sql);
                    st.setString(1,username.getText());
                    st.setString(2,email.getText());
                    st.setString(3,password.getText());
                    if (!phoneNo.getText().isEmpty())
                        st.setString(4,number);
                    else
                        st.setNull(4,Types.BIGINT);
                    st.setString(5, firstName.getText());
                    st.setString(6, lastName.getText());
                    st.setString(7, contryBox.getValue());
                    st.setString(8,state.getText());
                    st.setString(9, city.getText());
                    st.setString(10, street.getText());
                st.executeUpdate();
                String sql2 = String.format("insert into shopcart (cartid, username,checkedout) values (cartseq.nextval,'%s',0)", username.getText());
                Statement st2=con.createStatement();
                st2.executeQuery(sql2);
                con.commit();
                con.close();
                RegisterNext.username = username.getText();
                Main.control.setSenceRegisterNext();
            }catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);            }

        }
    }
    @FXML
    public void backToSignIn() throws IOException {
        Main.control.setSceneLogin();
    }




}
