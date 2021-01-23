package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ResourceBundle;


public class addArtistController implements Initializable {

    @FXML JFXButton add;
    @FXML JFXTextField artistName;
    @FXML Label error;
    @FXML JFXTextArea bio;
    @FXML Label biofill, namefill,picfill;
    @FXML StackPane stackPane;
    @FXML ImageView image;
    public FileChooser chooser;
    public FileChooser.ExtensionFilter filter;
    private File photo, jarDir;
    BufferedImage bi;
    private  Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Utilite.addTextLimiter(artistName, 20);
        Utilite.addAlphabetsOnly(artistName);


        add.setOnAction(event -> {
            if (artistName.getText().isEmpty()) {
                namefill.setVisible(true);
                return;
            } else {
                namefill.setVisible(false);
            }
            if (photo==null) {
                picfill.setVisible(true);
                return;
            } else {
                namefill.setVisible(false);
            }
            if (bio.getText().isEmpty()) {
                biofill.setVisible(true);
                return;
            } else
                biofill.setVisible(false);
            ResultSet rs = null;
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String sql = "SELECT artistname  FROM artist where artistname ='"+artistName.getText()+"'";
                rs = st.executeQuery(sql);
                if (rs.next()) {
                    error.setVisible(true);
                    return;
                } else
                    error.setVisible(false);

                String sql3 = "select artistSEQ.nextval from DUAL";
                Statement ps = con.createStatement();
                long nextID=0;
                ResultSet rs3 = ps.executeQuery(sql3);
               if(rs3.next())
                    nextID = rs3.getLong(1);
                String name=String.format("%s%s",Long.toString(nextID), photo.toString().substring(photo.toString().lastIndexOf(".")));
                File f= new File(jarDir,name);
                try {
                    Files.copy(Paths.get(photo.toString()),Paths.get(f.toString()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sql ="insert into artist (artistid, artistname, history,picurl) values (?,?,?,?)";
                PreparedStatement ps1=con.prepareStatement(sql);
                ps1.setLong(1,nextID);
                ps1.setString(2,artistName.getText());
                ps1.setString(3,bio.getText());
                ps1.setString(4,f.toString());

                ps1.executeUpdate();

            if (AppController.addPaintingLoader != null) {
                NewPaintingController cont = AppController.addPaintingLoader.getController();
                cont.loadArtist();
                cont.artist.setValue(artistName.getText());
                if (cont.addnewartist != null)
                    cont.addnewartist.close();

            }
            if (AppController.artistAdmin != null) {
                ArtistsAdminController cont2 = AppController.artistAdmin.getController();
                cont2.loadArtist();
                if (cont2.addnewartist != null) {
                    cont2.addnewartist.close();
                    DialogMaker.sucsessDialog("The Artist was added Successfuly..",cont2.stackPane);
                }
            }
            con.commit();
            con.close();
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }

        });
    }

    @FXML
    void browser() throws IOException, URISyntaxException {
        chooser = new FileChooser();
        filter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png");
        chooser.getExtensionFilters().add(filter);
        photo = chooser.showOpenDialog(stackPane.getScene().getWindow());

        if (photo != null) {
            jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "ArtistsPics");
            jarDir.mkdir();
            bi = ImageIO.read(new URL("file:///" + photo.toString()));
            if (bi != null) {
                image.setImage(SwingFXUtils.toFXImage(bi, null));
            }
        }

    }

}


