package main;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.effects.JFXDepthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


    public class Home implements Initializable {

        @FXML private ImageView image;
        @FXML private Hyperlink artistLatest,recentLabel,topLabel,top1label,top2label,top3label;
        @FXML private Label top1likes,top2likes,top3likes;
        @FXML private ImageView artistLastestImg;
        @FXML private ImageView top;
        @FXML private ImageView top1;
        @FXML private ImageView top2;
        @FXML private ImageView top3;
        @FXML private VBox vbox1,vbox2,vbox3;
        private Image[]img;
        private String [] recBarcodes;
        private String [] topBarcodes;
        private int [] topLikes;

        private String [] names;
        private String barcodeChoose;
        private String barcodevaluable;

        private Connection con;
        private Statement st;
        String url="jdbc:oracle:thin:@localhost:1521:orcl";


        @Override
        public void initialize(URL location, ResourceBundle resources) {
            CustomerHomeController contHome=AppController.customerHome.getController();

            //initialize recent
            img=new Image[3];
            recBarcodes =new String[3];
            topBarcodes =new String[3];
            topLikes =new int[3];
            names=new String[3];
            for (int i=0;i<3;i++){
                recBarcodes[i] ="0";
            }
            for (int i=0;i<3;i++){
                topBarcodes[i] ="-1";
            }
            for (int i=0;i<3;i++){
                names[i] ="";
            }

            try{
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL = "SELECT imageurl,barcode,name from painting where purchasedate is null order by adddate desc  ";
                ResultSet rs =st.executeQuery(SQL);
                int i=0;
                while(rs.next()&&i<3){
                    img[i]=new Image("file:///"+rs.getString(1),567,378,false,true);
                    names[i]=rs.getString("name");
                    recBarcodes[i++]=rs.getString(2);
                }
                con.close();
            }catch(Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,contHome.stackPane);
            }
            //image.setFitWidth(610);
            JFXDepthManager.setDepth(vbox1,1);
            JFXDepthManager.setDepth(vbox2,1);
            JFXDepthManager.setDepth(vbox3,1);

            image.setImage(img[0]);
            recentLabel.setText(names[0]);
            FadeOut out1=new FadeOut(image);
            FadeIn in1 =new FadeIn(image);
            FadeOut out2=new FadeOut(image);
            FadeIn in2 =new FadeIn(image);
            FadeOut out3=new FadeOut(image);
            FadeIn in3 =new FadeIn(image);

            out1.setDelay(Duration.seconds(2));
            out2.setDelay(Duration.seconds(2));
            out3.setDelay(Duration.seconds(2));
            in1.setSpeed(2);
            out1.setSpeed(10);
            in2.setSpeed(2);
            out2.setSpeed(10);
            in3.setSpeed(2);
            out3.setSpeed(10);

            out1.setOnFinished(event -> {
                image.setImage(img[1]);
                recentLabel.setText(names[1]);
                in1.play();

            });
            out2.setOnFinished(event -> {
                image.setImage(img[2]);
                recentLabel.setText(names[2]);
                in2.play();

            });
            out3.setOnFinished(event -> {
                image.setImage(img[0]);
                recentLabel.setText(names[0]);
                in3.play();

            });
            in1.setOnFinished(event -> out2.play());
            in2.setOnFinished(event -> out3.play());
            in3.setOnFinished(event -> out1.play());
            out1.play();

            recentLabel.setOnAction(event -> {
                if(recentLabel.getText().equals(names[0])&&!names[0].isEmpty()){
                    this.loadpainting(recBarcodes[0]);
                }
                else if(recentLabel.getText().equals(names[1])&&!names[1].isEmpty()) {
                    this.loadpainting(recBarcodes[1]);
                }
                else if(recentLabel.getText().equals(names[2])&&!names[2].isEmpty()){
                    this.loadpainting(recBarcodes[2]);
                }

            });
            //initialize "choose"
            try{
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL = "SELECT imageurl,barcode,artistname,name " +
                        "FROM (SELECT * FROM painting ORDER BY dbms_random.value) P,artist r " +
                        "WHERE rownum = 1 and P.artid=r.artistid and purchasedate is null ";
                ResultSet rs =st.executeQuery(SQL);
                if(rs.next()){
                    artistLastestImg.setImage(new Image("file:///"+rs.getString("imageurl"),567,378,false,true));
                    artistLatest.setText(String.format("%s by %s",rs.getString("name"),rs.getString("artistname")));
                    barcodeChoose=rs.getString("barcode");
                }
                con.close();
            }catch(Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,contHome.stackPane);
            }
            if(!artistLatest.getText().isEmpty())
            artistLatest.setOnAction(event ->  this.loadpainting(barcodeChoose));

            //initialize most valuable
            try{
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL = "SELECT imageurl,barcode,name FROM painting WHERE purchasedate is null and price=(SELECT MAX(price) FROM painting where purchasedate is null ) ";
                //ResultSet
                ResultSet rs =st.executeQuery(SQL);
                if(rs.next()){
                    top.setImage(new Image("file:///"+rs.getString("imageurl"),567,378,false,true));
                    topLabel.setText(rs.getString("name"));
                    barcodevaluable=rs.getString("barcode");
                }
                con.close();
            }catch(Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,contHome.stackPane);
            }
            if(!topLabel.getText().isEmpty())
            topLabel.setOnAction(event -> this.loadpainting(barcodevaluable));
            //initialize most liked
            try{

                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL = "SELECT likes.barcode,COUNT(likes.barcode) AS value_occurrence FROM likes,painting where painting.barcode=likes.barcode and purchasedate is null GROUP BY likes.barcode  ORDER BY value_occurrence DESC";
                ResultSet rs =st.executeQuery(SQL);
                int i=0;
                while(rs.next()&&i<3){
                    long barcode=rs.getLong("barcode");
                    topBarcodes[i]=Long.valueOf(barcode).toString();
                    topLikes[i++]=rs.getInt("value_occurrence");
                }
                top1likes.setText(Integer.valueOf(topLikes[0]).toString()+" Likes");
                top2likes.setText(Integer.valueOf(topLikes[1]).toString()+" Likes");
                top3likes.setText(Integer.valueOf(topLikes[2]).toString()+" Likes");

                String SQL2 =String.format("SELECT name,imageurl FROM painting where barcode=%d",Long.valueOf(topBarcodes[0]));
                ResultSet rs2 =st.executeQuery(SQL2);
                if(rs2.next()) {
                    top1label.setText(rs2.getString(1));
                    top1.setImage(new Image("file:///"+rs2.getString(2),138,95,false,true));
                }
                String SQL3 =String.format("SELECT name,imageurl FROM painting where barcode=%d",Long.valueOf(topBarcodes[1]));
                ResultSet rs3 =st.executeQuery(SQL3);
                if(rs3.next()) {
                    top2label.setText(rs3.getString(1));
                    top2.setImage(new Image("file:///"+rs3.getString(2),138,95,false,true));
                }
                String SQL4 =String.format("SELECT name,imageurl FROM painting where barcode=%d",Long.valueOf(topBarcodes[2]));
                ResultSet rs4 =st.executeQuery(SQL4);
                if(rs4.next()) {
                    top3label.setText(rs4.getString(1));
                    top3.setImage(new Image("file:///"+rs4.getString(2),138,95,false,true));
                }
                if(!topBarcodes[0].equals("-1"))
                    top1label.setOnAction(event -> this.loadpainting(topBarcodes[0]));
                if(!topBarcodes[1].equals("-1"))
                    top2label.setOnAction(event -> this.loadpainting(topBarcodes[1]));
                if(!topBarcodes[2].equals("-1"))
                    top3label.setOnAction(event -> this.loadpainting(topBarcodes[2]));
                con.close();

            }catch(Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,contHome.stackPane);
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
            CustomerHomeController contHome=AppController.customerHome.getController();
            AnchorPane Apane= (AnchorPane) painting.getChildren().get(0);
            Pane innerP= (Pane) Apane.getChildren().get(0);
            JFXButton back= (JFXButton) innerP.getChildren().get(0);
            back.setVisible(true);
            back.setOnAction(event -> {
                FadeInDownBig r = new FadeInDownBig(contHome.stackPane.getChildren().get(1));
                r.setOnFinished(event1 ->  contHome.stackPane.getChildren().remove(0));
                r.play();
            });
            cont.loadPainting(barcode);
            contHome.stackPane.getChildren().add(0, painting);
            FadeOutDownBig r=new FadeOutDownBig(contHome.stackPane.getChildren().get(1));
            r.play();
        }
    }

