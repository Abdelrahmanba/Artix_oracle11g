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
import javafx.scene.text.Text;
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


public class UsersAdminController implements Initializable {

    @FXML JFXHamburger ham;
    HamburgerBasicCloseTransition burger;
    @FXML JFXDrawer drawer;
    @FXML JFXTreeTableView<Users> table;
    @FXML StackPane stackPane;
    @FXML JFXTextField filter;ObservableList<Users> customers;
    @FXML JFXButton exploreB, deleteB;
    JFXDialog delete,explore;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

   public  class Users extends RecursiveTreeObject<Users>{
        StringProperty name;
        StringProperty username;
        StringProperty email;

        public String getName() {
            return name.get();
        }
        public void setName(String name) {
            this.name=new SimpleStringProperty(name);
        }

        public String getUsername() {
            return username.get();
        }
        public void setUsername(String username) {
            this.username=new SimpleStringProperty(username);
        }

        public String getEmail() {
            return email.get();
        }
        public void setEmail(String email) {
            this.email=new SimpleStringProperty(email);
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
                exploreB.setDisable(false);
                deleteB.setDisable(false);

            }
            else{
                exploreB.setDisable(true);
                deleteB.setDisable(true);
            }
        });

       JFXTreeTableColumn<Users,String> name=new JFXTreeTableColumn<>("Name");
        name.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Users, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Users, String> param) {
                return param.getValue().getValue().name;
            }
        });

        JFXTreeTableColumn<Users,String> username=new JFXTreeTableColumn<>("Username");
        username.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Users, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Users, String> param) {
                return param.getValue().getValue().username;
            }
        });

        JFXTreeTableColumn<Users,String> email=new JFXTreeTableColumn<>("Email");
        email.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Users, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Users, String> param) {
                return param.getValue().getValue().email;
            }
        });

        username.setPrefWidth(260);
        name.setPrefWidth(270);
        email.setPrefWidth(300);
        table.getColumns().addAll(username,name,email);
        this.loadcustomer();
        table.setRoot(new RecursiveTreeItem<Users>(customers,RecursiveTreeObject::getChildren));
        table.setShowRoot(false);

        filter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                table.setPredicate(new Predicate<TreeItem<Users>>() {
                    @Override
                    public boolean test(TreeItem<Users> usersTreeItem) {
                        return usersTreeItem.getValue().email.get().contains(newValue)||usersTreeItem.getValue().username.get().contains(newValue)||usersTreeItem.getValue().name.get().contains(newValue);
                    }
                });

            }
        }
    );

        exploreB.setOnAction(event -> {
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/ExploreCustomer.fxml"));
            Pane pane= null;
            try {
                pane = dialogLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            explore=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.CENTER,false);
            explore.setContent(pane);
            explore.show();

        });

    }
    public void delete(){
        FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/deleteCustomer.fxml"));
        Pane pane= null;
        try {
            pane = dialogLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Text label=(Text)pane.getChildren().get(0);
        label.setText("Delete Customer");
        delete=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
        delete.setContent(pane);
        delete.show();


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

    public void loadcustomer(){
        customers= FXCollections.observableArrayList();

        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT usernamec,email,firstname,lastname from customer";
            //ResultSet
            ResultSet rs =st.executeQuery(SQL);
            while(rs.next()){
                Users data=new Users();
                data.setName(rs.getString("firstname")+" "+rs.getString("lastname"));
                data.setEmail(rs.getString("email"));
                data.setUsername(rs.getString("usernamec"));
                customers.add(data);
            }
            table.setRoot(new RecursiveTreeItem<Users>(customers,RecursiveTreeObject::getChildren));
            table.setShowRoot(false);
            con.close();

        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
    }
}
