
package main;

import com.jfoenix.controls.JFXButton;
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


public class ExploreUserController implements Initializable {
    @FXML private Label name;
    @FXML private JFXButton x;
    @FXML private TableView<Paintings> table;
    @FXML private ImageView image;
    @FXML private Label username;
    @FXML private Label email;
    @FXML private Label phone;
    @FXML private Label bday;
    @FXML private Label payment;
    @FXML private Label address;
    @FXML private TableColumn<Paintings, ImageView> imageC;
    @FXML private TableColumn<Paintings, String> nameC;
    @FXML private TableColumn<Paintings, String> priceC;
    private Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";
    ObservableList<Paintings> paintings= FXCollections.observableArrayList();


    public class Paintings extends RecursiveTreeObject<ExploreArtistController.Painting> {
        public ImageView photo;
        public StringProperty paintingName;
        public StringProperty price;

        public ImageView getPhoto() {
            return photo;
        }
        public String getPaintingName() {
            return paintingName.get();
        }
        public String getPrice() {
            return price.get();
        }

        public void setPhoto(ImageView photo) {
            this.photo = photo;
        }
        public void setPrice(String price) {
            this.price=new SimpleStringProperty(price);
        }
        public void setPaintingName(String paintingName) {
            this.paintingName=new SimpleStringProperty(paintingName);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UsersAdminController cont2 = AppController.userAdmin.getController();
        UsersAdminController.Users data = cont2.table.getSelectionModel().getSelectedItem().getValue();

        name.setText(cont2.table.getSelectionModel().getSelectedItem().getValue().name.get());
        email.setText(cont2.table.getSelectionModel().getSelectedItem().getValue().email.get());
        username.setText(cont2.table.getSelectionModel().getSelectedItem().getValue().username.get());
        imageC.setCellValueFactory(new PropertyValueFactory<Paintings,ImageView>("photo"));
        nameC.setCellValueFactory(new PropertyValueFactory<Paintings,String>("paintingName"));
        imageC.setPrefWidth(170);
        nameC.setPrefWidth(395);
        priceC.setCellValueFactory(new PropertyValueFactory<Paintings,String>("price"));
        priceC.setPrefWidth(120);

        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL1 = "SELECT profilepicurl,phonenumber,bdate,country,state,city,street,ccv from customer where usernamec='"+username.getText()+"'";
            ResultSet rs1 =st.executeQuery(SQL1);
            while(rs1.next()){
                String url=rs1.getString("profilepicurl");
                if(url!=null)
                image.setImage(new Image("file:///"+url));
                String phoneN=rs1.getString("phonenumber");
                if(phoneN!=null)
                    phone.setText("+"+phoneN);
                else
                    phone.setText("Not Added");
                bday.setText(rs1.getDate("bdate").toString());
                String addressS=rs1.getString("country")+"\n"+ rs1.getString("state")+" - "+rs1.getString("city")+"\n"+rs1.getString("street");
                address.setText(addressS);
                if(rs1.getString("ccv")==null){
                    payment.setText("Not Added");
                }
                else
                    payment.setText("Added");
            }
            //cart loading
            String SQL = "SELECT cartdetail.barcode,painting.price,painting.name,painting.imageurl from cartdetail , shopcart ,painting where shopcart.username='"+data.getUsername()+"' and shopcart.checkedout=0 and cartdetail.cartid=shopcart.cartid";
            ResultSet rs =st.executeQuery(SQL);
            while(rs.next()){
                        Paintings info=new Paintings();
                        info.setPaintingName(rs.getString("name"));
                        ImageView test=new ImageView();
                        test.setImage(new Image("file:///"+rs.getString("imageurl")));
                        test.setFitWidth(170);
                        test.setPreserveRatio(true);
                        test.setSmooth(true);
                        test.setCache(true);
                        info.setPhoto(test);
                        info.setPrice(rs.getString("price"));
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




