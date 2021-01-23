

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import net.sf.jasperreports.engine.design.JasperDesign;
//import net.sf.jasperreports.engine.xml.JRXmlLoader;
//import net.sf.jasperreports.swing.JRViewer;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.function.Predicate;

public class OrdersController implements Initializable {

    @FXML JFXHamburger ham;
    HamburgerBasicCloseTransition burger;
    @FXML JFXDrawer drawer;
    @FXML JFXTreeTableView<Orders> table;
    @FXML StackPane stackPane;
    JFXDialog explore;
    ObservableList<Orders> orders;
    @FXML JFXButton  exploreB, shippedB,viewB;
    @FXML JFXTextField filter;
    @FXML ScrollPane scrollPane;
    FXMLLoader updateLoader;
    private Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    public class Orders extends RecursiveTreeObject<Orders> {
        private StringProperty orderid;
        private StringProperty CustomerName;
        private StringProperty noItems;
        private StringProperty date;
        private StringProperty totalPrice;
        private StringProperty status;


        public String getStatus() {
            return status.get();
        }
        public void setStatus(String status) {
            this.status=new SimpleStringProperty(status);
        }

        public String getOrderid() { return orderid.get(); }
        public void setOrderid(String orderid) {
            this.orderid=new SimpleStringProperty(orderid);
        }

        public String getCustomerName() {
            return CustomerName.get();
        }
        public void setCustomerName(String customerName) {
            this.CustomerName=new SimpleStringProperty(customerName);
        }

        public String getNoItems() {
            return noItems.get();
        }
        public void setNoItems(String noItems) {
            this.noItems=new SimpleStringProperty(noItems);
        }

        public String getDate() {
            return date.get();
        }
        public void setDate(String date) {
            this.date=new SimpleStringProperty(date);
        }

        public String getTotalPrice() {
            return totalPrice.get();
        }
        public void setTotalPrice(String totalPrice) {
            this.totalPrice=new SimpleStringProperty(totalPrice);
        }
    }
    public class ORDER  {
        private String paintingName;
        private String barcode;
        private String price;

        public String getPaintingName() { return paintingName; }
        public void setPaintingName(String paintingName) { this.paintingName = paintingName; }

        public String getBarcode() { return barcode; }
        public void setBarcode(String barcode) { this.barcode = barcode; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
    }
    public class pricee  {
        private String totalprice;
        private String date;
        private String orderid;

        public String getTotalprice() { return totalprice; }
        public void setTotalprice(String totalprice) { this.totalprice = totalprice; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getOrderid() { return orderid; }
        public void setOrderid(String orderid) { this.orderid = orderid; }
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
                exploreB.setDisable(false);
               if(table.getSelectionModel().getSelectedItem().getValue().getStatus().equals("Paid"))
                    shippedB.setDisable(false);
                else
                    shippedB.setDisable(true);
                viewB.setDisable(false);
            }
            else{
                exploreB.setDisable(true);
                shippedB.setDisable(true);
                viewB.setDisable(true);
            }
        });
        //table
        JFXTreeTableColumn<Orders,String> orderId=new JFXTreeTableColumn<>("Order ID");
        orderId.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Orders, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Orders, String> param) {
                return param.getValue().getValue().orderid;
            }
        });
        JFXTreeTableColumn<Orders,String> customerName=new JFXTreeTableColumn<>("Customer Name");
        customerName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Orders, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Orders, String> param) {
                return param.getValue().getValue().CustomerName;
            }
        });
        JFXTreeTableColumn<Orders,String> noItems=new JFXTreeTableColumn<>("Number of Items");
        noItems.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Orders, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Orders, String> param) {
                return param.getValue().getValue().noItems;
            }
        });
        JFXTreeTableColumn<Orders,String> date=new JFXTreeTableColumn<>("Order Date");
        date.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Orders, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Orders, String> param) {
                return param.getValue().getValue().date;
            }
        });
        JFXTreeTableColumn<Orders,String> price=new JFXTreeTableColumn<>("Total Price");
        price.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Orders, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Orders, String> param) {
                return param.getValue().getValue().totalPrice;
            }
        });
        JFXTreeTableColumn<Orders,String> status=new JFXTreeTableColumn<>("Status");
        status.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Orders, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Orders, String> param) {
                return param.getValue().getValue().status;
            }
        });

        orderId.setPrefWidth(90);
        customerName.setPrefWidth(280);
        price.setPrefWidth(160);
        date.setPrefWidth(160);
        noItems.setPrefWidth(120);
        status.setPrefWidth(180);
        this.loadOrders();
        table.getColumns().addAll(orderId,customerName,price,noItems,date,status);
        table.setShowRoot(false);

        //filter
        filter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                table.setPredicate(new Predicate<TreeItem<Orders>>() {
                    @Override
                    public boolean test(TreeItem<Orders> ordersTreeItem) {
                        return ordersTreeItem.getValue().status.toString().contains(newValue)||ordersTreeItem.getValue().totalPrice.equals(newValue)
                                ||ordersTreeItem.getValue().orderid.equals(newValue)||ordersTreeItem.getValue().CustomerName.toString().contains(newValue)
                                ||ordersTreeItem.getValue().noItems.equals(newValue);
                    }
                });
            }
        }
        );



        //shipped
        shippedB.setOnAction(event -> {
            String sql2 ="update orders set status='Shipped'where orderid='"+Long.valueOf(table.getSelectionModel().getSelectedItem().getValue().orderid.get())+"'";
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                st.executeUpdate(sql2);
                con.commit();
                con.close();
                this.loadOrders();
                DialogMaker.sucsessDialog("The status of the order was updated successfully..",stackPane);
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }

        });
/*
        viewB.setOnAction(event -> {
            try {
                List<ORDER> items = new ArrayList<ORDER>();
                List<pricee> priceItems = new ArrayList<pricee>();

                try {
                    con = DriverManager.getConnection(url,"artix","123456");
                    st = con.createStatement();
                    Long ord=Long.valueOf(table.getSelectionModel().getSelectedItem().getValue().getOrderid());
                    String SQL = String.format("SELECT p.name,p.price,p.barcode " +
                            "from painting p,orderdetail,orders " +
                            "where p.barcode=orderdetail.barcode " +
                            "and orders.orderid=%d " +
                            "and orderdetail.orderid=ORDERS.ORDERID",ord);
                    ResultSet rs = st.executeQuery(SQL);
                    while (rs.next()) {
                        ORDER info = new ORDER();
                        info.setPaintingName(rs.getString("name"));
                        info.setBarcode(rs.getString("barcode"));
                        info.setPrice(rs.getString("price"));
                        items.add(info);
                    }
                    con.close();
                } catch (SQLException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }
                pricee info=new pricee();
                info.setDate(table.getSelectionModel().getSelectedItem().getValue().getDate());
                info.setOrderid(table.getSelectionModel().getSelectedItem().getValue().getOrderid());
                info.setTotalprice(table.getSelectionModel().getSelectedItem().getValue().getTotalPrice());
                priceItems.add(info);

                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(items);
                JRBeanCollectionDataSource itemsPrice = new JRBeanCollectionDataSource(priceItems);

                */
/* Map to hold Jasper report Parameters *//*

                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("beans", itemsJRBean);
                parameters.put("price", itemsPrice);
                OracleDataSource ods=new OracleDataSource();
                ods.setUser("artix");
                ods.setPassword("123456");
                ods.setURL("jdbc:oracle:thin:@localhost:1521:orcl");
                Connection con=ods.getConnection();
                InputStream in=new FileInputStream(new File("invoice.jrxml"));
                JasperDesign j= JRXmlLoader.load(in);
                JasperReport x = JasperCompileManager.compileReport(j);
                parameters.put("Username",table.getSelectionModel().getSelectedItem().getValue().getCustomerName());
                JasperPrint jp= JasperFillManager.fillReport(x,parameters,con);
                JFrame frame=new JFrame();
                frame.getContentPane().add(new JRViewer(jp));
                frame.setPreferredSize(new Dimension(1000,1200));
                frame.pack();
                frame.setVisible(true);



            } catch (SQLException | FileNotFoundException | JRException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }


        });
*/


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


    public void explore(){
        FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/ExploreOrder.fxml"));
        Pane pane= null;
        try {
            pane = dialogLoader.load();
            ExploreOrder ex=dialogLoader.getController();
            ex.load("admin");
        } catch (IOException e) {
            e.printStackTrace();
        }
        explore=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
        explore.setContent(pane);
        explore.show();

    }

    public void loadOrders(){
        orders=FXCollections.observableArrayList();
        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT * from orders ";
            ResultSet rs =st.executeQuery(SQL);
            while(rs.next()){
                Orders data=new Orders();
                String SQL2 = String.format("SELECT count(*) from orderdetail where orderid='%s'",rs.getString("orderid"));
                Statement st2=con.createStatement();
                ResultSet rs2=st2.executeQuery(SQL2);
                if(rs2.next()){
                    data.setNoItems(rs2.getString(1));
                }
                data.setCustomerName(rs.getString("customername"));
                data.setOrderid(rs.getString("orderid"));
                data.setTotalPrice(rs.getString("totalprice"));
                data.setDate(rs.getDate("Pdate").toString());
                data.setStatus(rs.getString("status"));
                orders.add(data);
            }
            con.close();
            table.setRoot(new RecursiveTreeItem<Orders>(orders,RecursiveTreeObject::getChildren));
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
    }




}


