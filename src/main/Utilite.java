package main;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;



public class Utilite {
    private  static Connection con;
    private  static Statement st;
    static String  url="jdbc:oracle:thin:@localhost:1521:orcl";
    public static void isValidEmailAddress(String email) throws SQLException {
        RegisterController cont=AppController.registerLoad.getController();


        if(email.isEmpty()){
            cont.validEmail.setVisible(false);
            cont.verifyEmail.setVisible(false);
            return;
        }
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        if(m.matches()&&!(cont.validEmail.getText().equals("Verified!"))){
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String sql=String.format("SELECT email  FROM customer where email ='%s'",cont.email.getText());
            ResultSet rs=st.executeQuery(sql);
            boolean found=false;
            while(rs.next()){
                if(rs.getString("email").equals(cont.email.getText())){
                    found=true;
                    break;
                }
            }
            con.close();
            if(found) {
                cont.validEmail.setText("This Email is already Registered.");
                cont.validEmail.setTextFill(Color.RED);
                cont.validEmailIcon.setGlyphName("ALERT_CIRCLE_OUTLINE");
                cont.validEmailIcon.setFill(Paint.valueOf("#e70d0d"));
                cont.validEmail.setVisible(true);
                cont.verifyEmail.setVisible(false);
            }
            else {
                cont.validEmail.setText("This Email is Not Registered,");
                cont.validEmail.setTextFill(new Color(31.0 / 255, 158.0 / 255, 36.0 / 255, 1));
                cont.validEmailIcon.setGlyphName("CHECKBOX_MARKED_CIRCLE_OUTLINE");
                cont.validEmailIcon.setFill(Paint.valueOf("#1f9e24"));
                cont.validEmail.setVisible(true);
                cont.verifyEmail.setVisible(true);
            }

        }
        else {
            if(!cont.validEmail.getText().equals("Verified!"))
            {
            cont.validEmail.setText("This is Not a valid Email format.");
            cont.validEmail.setTextFill(Color.RED);
            cont.validEmailIcon.setGlyphName("ALERT_CIRCLE_OUTLINE");
            cont.validEmailIcon.setFill(Paint.valueOf("#e70d0d"));
            cont.validEmail.setVisible(true);
            cont.verifyEmail.setVisible(false);
        }
        }
    }

    public static void addNumericOnly(JFXTextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
    public static void addNumericOnlyWithDot(JFXTextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    textField.setText(oldValue);
                }
            }
        });
    }

    public static void addAlphabetsOnly(JFXTextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("a-zA-Z*")) {
                    textField.setText(newValue.replaceAll("[^a-zA-Z ]", ""));
                }
            }
        });
    }

    public static void addTextLimiter(final JFXTextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }
    public static void addPasswordLimiter(final JFXPasswordField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

    public static void hintfocuse(JFXTextField textField) {
        RegisterController cont=AppController.registerLoad.getController();
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    if(textField.getPromptText().equals("Username"))
                    cont.userNameHint.setVisible(true);
                }
                else {
                    if(textField.getPromptText().equals("Username")) {
                        String sql = String.format("SELECT usernamec,usernamee FROM customer,employee where usernamec ='%s'or usernamee='%s'", cont.username.getText(), cont.username.getText());
                        ResultSet rs = null;
                        try {
                            con = DriverManager.getConnection(url, "main","123456");
                            st = con.createStatement();
                            rs = st.executeQuery(sql);

                            boolean found = false;
                            if (rs.next()) {
                                cont.usernameE.setText("This Username is already taken!");
                                cont.usernameE.setVisible(true);
                                cont.nextUsername = false;
                            } else {
                                cont.usernameE.setVisible(false);
                                cont.nextUsername = true;

                            }
                            cont.userNameHint.setVisible(false);
                            con.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    if(textField.getPromptText().equals("Email Address")) {
                        try {
                            Utilite.isValidEmailAddress(textField.getText());
                        } catch (SQLException throwables) {
                            StringWriter sw = new StringWriter();
                            throwables.printStackTrace(new PrintWriter(sw));
                            String exception = sw.toString();
                            DialogMaker.errorDialog(exception,cont.stackPane);                        }
                    }


                }
            }
        });
    }

    }







