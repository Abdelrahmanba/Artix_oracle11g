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
import javafx.util.Callback;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class EmployeesController implements Initializable {

    @FXML JFXHamburger ham;
    HamburgerBasicCloseTransition burger;
    @FXML JFXDrawer drawer;
    @FXML JFXTreeTableView<Employee> table;
    @FXML StackPane stackPane;
    @FXML JFXTextField filter;ObservableList<Employee> employees;
    @FXML JFXButton exploreB, deleteB,addB;JFXDialog addDialog,delete,explore;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";

    public  class Employee extends RecursiveTreeObject<Employee>{
        StringProperty name;
        StringProperty position;
        StringProperty username;


        public String getName() {
            return name.get();
        }
        public void setName(String name) {
            this.name=new SimpleStringProperty(name);
        }

        public String getPosition() {
            return position.get();
        }
        public void setPosition(String position) {
            this.position =new SimpleStringProperty(position);
        }

        public String getUsername() {
            return username.get();
        }
        public void setUsername(String username) {
            this.username=new SimpleStringProperty(username);
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
                if(table.getRoot().getChildren().size()==1){
                    deleteB.setDisable(true);

                }

            }
            else{
                exploreB.setDisable(true);
                deleteB.setDisable(true);
            }
        });

        JFXTreeTableColumn<Employee,String> name=new JFXTreeTableColumn<>("Name");
        name.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Employee, String> param) {
                return param.getValue().getValue().name;
            }
        });
        JFXTreeTableColumn<Employee,String> position=new JFXTreeTableColumn<>("Position");
        position.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Employee, String> param) {
                return param.getValue().getValue().position;
            }
        });
        JFXTreeTableColumn<Employee,String> username=new JFXTreeTableColumn<>("Username");
        username.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Employee, String> param) {
                return param.getValue().getValue().username;
            }
        });

        position.setPrefWidth(260);
        name.setPrefWidth(270);
        username.setPrefWidth(300);
        table.getColumns().addAll(position,name,username);
        this.loadEmployee();
        table.setRoot(new RecursiveTreeItem<Employee>(employees,RecursiveTreeObject::getChildren));
        table.setShowRoot(false);

        filter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                table.setPredicate(new Predicate<TreeItem<Employee>>() {
                    @Override
                    public boolean test(TreeItem<Employee> usersTreeItem) {
                        return usersTreeItem.getValue().username.get().contains(newValue)||usersTreeItem.getValue().position.get().contains(newValue)||usersTreeItem.getValue().name.get().contains(newValue);
                    }
                });

            }
        }
        );

        exploreB.setOnAction(event -> {
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/ExploreEmployee.fxml"));
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

        addB.setOnAction(event -> {
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/addEmployeeDialog.fxml"));
            StackPane pane= null;
            try {
                pane = dialogLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            addDialog=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
            addDialog.setContent(pane);
            addDialog.show();
        });

    }
    public void delete(){
        JFXButton no,yes;
        yes=new JFXButton("    Yes    ");
        yes.setPrefHeight(40);
        yes.setStyle("-fx-text-fill: #0273a8");
        no=new JFXButton("     No    ");
        no.setStyle("-fx-text-fill: #0273a8");
        no.setPrefHeight(40);
        JFXDialog di=DialogMaker.confirmDialog(yes,no,"Delete Employee","Are you sure you want to delete \""+table.getSelectionModel().getSelectedItem().getValue().getUsername()+"\" and all of his information?",stackPane);
        no.setOnAction(event -> {
        di.close();
        return;
        });
        yes.setOnAction(event -> {
            di.close();
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String sql = String.format("DELETE from employee where usernamee='%s'",table.getSelectionModel().getSelectedItem().getValue().getUsername());
                st.execute(sql);
                File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "CVs");
                File file =new File(jarDir,""+table.getSelectionModel().getSelectedItem().getValue().getUsername()+""+".pdf");
                if(!file.delete()){
                    throw new Exception();
                }
                con.commit();
                con.close();
                this.loadEmployee();
            } catch (SQLException throwables) {
                StringWriter sw = new StringWriter();
                throwables.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }

        });



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
    public void loadEmployee(){
        employees= FXCollections.observableArrayList();

        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = "SELECT usernamee,position,firstname,lastname from employee";
            ResultSet rs =st.executeQuery(SQL);
            while(rs.next()){
                Employee data=new Employee();
                data.setName(rs.getString("firstname")+" "+rs.getString("lastname"));
                data.setUsername(rs.getString("usernamee"));
                data.setPosition(rs.getString("position"));
                employees.add(data);
            }
            con.close();
            table.setRoot(new RecursiveTreeItem<Employee>(employees,RecursiveTreeObject::getChildren));
            table.setShowRoot(false);
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
    }
}
