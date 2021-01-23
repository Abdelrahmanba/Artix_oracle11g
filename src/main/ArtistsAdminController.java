package main;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.function.Predicate;


public class ArtistsAdminController implements Initializable {

    @FXML JFXHamburger ham;
    HamburgerBasicCloseTransition burger;
    @FXML JFXDrawer drawer;
    @FXML JFXTreeTableView<Artist> table;
    @FXML StackPane stackPane;
    JFXDialog addnewartist,update,delete,explore;
    ObservableList<Artist>artists;
    @FXML JFXButton updateB, exploreB;
    @FXML JFXTextField filter;
    private Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";



    class Artist extends RecursiveTreeObject<Artist>{
        StringProperty name;
        StringProperty bio;
        StringProperty noOfPaintings;
        String id;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        public Artist(String name1, String bio, String id) {
            this.name = new SimpleStringProperty(name1);
            this.bio = new SimpleStringProperty(bio);
            this.noOfPaintings = new SimpleStringProperty(Integer.valueOf(id).toString());
        }
        public Artist(){}
        public void setName(String name) {
            this.name=new SimpleStringProperty(name);
        }
        public StringProperty getName() {
            return name;
        }

        public void setBio(String bio) { this.bio=new SimpleStringProperty(bio);; }
        public StringProperty getBio() { return bio; }

        public void setNoOfPaintings(String noOfPaintings) { this.noOfPaintings =new SimpleStringProperty(Integer.valueOf(noOfPaintings).toString()); }
        public StringProperty getNoOfPaintings() {
            return noOfPaintings;
        }


    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //hamburger and side panel
        burger =new HamburgerBasicCloseTransition(ham);
        burger.setRate(-1);

        try {
            drawer.setSidePane((AnchorPane) FXMLLoader.load(getClass().getResource("../resources/views/drawerContant.fxml")));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateB.setDisable(false);
                exploreB.setDisable(false);
            }
            else{
                updateB.setDisable(true);
                exploreB.setDisable(true);
            }
        });
        //table
        JFXTreeTableColumn<Artist,String> name=new JFXTreeTableColumn<>("Name");
        name.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Artist, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Artist, String> param) {
                return param.getValue().getValue().name;
            }
        });

        JFXTreeTableColumn<Artist,String> bio=new JFXTreeTableColumn<>("Bio");
        bio.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Artist, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Artist, String> param) {
                return param.getValue().getValue().bio;
            }
        });

        JFXTreeTableColumn<Artist,String> noPaintings=new JFXTreeTableColumn<>("No. of Paintings");
        noPaintings.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Artist, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Artist, String> param) {
                return param.getValue().getValue().noOfPaintings;
            }
        });

        this.loadArtist();
        name.setPrefWidth(255);
        bio.setPrefWidth(430);
        noPaintings.setMinWidth(150);
        table.getColumns().addAll(name,bio,noPaintings);
        table.setShowRoot(false);

        filter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                table.setPredicate(new Predicate<TreeItem<Artist>>() {
                    @Override
                    public boolean test(TreeItem<Artist> artistTreeItem) {
                        return artistTreeItem.getValue().name.getValue().contains(newValue)||artistTreeItem.getValue().noOfPaintings.getValue().equals(newValue);
                    }
                });
            }
        });

    }

    public void loadArtist(){
        artists= FXCollections.observableArrayList();

        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT * from artist";
            //ResultSet
            ResultSet rs =st.executeQuery(SQL);
            while(rs.next()){
                    Artist data=new Artist();
                    data.setId(rs.getString("artistid"));
                    data.setName(rs.getString("artistname"));
                    data.setBio(rs.getString("history"));
                String SQL2 =String.format("SELECT count(artid) from painting where artid= %d",Integer.valueOf(data.getId()));
                Statement st2=con.createStatement();
                ResultSet no =st2.executeQuery(SQL2);
                if (no.next()){
                    data.setNoOfPaintings(Integer.valueOf(no.getInt(1)).toString());
                }
                else
                    data.setNoOfPaintings(Integer.valueOf(0).toString());
                artists.add(data);
            }
            RecursiveTreeItem<Artist> root=new RecursiveTreeItem<Artist>(artists,RecursiveTreeObject::getChildren);
            table.setRoot(new RecursiveTreeItem<Artist>(artists,RecursiveTreeObject::getChildren));
            con.close();

        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
    }

    @FXML
    public void hamEvent(){
        burger.setRate(burger.getRate()*-1);
        burger.play();
        if(drawer.isOpened())
            drawer.close();
        else
            drawer.open();
    }
    @FXML
    public void addArtist(){
        FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/addArtistDialog.fxml"));
        StackPane pane= null;
        try {
            pane = dialogLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addnewartist=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
        addnewartist.setContent(pane);
        addnewartist.show();

    }
    @FXML
    public void updateArtist(){
        FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/UpdateArtist.fxml"));
        Pane pane= null;
        try {
            pane = dialogLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        update=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
        update.setContent(pane);
        update.show();


    }

    public void explore(){
        FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/ExploreDialog.fxml"));
        Pane pane= null;
        try {
            pane = dialogLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        explore=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
        explore.setContent(pane);
        explore.show();




    }

}
