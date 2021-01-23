package main;


import animatefx.animation.FadeInDownBig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PaintingController implements Initializable {

    @FXML private Label paintingName;
    @FXML private Label ArtistName;
    @FXML private JFXButton whoB;
    @FXML private ImageView home;
    @FXML private MaterialDesignIconView back;
    @FXML private JFXButton addToCart;
    @FXML private MaterialDesignIconView cart;
    @FXML private Label hight;
    @FXML private Label width;
    @FXML private Label likes;
    @FXML private Label price;
    @FXML private JFXButton likeB;
    @FXML private JFXTextArea desc;
    @FXML private ImageView img;
    @FXML private Label genre;
    @FXML private JFXComboBox<String> unit;
    @FXML private StackPane stackPane;
    @FXML private MaterialDesignIconView heartlike;
    private Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

    JFXDialog explore;
    String artid,barcode;
    FXMLLoader dialogLoader;
    CustomerHomeController contHome;
    double hightD,widthD;
    String unitD;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contHome=AppController.customerHome.getController();

        addToCart.setOnAction(event -> {
            if(addToCart.getText().equals(" Add to Cart")) {
                try {
                    con = DriverManager.getConnection(url, "main","123456");
                    st = con.createStatement();
                    String sql = "insert into cartdetail values((select cartid from shopcart where username='" + contHome.username + "' and checkedout =0),'" + barcode + "')";
                    st.executeUpdate(sql);
                    addToCart.setText(" Remove From Cart");
                    cart.setGlyphName("CART_OFF");
                    con.commit();
                    con.close();
                } catch (SQLException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }
            }
            else{
                    try {
                        con = DriverManager.getConnection(url, "main","123456");
                        st = con.createStatement();
                        String SQL5="delete from cartdetail where cartid=(select cartid from shopcart where checkedout=0 and username ='"+contHome.username+"') and barcode="+barcode+"";
                        ResultSet rs5 =st.executeQuery(SQL5);
                        addToCart.setText(" Add to Cart");
                        cart.setGlyphName("CART_PLUS");
                        con.commit();
                        con.close();
                    } catch (SQLException throwables) {
                        StringWriter sw = new StringWriter();
                        throwables.printStackTrace(new PrintWriter(sw));
                        String exception = sw.toString();
                        DialogMaker.errorDialog(exception,stackPane);
                    }
            }
        });


        likeB.setOnAction(event -> {

            if (likeB.getText().equals("Like ")) {
                heartlike.setFill(Paint.valueOf("RED"));
                likeB.setText("Like");
                String finalBarcode = barcode;
                String sql = String.format("insert into likes values('"+contHome.username+"',%d)", Long.valueOf(finalBarcode));
                try {
                    con = DriverManager.getConnection(url, "main","123456");
                    st = con.createStatement();
                    st.execute(sql);
                    con.commit();
                } catch (SQLException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }
            }
            else {
                String finalBarcode1 = barcode;
                likeB.setText("Like ");
                heartlike.setFill(Paint.valueOf("WHITE"));
                    String sql = String.format("DELETE from likes where barcode=%d and username='"+ contHome.username +"'", Long.valueOf(finalBarcode1));
                    try {
                        con = DriverManager.getConnection(url, "main","123456");
                        st = con.createStatement();
                        st.execute(sql);
                        con.commit();
                        con.close();
                    } catch (SQLException throwables) {
                        StringWriter sw = new StringWriter();
                        throwables.printStackTrace(new PrintWriter(sw));
                        String exception = sw.toString();
                        DialogMaker.errorDialog(exception,stackPane);
                    }

            }
            this.loadPainting(barcode);
            contHome.loadMain();

        });
        unit.getItems().addAll("CM","INCH","M");
       home.setOnMouseClicked(event ->{
           FadeInDownBig r = new FadeInDownBig(contHome.stackPane.getChildren().get(1));
           r.setOnFinished(event1 ->  contHome.stackPane.getChildren().remove(0));
           r.play();
       } );

       whoB.setOnAction(event -> {
            dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/ArtistDialog.fxml"));
           Pane pane= null;
           try {
               pane = dialogLoader.load();
           } catch (IOException e) {
               e.printStackTrace();
           }
           PaintingArtistController cont=dialogLoader.getController();
           cont.loadArtist(artid);

           explore=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
           explore.setContent(pane);
           explore.show();
       });



    }

    public void loadPainting(String barcode){
        contHome=AppController.customerHome.getController();

        Long bar=Long.valueOf(barcode);
        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = String.format("SELECT name,price,hight,width,unit,description,imageurl,genre,artistname,artid from painting,artist where barcode=%d and artist.artistid=painting.artid",bar);
            ResultSet rs =st.executeQuery(SQL);
        while(rs.next()){
            this.barcode=barcode;
            paintingName.setText(rs.getString("name"));
            ArtistName.setText(rs.getString("artistname"));
            img.setImage(new Image("File:///"+rs.getString("imageurl")));
            img.setFitWidth(450);
            JFXDepthManager.setDepth(img,1);
            hight.setText(rs.getString("hight"));
             width.setText(rs.getString("width"));
            price.setText(rs.getString("price"));
            genre.setText(rs.getString("genre"));
            desc.setText(rs.getString("description"));
            unit.setValue(rs.getString("unit"));
            unitD=rs.getString("unit");
            if(unitD.equals("CM")){
                hightD=rs.getDouble("hight");
                widthD=rs.getDouble("width");
            }
            if(unitD.equals("M")){
                hightD=rs.getDouble("hight")/100.0;
                widthD=rs.getDouble("width")/100.0;
            }
            if(unitD.equals("INCH")){
                hightD=rs.getDouble("hight")/2.54;
                widthD=rs.getDouble("width")/2.54;
            }
            artid=rs.getString("artid");

        }
            String SQL2 = "SELECT barcode, COUNT(*)  FROM likes where barcode ="+bar+"GROUP BY barcode";
            //ResultSet
            ResultSet rs2 =st.executeQuery(SQL2);
            if(rs2.next()){
                likes.setText(rs2.getString(2));
            }
            else
                likes.setText("0");

            String SQL3 = "SELECT barcode  FROM likes where barcode ="+bar+" AND username ='"+contHome.username+"'";
            //ResultSet
            ResultSet rs3 =st.executeQuery(SQL3);
            if(rs3.next()) {
                heartlike.setFill(Paint.valueOf("RED"));
                likeB.setText("Like");
            }

            String SQL4="select barcode from cartdetail where cartid in (select cartid from shopcart where checkedout=0 and username ='"+contHome.username+"')";
            ResultSet rs4 =st.executeQuery(SQL4);
            while(rs4.next()){
                if(rs4.getString(1).equals(barcode)){
                    addToCart.setText(" Remove From Cart");
                    cart.setGlyphName("CART_OFF");
                }

            }
            unit.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if(newValue.equals("M")){
                            hight.setText(String.format("%.2f",hightD/100.0));
                            width.setText(String.format("%.2f",widthD/100.0));
                        }
                        else if(newValue.equals("INCH")){
                            hight.setText(String.format("%.2f",(hightD/2.54)));
                            width.setText(String.format("%.2f",(widthD/2.54)));
                        }

                        if(newValue.equals("CM")){
                            hight.setText(String.format("%.0f",hightD));
                            width.setText(String.format("%.0f",widthD));
                        }

                }
            });

            con.close();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
    }
    }
}
