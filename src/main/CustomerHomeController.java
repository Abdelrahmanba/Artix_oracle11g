package main;

import animatefx.animation.BounceInRight;
import animatefx.animation.BounceOutRight;
import animatefx.animation.FadeOutDownBig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class CustomerHomeController implements Initializable {

    @FXML private JFXButton searchB,cartB,likesB,orderB;
    @FXML private JFXButton profile;
    @FXML private JFXTextField searchBar;
    @FXML public StackPane stackPane;
    @FXML public ScrollPane scrollPane;
    @FXML public JFXButton signOutB;
     String  username;
    StackPane serach;
    SearchController cont;
    FXMLLoader orders;
    private Connection con;
    private Statement st;
    FXMLLoader profileLoader;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadMain();
        searchB.setOnAction(event -> {
            boolean flag1=true,flag2=true;
            if(searchBar.getText().isEmpty())
                return;

            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL1 = "SELECT name,barcode,imageurl,price,artistname from painting,artist where UPPER(name) like UPPER ('%" + searchBar.getText() + "%' )and artist.artistid=painting.artid and purchasedate is null";
                ResultSet rs1 = st.executeQuery(SQL1);
                try {
                    FXMLLoader searchResults=new FXMLLoader(AppController.class.getResource("../resources/views/searchResults.fxml"));
                    serach=(StackPane) searchResults.load();
                    cont = searchResults.getController();
                    cont.label.setText("Search results for \"" + searchBar.getText() + "\"");
                } catch (IOException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);                }
                if (rs1.next() == false) {
                    flag1=false;
                }
                else {
                    do {
                        Pane pane = FXMLLoader.load(getClass().getResource("../resources/views/searchPainting.fxml"));
                        ImageView img = (ImageView) pane.getChildren().get(0);
                        img.setImage(new Image("File:///" + rs1.getString("imageurl")));
                        Label name = (Label) pane.getChildren().get(1);
                        String barcodee=rs1.getString("barcode");
                        name.setOnMouseClicked(event1 -> {
                            loadpainting(barcodee);

                        });

                        name.setText(rs1.getString("name"));
                        Label artist = (Label) pane.getChildren().get(2);
                        artist.setText(rs1.getString("artistname"));
                        Label barcode = (Label) pane.getChildren().get(3);
                        barcode.setText(rs1.getString("barcode"));
                        Label price = (Label) pane.getChildren().get(4);
                        price.setText(rs1.getString("price"));
                        cont.paintingV.getChildren().add(pane);
                    } while (rs1.next());
                }


                String SQL2 = "SELECT picurl,artistname,artistid from artist where UPPER(artistname) like UPPER ('%" + searchBar.getText() + "%')";
                ResultSet rs2 = st.executeQuery(SQL2);
                if (rs2.next() == false) {
                    flag2=false;
                }
                else {
                    do {
                        Pane pane = FXMLLoader.load(getClass().getResource("../resources/views/artistSearch.fxml"));
                        ImageView img = (ImageView) pane.getChildren().get(0);
                        img.setImage(new Image("File:///" + rs2.getString("picurl")));
                        Label name = (Label) pane.getChildren().get(1);
                        String artistId=rs2.getString("artistid");
                        name.setOnMouseClicked(event1 -> {
                            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/ArtistDialog.fxml"));
                            Pane pane1= null;
                            try {
                                pane1 = dialogLoader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            PaintingArtistController cont=dialogLoader.getController();
                                cont.loadArtist(artistId);

                            JFXDialog explore=new JFXDialog(stackPane,pane1, JFXDialog.DialogTransition.BOTTOM,true);
                            explore.setContent(pane1);
                            explore.show();
                        });
                        name.setText(rs2.getString("artistname"));
                        cont.artistV.getChildren().add(pane);
                    } while (rs2.next());
                }
                if(!flag1&&!flag2)
                   searchBar.setText("No results :(");
                else {
                    if(!flag1)
                        cont.noresultsP.setVisible(true);
                     if(!flag2)
                        cont.noresultsA.setVisible(true);
                    stackPane.getChildren().add(0, serach);
                    BounceOutRight r=new BounceOutRight(stackPane.getChildren().get(1));
                    r.play();
                }
                con.close();
            }catch(Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
        }
        });

        profile.setOnAction(event -> {
            StackPane pane=null;
            try {
               profileLoader=new FXMLLoader(getClass().getResource("../resources/views/CustomerProfile.fxml"));
                pane=(StackPane) profileLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stackPane.getChildren().add(0,pane);
            new FadeOutDownBig(stackPane.getChildren().get(1)).play();
        });

        cartB.setOnAction(event -> { StackPane pane=null;
        try {
            pane=(StackPane)FXMLLoader.load(getClass().getResource("../resources/views/CustomerCart.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stackPane.getChildren().add(0,pane);
        new FadeOutDownBig(stackPane.getChildren().get(1)).play();
        });

        likesB.setOnAction(event -> {StackPane pane=null;
            try {
                pane=(StackPane)FXMLLoader.load(getClass().getResource("../resources/views/CustomerLikes.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stackPane.getChildren().add(0,pane);
            new FadeOutDownBig(stackPane.getChildren().get(1)).play();

        });
        orderB.setOnAction(event -> {StackPane pane=null;
            try {
                 orders=new FXMLLoader(getClass().getResource("../resources/views/CustomerOrders.fxml"));
                pane=(StackPane) orders.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stackPane.getChildren().add(0,pane);
            new FadeOutDownBig(stackPane.getChildren().get(1)).play();


        });

        signOutB.setOnAction(event -> {
            try {
                Main.control.setSceneLogin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


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

   public  void  loadMain(){
        Pane main=null;
        try {
            main=(Pane)FXMLLoader.load(getClass().getResource("../resources/views/userMain.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrollPane.setContent(main);
    }
}