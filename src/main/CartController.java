package main;

import animatefx.animation.BounceInRight;
import animatefx.animation.BounceOutRight;
import animatefx.animation.FadeInDownBig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class CartController implements Initializable {


    @FXML private MaterialDesignIconView back;
    @FXML private ScrollPane scrollPane;
    ArrayList<CartItem> cartItem;
    @FXML private JFXButton chkAll;
    @FXML private JFXButton unchkAll;
    @FXML private JFXButton remove;
    @FXML private JFXButton checkOut;
    @FXML private Label totalPrice,totalItems;
    @FXML private AnchorPane empty;
    @FXML private StackPane stackPane;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

    SimpleDoubleProperty totalPriceP;
    SimpleIntegerProperty totalItemsP;
    CustomerHomeController contHome;


    public class CartItem{
        public String barcode;
        public JFXCheckBox check;
        public Double price;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         contHome =AppController.customerHome.getController();
        chkAll.setDisable(true);
        cartItem=new ArrayList<CartItem>();
        this.loadCartItems();

        chkAll.setOnAction(event -> {
            for(CartItem x:cartItem){
                x.check.setSelected(true);
            }
        });
        unchkAll.setOnAction(event -> {
            for(CartItem x:cartItem){
                x.check.setSelected(false);
            }
        });

        for(CartItem x:cartItem){
            x.check.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    //Disable button
                    boolean flagChk =true,flagUnchk=true;
                    for(CartItem x:cartItem){
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
                        checkOut.setDisable(true);
                    }
                    else{
                        unchkAll.setDisable(false);
                        remove.setDisable(false);
                        checkOut.setDisable(false);

                    }
                    //total price and items
                    if(newValue){
                        totalItemsP.set(totalItemsP.get()+1);
                        totalPriceP.setValue(totalPriceP.get()+ Double.valueOf(x.price));
                    }
                    else{

                        totalItemsP.set(totalItemsP.get()-1);
                        totalPriceP.setValue(totalPriceP.get()- Double.valueOf(x.price));
                    }
                }
            });


        }
        remove.setOnAction(event -> {
            CartItem []array=new CartItem[cartItem.size()];
            array=cartItem.toArray(array);
            for(CartItem x:array) {
                if (x.check.isSelected()) {
                    String SQL5 = "delete from cartdetail where cartid=(select cartid from shopcart where checkedout=0 and username ='" + contHome.username + "') and barcode=" + x.barcode + "";
                    try {
                        con = DriverManager.getConnection(url, "main","123456");
                        st = con.createStatement();
                        ResultSet rs5 = st.executeQuery(SQL5);
                        con.commit();
                        con.close();
                    } catch (SQLException throwables) {
                        StringWriter sw = new StringWriter();
                        throwables.printStackTrace(new PrintWriter(sw));
                        String exception = sw.toString();
                        DialogMaker.errorDialog(exception,stackPane);
                    }
                }
            }
            this.loadCartItems();

        });

        checkOut.setOnAction(event -> {
            String orderId=null;
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String paymentAdded="SELECT ccv FROM customer where usernamec='"+contHome.username+"'";
                ResultSet s=st.executeQuery(paymentAdded);
                if(s.next()){
                    if(s.getString(1)==null) {
                        JFXButton ok=new JFXButton("    I will   ");
                        ok.setPrefHeight(35);
                        ok.setStyle("-fx-text-fill: #0273a8");
                        JFXDialog fail = DialogMaker.confirmDialog(new JFXButton(),ok, "Ops..!", "Please add a Payment method first..", stackPane);
                        ok.setOnAction(event1 -> fail.close());
                        return;
                    }
                }
                con.close();
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);            }
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQLID="SELECT ORDERIDSEQ.NEXTVAL FROM DUAL";
                ResultSet id=st.executeQuery(SQLID);
                if(id.next())
                    orderId=id.getString(1);
                con.close();
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL="UPDATE SHOPCART SET CHECKEDOUT =1 WHERE CARTID=(select cartid from shopcart where checkedout=0 and username ='" + contHome.username + "')";
                String SQL2="INSERT INTO ORDERS VALUES ("+Long.valueOf(orderId)+","+totalPriceP.get()+",'"+contHome.username+"','Paid',To_Date('"+ LocalDate.now().toString()+"','yyyy-mm-dd'))";
                String sqlNewCart="insert into shopcart (cartid, username,checkedout) values (cartseq.nextval,'" + contHome.username +"',0)";
                st.executeUpdate(SQL);
                st.executeUpdate(SQL2);
                st.execute(sqlNewCart);
                con.commit();
                con.close();
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }
            DialogMaker.thankYou(stackPane);

            for(CartItem x:cartItem) {
                if (x.check.isSelected()) {
                    System.out.println("in if");

                    String SQL3 = "update painting set purchasedate=To_Date('"+ LocalDate.now().toString()+"','yyyy-mm-dd') where barcode="+x.barcode+"";
                    String SQL4 = "insert into orderdetail (orderid,barcode) values ("+Long.valueOf(orderId)+","+x.barcode+")";
                    try {
                        con = DriverManager.getConnection(url, "main","123456");
                        st = con.createStatement();
                        st.executeUpdate(SQL3);
                        st.executeUpdate(SQL4);
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
                    String SQL5 = "update cartdetail set cartid=(select cartid from shopcart where checkedout=0 and username ='" + contHome.username +"') where barcode="+x.barcode+"";
                    try {
                        con = DriverManager.getConnection(url, "main","123456");
                        st = con.createStatement();
                        st.executeUpdate(SQL5);
                        con.commit();
                        con.close();
                    } catch (SQLException throwables) {
                        StringWriter sw = new StringWriter();
                        throwables.printStackTrace(new PrintWriter(sw));
                        String exception = sw.toString();
                        DialogMaker.errorDialog(exception,stackPane);
                    }

                }
            }
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL6 = "DELETE FROM SHOPCART WHERE USERNAME='"+contHome.username+"' AND CHECKEDOUT=1";
                st.executeUpdate(SQL6);
                con.commit();
                con.close();
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }

            contHome.loadMain();
            this.loadCartItems();
        });

        back.setOnMouseClicked(event -> {
            FadeInDownBig z=new FadeInDownBig(contHome.stackPane.getChildren().get(1));
            contHome.loadMain();
            z.play();
            z.setOnFinished(event1 ->   contHome.stackPane.getChildren().remove(0));
        });



    }

    public void loadCartItems(){
        cartItem.clear();
        totalPriceP=new SimpleDoubleProperty(0.0);
        totalItemsP=new SimpleIntegerProperty(0);
        totalItems.setText("0");
        totalPrice.setText("0.0");
        totalItemsP.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                totalItems.setText(""+newValue.toString()+"");
            }
        });
        totalPriceP.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                totalPrice.setText(""+newValue.toString()+" ILS");
            }
        });
        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "select cartdetail.barcode,name,imageurl,price,artistname from cartdetail,painting,artist where cartid in (select cartid from shopcart where checkedout=0 and username ='"+contHome.username+"')and artist.artistid=painting.artid and cartdetail.barcode=painting.barcode";
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()!= false) {
                Pane pane=null;
                VBox cartItems=new VBox();
                cartItems.setStyle("-fx-background-color:#f7fbff");
                do {
                    CartItem item=new CartItem();
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
                    totalPriceP.setValue(totalPriceP.get()+ Double.valueOf(price.getText()));
                    totalItemsP.set(totalItemsP.get()+1);
                    cartItem.add(item);
                    cartItems.getChildren().add(pane);
                } while (rs.next());
                scrollPane.setContent(cartItems);
            }
            else{
                remove.setDisable(true);
                unchkAll.setDisable(true);
                checkOut.setDisable(true);
                scrollPane.setContent(empty);
            }
            con.close();
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
