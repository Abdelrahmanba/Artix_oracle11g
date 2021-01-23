package main;

import animatefx.animation.BounceInRight;
import animatefx.animation.BounceOutRight;
import animatefx.animation.FadeInDownBig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class CustomerLikesController implements Initializable {


    @FXML private MaterialDesignIconView back;
    @FXML private ScrollPane scrollPane;
    ArrayList<LikeItem> LikesItem;
    @FXML private JFXButton chkAll;
    @FXML private JFXButton unchkAll;
    @FXML private JFXButton remove;
    @FXML private AnchorPane empty;
    @FXML private StackPane stackPane;
    CustomerHomeController contHome;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    public class LikeItem {
        public String barcode;
        public JFXCheckBox check;
        public Double price;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contHome =AppController.customerHome.getController();
        chkAll.setDisable(true);
        LikesItem =new ArrayList<LikeItem>();
        this.loadLikesItems();

        chkAll.setOnAction(event -> {
            for(LikeItem x: LikesItem){
                x.check.setSelected(true);
            }
        });
        unchkAll.setOnAction(event -> {
            for(LikeItem x: LikesItem){
                x.check.setSelected(false);
            }
        });

        for(LikeItem x: LikesItem){
            x.check.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    //Disable button
                    boolean flagChk =true,flagUnchk=true;
                    for(LikeItem x: LikesItem){
                        if(!x.check.isSelected()) {
                            flagChk =false;
                        }
                        if(x.check.isSelected()) {
                            flagUnchk =false;
                        }

                    }
                    if(flagChk)
                        chkAll.setDisable(true);
                    else
                        chkAll.setDisable(false);
                    if(flagUnchk){
                        unchkAll.setDisable(true);
                        remove.setDisable(true);
                    }
                    else{
                        unchkAll.setDisable(false);
                        remove.setDisable(false);

                    }

                }
            });


        }
        remove.setOnAction(event -> {
            LikeItem[]array=new LikeItem[LikesItem.size()];
            array= LikesItem.toArray(array);
            for(LikeItem x:array) {
                if (x.check.isSelected()) {
                    try {
                        con = DriverManager.getConnection(url, "main","123456");
                        st = con.createStatement();
                        String SQL5 = "delete from likes where username='"+contHome.username+"'and barcode=" + x.barcode + "";
                        ResultSet rs5 = st.executeQuery(SQL5);
                        con.commit();
                        con.close();
                    } catch (SQLException throwables) {
                        StringWriter sw = new StringWriter();
                        throwables.printStackTrace(new PrintWriter(sw));
                        String exception = sw.toString();
                        DialogMaker.errorDialog(exception,stackPane);                    }
                }
            }
            this.loadLikesItems();
        });



        back.setOnMouseClicked(event -> {
            FadeInDownBig z=new FadeInDownBig(contHome.stackPane.getChildren().get(1));
            z.play();
            contHome.loadMain();
            z.setOnFinished(event1 ->   contHome.stackPane.getChildren().remove(0));
        });



    }

    public void loadLikesItems(){

        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "select likes.barcode,name,imageurl,price,artistname from painting,artist,likes where artist.artistid=painting.artid and painting.barcode=likes.barcode and likes.username='"+contHome.username+"' and painting.purchasedate is null";
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()!= false) {
                Pane pane=null;
                VBox likesItems=new VBox();
                likesItems.setStyle("-fx-background-color:#f7fbff");
                do {
                    LikeItem item=new LikeItem();
                    pane= (Pane)FXMLLoader.load(getClass().getResource("../resources/views/cartNode.fxml"));
                    ImageView img = (ImageView) pane.getChildren().get(0);
                    img.setImage(new Image("File:///" + rs.getString("imageurl")));
                    Label name = (Label) pane.getChildren().get(1);
                    String barcodee=rs.getString("barcode");
                    item.barcode=barcodee;
                    name.setOnMouseClicked(event1 -> {
                        loadpainting(barcodee);

                    });
                    name.setText(rs.getString("name"));
                    Label artist = (Label) pane.getChildren().get(2);
                    artist.setText(rs.getString("artistname"));
                    Label barcode = (Label) pane.getChildren().get(3);
                    barcode.setText(rs.getString("barcode"));
                    Label price = (Label) pane.getChildren().get(4);
                    price.setText(rs.getString("price"));
                    item.price=Double.valueOf(price.getText());
                    item.check=(JFXCheckBox) pane.getChildren().get(5);
                    item.check.setSelected(true);
                    LikesItem.add(item);
                    likesItems.getChildren().add(pane);
                } while (rs.next());
                con.close();
                scrollPane.setContent(likesItems);
            }
            else{
                remove.setDisable(true);
                unchkAll.setDisable(true);
                scrollPane.setContent(empty);
            }

        } catch (SQLException | IOException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
    }

    public void loadpainting(String barcode){
        FXMLLoader paintingLoader=new FXMLLoader(AppController.class.getResource("../resources/views/paintingCustomer.fxml"));
        StackPane painting=null;
        try {
            painting =paintingLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PaintingController cont=paintingLoader.getController();
        AnchorPane Apane= (AnchorPane) painting.getChildren().get(0);
        Pane innerP= (Pane) Apane.getChildren().get(0);
        JFXButton back= (JFXButton) innerP.getChildren().get(0);
        ImageView img=(ImageView) innerP.getChildren().get(1);
        img.setOnMouseClicked(null);
        img.setCursor(Cursor.DEFAULT);
        back.setVisible(true);
        back.setOnMouseClicked(event -> {
            new BounceInRight(stackPane.getChildren().get(1)).play();
            stackPane.getChildren().remove(0);
        });
        cont.loadPainting(barcode);
        stackPane.getChildren().add(0,painting);
        BounceOutRight r=new BounceOutRight(stackPane.getChildren().get(1));
        r.play();
    }
}
