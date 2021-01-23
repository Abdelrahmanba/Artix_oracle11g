

    package main;

    import com.jfoenix.controls.*;
    import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
    import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
    import javafx.beans.property.*;
    import javafx.beans.value.ChangeListener;
    import javafx.beans.value.ObservableValue;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.control.ScrollPane;
    import javafx.scene.control.TreeItem;
    import javafx.scene.control.TreeTableColumn;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.*;
    import javafx.util.Callback;

    import java.io.IOException;
    import java.io.PrintWriter;
    import java.io.StringWriter;
    import java.net.URL;
    import java.sql.*;
    import java.text.DateFormat;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.ResourceBundle;
    import java.util.function.Predicate;

    public class InventoryController implements Initializable {

        @FXML JFXHamburger ham;
        HamburgerBasicCloseTransition burger;
        @FXML JFXDrawer drawer;
        @FXML JFXTreeTableView<Painting> table;
        @FXML StackPane stackPane;
        JFXDialog addnewartist,update,delete,explore;
        ObservableList<Painting> paintings;
        @FXML JFXButton add,updateB, exploreB,soldB;
        @FXML JFXTextField filter;
        @FXML ScrollPane scrollPane;
        private Connection con;
        private  Statement st;
        String url="jdbc:oracle:thin:@localhost:1521:orcl";
        FXMLLoader updateLoader;




        public class Painting extends RecursiveTreeObject<Painting> {
            private ObjectProperty<ImageView> photo;
            private StringProperty paintingName;
            private StringProperty price;
            private SimpleBooleanProperty sold;
            private StringProperty artist;
            private StringProperty barcode;

            public String getBarcode() {
                return barcode.get();
            }
            public void setBarcode(String barcode) {
                this.barcode=new SimpleStringProperty(barcode);
            }

            public ImageView getPhoto() {
                return photo.get();
            }
            public void setPhoto(ImageView photo) {
                this.photo=new SimpleObjectProperty<ImageView>(photo);
            }

            public String getPaintingName() {
                return paintingName.toString();
            }
            public void setPaintingName(String paintingName) {
                this.paintingName=new SimpleStringProperty(paintingName);
            }

            public String getPrice() {
                return price.get();
            }
            public void setPrice(String price) {
                this.price=new SimpleStringProperty(price);
            }

            public boolean isSold() {
                return sold.get();
            }
            public void setSold(boolean sold) {
                this.sold=new SimpleBooleanProperty(sold);
            }

            public String getArtist() {
                return artist.get();
            }
            public void setArtist(String artist) {
                this.artist=new SimpleStringProperty(artist);
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
                    if(!table.getSelectionModel().getSelectedItem().getValue().sold.get())
                    soldB.setDisable(false);
                    else
                        soldB.setDisable(true);
                }
                else{
                    updateB.setDisable(true);
                    soldB.setDisable(true);
                    exploreB.setDisable(true);
                }
            });
            //table
            JFXTreeTableColumn<Painting,String> names=new JFXTreeTableColumn<>("Name");
            names.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Painting, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Painting, String> param) {
                    return param.getValue().getValue().paintingName;
                }
            });

            JFXTreeTableColumn<Painting,ImageView> photos=new JFXTreeTableColumn<>("Image");
            photos.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Painting, ImageView>, ObservableValue<ImageView>>() {
                @Override
                public ObservableValue<ImageView> call(TreeTableColumn.CellDataFeatures<Painting, ImageView> param) {
                    return param.getValue().getValue().photo;
                }
            });
            JFXTreeTableColumn<Painting,String> barcode=new JFXTreeTableColumn<>("Barcode");
            barcode.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Painting, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Painting, String> param) {
                    return param.getValue().getValue().barcode;
                }
            });
            JFXTreeTableColumn<Painting,String> price=new JFXTreeTableColumn<>("Price");
            price.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Painting, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Painting, String> param) {
                    return param.getValue().getValue().price;
                }
            });
            JFXTreeTableColumn<Painting,String> artistname=new JFXTreeTableColumn<>("Artist");
            artistname.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Painting, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Painting, String> param) {
                    return param.getValue().getValue().artist;
                }
            });
            JFXTreeTableColumn<Painting,String> sold= new JFXTreeTableColumn<>("Sold");
            sold.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Painting, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Painting,String> param) {
                    if(param.getValue().getValue().sold.get())
                    return new SimpleStringProperty("Yes");
                    else return new SimpleStringProperty("No");
                }
            });
            barcode.setPrefWidth(90);
            photos.setMaxWidth(180);
            names.setPrefWidth(200);
            artistname.setPrefWidth(180);
            price.setPrefWidth(100);
            sold.setPrefWidth(70);
            this.loadPainting();
            table.getColumns().addAll(barcode,photos,names,artistname,price,sold);
            table.setShowRoot(false);

            add.setOnAction(event -> {
                try {
                    Main.control.setSenceAddPainting();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            soldB.setOnAction(event ->{
                try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                long bar=Long.valueOf(table.getSelectionModel().getSelectedItem().getValue().getBarcode());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                String sql=String.format("update painting set purchasedate=To_Date('%s','yyyy/MM/dd')where barcode=%d",dateFormat.format(date),bar);
                st.executeUpdate(sql);
                con.commit();
                con.close();
                } catch (SQLException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }
                loadPainting();


            });
            //filter
            filter.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    table.setPredicate(new Predicate<TreeItem<Painting>>() {
                        @Override
                        public boolean test(TreeItem<Painting> paintingTreeItem) {
                            return paintingTreeItem.getValue().barcode.getValue().equals(newValue) || paintingTreeItem.getValue().paintingName.getValue().contains(newValue) || paintingTreeItem.getValue().artist.getValue().contains(newValue);
                        }
                    });
                }
            } );


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
        public void update(){
             updateLoader= new FXMLLoader(getClass().getResource("../resources/views/UpdatePainting.fxml"));
            Pane pane= null;
            try {
                pane = updateLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            update=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
            update.setContent(pane);
            update.show();

        }

        public void explore(){
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/explorePainting.fxml"));
            Pane pane= null;
            try {
                pane = dialogLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            HBox h= (HBox) pane.getChildren().get(0);
            VBox v= (VBox) h.getChildren().get(3);
            v.setVisible(true);
            explore=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
            explore.setContent(pane);
            explore.show();

        }

        public void loadPainting(){
            paintings=FXCollections.observableArrayList();

            try{
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL = "SELECT artistname,name,price,imageurl,purchasedate,barcode from painting,artist where artist.artistid=painting.artid";
                //ResultSet
                ResultSet rs =st.executeQuery(SQL);
                while(rs.next()){
                    Painting data=new Painting();
                    ImageView e=new ImageView("file:///"+rs.getString("imageurl"));
                    e.setFitWidth(180);
                    e.setPreserveRatio(true);
                    data.setPhoto(e);
                    data.setPaintingName(rs.getString("name"));
                    data.setPrice(String.valueOf(rs.getDouble("price")));
                    data.setBarcode(rs.getString("barcode"));
                    if(rs.getString("purchasedate")==null){
                        data.setSold(false);
                    }
                    else
                        data.setSold(true);
                        data.setArtist(rs.getString("artistname"));

                    paintings.add(data);
                }
                table.setRoot(new RecursiveTreeItem<Painting>(paintings,RecursiveTreeObject::getChildren));
                con.close();
            }catch(Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }
        }




    }


