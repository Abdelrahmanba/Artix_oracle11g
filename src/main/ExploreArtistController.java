
package main;

import com.jfoenix.controls.JFXButton;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class ExploreArtistController implements Initializable {

    @FXML JFXTextArea bio;
    @FXML Label name;
    @FXML private TableColumn<Painting, String> nameP;
    @FXML private TableView<Painting> table;
    @FXML private TableColumn<Painting, ImageView> image;
    @FXML JFXButton x;
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
        ArtistsAdminController cont2 = AppController.artistAdmin.getController();
        ArtistsAdminController.Artist data = cont2.table.getSelectionModel().getSelectedItem().getValue();

        name.setText(data.getName().get());
        bio.setText(data.getBio().get());
        image.setCellValueFactory(new PropertyValueFactory<Painting,ImageView>("photo"));
        nameP.setCellValueFactory(new PropertyValueFactory<Painting,String>("paintingName"));


        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL1 = String.format("SELECT picurl from artist where Artistid=%d",Integer.valueOf(data.getId()));
            //ResultSet
            ResultSet rs1 =st.executeQuery(SQL1);
            while(rs1.next()){
                imageArtist.setImage(new Image("file:///"+rs1.getString("picurl")));

            }
            String SQL = String.format("SELECT name,imageurl from painting where Artid=%d",Integer.valueOf(data.getId()));
            //ResultSet
            ResultSet rs =st.executeQuery(SQL);
            while(rs.next()){
                Painting info=new Painting();
                info.setPaintingName(rs.getString("name"));
                ImageView test=new ImageView();
                test.setImage(new Image("file:///"+rs.getString("imageurl")));
                test.setFitWidth(217);
                test.setPreserveRatio(true);
                test.setSmooth(true);
                test.setCache(true);
                info.setPhoto(test);
                paintings.add(info);
            }
            table.setItems(paintings);
            con.close();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont2.stackPane);
        }
        x.setOnAction(event -> cont2.explore.close());
    }




}




