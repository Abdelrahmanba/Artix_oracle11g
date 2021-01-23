package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;


public class ExploreOrder {


    @FXML Label name;
    @FXML JFXButton x;
    @FXML ScrollPane scrollpane;
    @FXML private StackPane stackPane;
    @FXML JFXScrollPane scroll;
    @FXML Label shippingAddress,totalPrice;
    OrdersController cont2;
    CustomerOrdersController cont;
    ObservableList<Painting> paintings= FXCollections.observableArrayList();
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";




    public class Painting {
        private String photo;
        private String paintingName;
        private String barcode;
        private String price;

        public String getPhoto() { return photo; }
        public void setPhoto(String photo) { this.photo = photo; }

        public String getPaintingName() { return paintingName; }
        public void setPaintingName(String paintingName) { this.paintingName = paintingName; }

        public String getBarcode() { return barcode; }
        public void setBarcode(String barcode) { this.barcode = barcode; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
    }




    public void load(String source){
        Long orderId=null;

        if(source.equals("admin")) {
            cont2 = AppController.ordersAdmin.getController();
            OrdersController.Orders data = cont2.table.getSelectionModel().getSelectedItem().getValue();
            name.setText(String.format("Order No. %s By %s", data.getOrderid(), data.getCustomerName()));
            totalPrice.setText(data.getTotalPrice());
            orderId=Long.valueOf(data.getOrderid());
        }
        else if(source.equals("user")){
            CustomerHomeController countHome = AppController.customerHome.getController();
            cont=countHome.orders.getController();
            CustomerOrdersController.Orders data = cont.table.getSelectionModel().getSelectedItem().getValue();
            name.setText(String.format("Order No. %s ", data.getOrderid()));
            totalPrice.setText(data.getTotalPrice());
            orderId=Long.valueOf(data.getOrderid());
        }
        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = String.format("SELECT p.name,p.price,p.barcode,p.imageurl,c.country,c.state,c.city,c.street " +
                    "from painting p,orderdetail,orders,customer c " +
                    "where p.barcode=orderdetail.barcode " +
                    "and orders.customername=c.usernamec " +
                    "and orders.orderid=%d " +
                    "and orderdetail.orderid=%d", orderId,orderId);
            ResultSet rs = st.executeQuery(SQL);
            while (rs.next()) {
                Painting info = new Painting();
                info.setPaintingName(rs.getString("name"));
                info.setPhoto("file:///" + rs.getString("imageurl"));
                info.setBarcode(rs.getString("barcode"));
                info.setPrice(rs.getString("price"));
                String addressS=rs.getString("country")+
                        "\n"+ rs.getString("state")+
                        " - "+rs.getString("city")+
                        "\n"+rs.getString("street");
                shippingAddress.setText(addressS);
                paintings.add(info);
            }
            con.close();
        } catch (SQLException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }

        VBox vbox = new VBox();
        for (int i = 0; i <paintings.size(); i++) {
            Pane pane = null;
            try {
                pane = FXMLLoader.load(getClass().getResource("../resources/views/painting_info.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView image = (ImageView) pane.getChildren().get(0);
            image.setImage(new Image(paintings.get(i).getPhoto()));
            Label name = (Label) pane.getChildren().get(1);
            name.setText(paintings.get(i).getPaintingName());
            Hyperlink link=(Hyperlink)pane.getChildren().get(2);
            link.setText(paintings.get(i).getBarcode());
            link.setOnAction(event -> {
                try {
                    FXMLLoader loadPainting = new FXMLLoader(getClass().getResource("../resources/views/explorePainting.fxml"));
                    Pane pane1=loadPainting.load();
                    HBox H=(HBox)pane1.getChildren().get(0);
                    JFXButton arrow= (JFXButton) H.getChildren().get(1);
                    arrow.setVisible(true);
                    JFXDialog painting=new JFXDialog(stackPane,pane1, JFXDialog.DialogTransition.LEFT,false);
                    arrow.setOnAction(event1 ->painting.close());
                    ExplorePaintingController cont =loadPainting.getController();
                    cont.loadPainting(Long.valueOf(link.getText()));
                    painting.setContent(pane1);
                    painting.show();
                } catch (IOException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }
            });
            Label price = (Label) pane.getChildren().get(3);
            price.setText(paintings.get(i).getPrice());
            vbox.getChildren().add(pane);
        }
        scrollpane.setContent(vbox);
        if(source.equals("admin"))
        x.setOnAction(event -> cont2.explore.close());
        else if(source.equals("user"))
            x.setOnAction(event -> cont.explore.close());

    }

}

