package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UpdateArtistController implements Initializable {

    @FXML
    JFXButton update;
    @FXML
    JFXTextField artistName;
    @FXML Label error;
    @FXML JFXTextArea bio;
    @FXML Label biofill,namefill;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArtistsAdminController cont2=AppController.artistAdmin.getController();
        ArtistsAdminController.Artist data=cont2.table.getSelectionModel().getSelectedItem().getValue();
        Utilite.addTextLimiter(artistName, 20);
        Utilite.addAlphabetsOnly(artistName);
        artistName.setText(data.getName().get());
        bio.setText(data.getBio().get());

        update.setOnAction(event -> {
            if(artistName.getText().isEmpty() ){
                namefill.setVisible(true);
                return;
            }
            else {
                namefill.setVisible(false);
            }
            if(bio.getText().isEmpty()){
                biofill.setVisible(true);
                return;
            }
            else
                biofill.setVisible(false);

                long id = Long.valueOf(cont2.table.getSelectionModel().getSelectedItem().getValue().getId());

            try {
                con = DriverManager.getConnection(url, "main","123456");
                String checkArtist="select artistname from artist where artistname='"+artistName.getText()+"' AND ARTISTNAME!='"+data.getName().get()+"'";
                st=con.createStatement();
                ResultSet chk=st.executeQuery(checkArtist);
                if(chk.next()){
                    error.setVisible(true);
                    return;
                }
                else error.setVisible(false);
                String sql="update artist set artistname=?,history=? where artistid=?";
                PreparedStatement ps=con.prepareStatement(sql);
                ps.setString(1,artistName.getText());
                ps.setString(2,bio.getText());
                ps.setLong(3,id);
                ps.executeUpdate();
                con.commit();
                con.close();
                DialogMaker.sucsessDialog("Artist Updated Successfully..",cont2.stackPane);
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,cont2.stackPane);
            }
                cont2.loadArtist();
                cont2.update.close();
        });





    }

}
