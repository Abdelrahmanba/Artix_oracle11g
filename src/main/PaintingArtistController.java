
package main;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class PaintingArtistController implements Initializable {

    @FXML JFXTextArea bio;
    @FXML Label name;
    @FXML private TableColumn<Painting, String> nameP;
    @FXML private TableView<Painting> table;
    @FXML private TableColumn<Painting, ImageView> image;
    @FXML ImageView imageArtist;
    ObservableList<Painting> paintings= FXCollections.observableArrayList();
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

    public class Painting extends RecursiveTreeObject<ExploreArtistController.Painting> {
        public ImageView photo;
        public StringProperty paintingName;

        public ImageView getPhoto() {
            return photo;
        }
        public void setPhoto(ImageView photo) {
            this.photo = photo;
        }

        public String getPaintingName() {
            return paintingName.get();
        }
        public void setPaintingName(String paintingName) {
            this.paintingName=new SimpleStringProperty(paintingName);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        image.setCellValueFactory(new PropertyValueFactory<Painting,ImageView>("photo"));
        nameP.setCellValueFactory(new PropertyValueFactory<Painting,String>("paintingName"));

    }

    public void loadArtist (String artistId) {
        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            Long artId=Long.valueOf(artistId);
            String SQL = String.format("SELECT artistname,history,picurl,name,imageurl from artist,painting where Artistid=%d and artid=artistid",artId );
            //ResultSet
            ResultSet rs = st.executeQuery(SQL);
            while (rs.next()) {
                name.setText(rs.getString(1));
                imageArtist.setImage(new Image("file:///" + rs.getString("picurl")));
                Painting info = new Painting();
                info.setPaintingName(rs.getString("name"));
                ImageView test = new ImageView();
                test.setImage(new Image("file:///" + rs.getString("imageurl")));
                test.setFitWidth(217);
                test.setPreserveRatio(true);
                test.setSmooth(true);
                test.setCache(true);
                info.setPhoto(test);
                paintings.add(info);
            }
            table.setItems(paintings);
            con.close();
        } catch (Exception e) {
           e.printStackTrace();
        }

    }


}




