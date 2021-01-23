package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogController implements Initializable {

    @FXML
    JFXButton verify, cancel;
    @FXML
    Text text;
    @FXML
    JFXTextField OTP;
    @FXML
    Label invalid;
    @FXML JFXButton send;

    public int OTPvalue;
    RegisterController cont = AppController.registerLoad.getController();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        text.setText(String.format("To verify your email, we've sent a One Time Password (OTP) To \" %s\".", cont.email.getText()));
        Utilite.addTextLimiter(OTP, 6);
        Utilite.addNumericOnly(OTP);
        OTPvalue = SendMail.sendMail(cont.email.getText());
        cancel.setOnAction(event -> {
                    cont.email.setEditable(true);
                    cont.vaidation.close();
                }
        );

        verify.setOnAction(event -> {
            if(!OTP.getText().isEmpty()){
            if (Integer.valueOf(OTP.getText()) == OTPvalue) {
                cont.vaidation.close();
                cont.validEmail.setText("Verified!");
                cont.emailE.setVisible(false);
                cont.verifyEmail.setVisible(false);
            } else {
                invalid.setVisible(true);
                OTP.setText("");
            }

        }});


    }
}
