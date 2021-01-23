package main;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UpdatePaintingController implements Initializable {


    @FXML private Label nameL;
    @FXML private JFXButton x;
    @FXML private ImageView image;
    @FXML private JFXTextField name;
    @FXML private Label barcode;
    @FXML private JFXTextField hight;
    @FXML private JFXTextField width;
    @FXML private JFXTextField price;
    @FXML private JFXComboBox<String> genre,unit,artist;
    @FXML private JFXDatePicker date;
    @FXML private JFXTextArea desc;
    String imgurl;
    JFXDialog confirm;
    public Boolean confirmDelete;
    InventoryController cont;
    private Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cont=AppController.InventroyAdmin.getController();
        x.setOnAction(event ->  cont.update.close());
        genre.getItems().addAll( "animals", "birds", "flowers", "naturescapes", "vintage", "city", "realistic","portrait");
        unit.getItems().addAll("CM","METER","INCH");
          confirmDelete=false;
        Utilite.addNumericOnlyWithDot(hight);
        Utilite.addTextLimiter(hight,8);
        Utilite.addNumericOnlyWithDot(width);
        Utilite.addTextLimiter(width,8);
        Utilite.addNumericOnlyWithDot(price);
        Utilite.addTextLimiter(price,9);
        Utilite.addTextLimiter(name,32);

        //load artists list
        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String sql = "SELECT artistname  FROM artist";
            ResultSet rs1 = st.executeQuery(sql);
            while (rs1.next()) {
                artist.getItems().add(rs1.getString("artistname"));
            }
            con.close();
        }
        catch (SQLException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont.stackPane);
        }


        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String sql2 = String.format("select imageurl,description,price,barcode,hight,width,Unit,genre,createdate,name,artistname from Painting,artist where barcode=%d and artist.artistid=painting.artid",Long.valueOf(cont.table.getSelectionModel().getSelectedItem().getValue().getBarcode()));
            ResultSet rs= st.executeQuery(sql2);
            while(rs.next()){
                image.setImage(new Image("File:///"+rs.getString("imageurl")));
                imgurl=rs.getString("imageurl");
                image.setFitWidth(350);
                JFXDepthManager.setDepth(image,1);
                nameL.setText(rs.getString("name"));
                name.setText(rs.getString("name"));
                artist.setValue(rs.getString("artistname"));
                barcode.setText(rs.getString("barcode"));
                hight.setText(rs.getString("hight"));
                width.setText((rs.getString("width")));
                unit.setValue(rs.getString("unit"));
                price.setText(rs.getString("price"));
                genre.setValue(rs.getString("genre"));
                desc.setText(rs.getString("description"));
                if(rs.getDate("creataeate")!=null)
                date.setValue(rs.getDate("createdate").toLocalDate());
            }
            con.close();

        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont.stackPane);
        }

    }

    @FXML void delete() {

        FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/deletePaintingDialog.fxml"));
        Pane pane1= null;
        try {
            pane1 = dialogLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        confirm=new JFXDialog(cont.stackPane,pane1, JFXDialog.DialogTransition.BOTTOM,false);
        confirm.setContent(pane1);
        confirm.show();
        confirm.setOnDialogClosed(event -> {
            if(confirmDelete) {
                try {
                    con = DriverManager.getConnection(url, "main","123456");
                    st = con.createStatement();
                    String sql = String.format("DELETE from painting where barcode=%d", Long.valueOf(cont.table.getSelectionModel().getSelectedItem().getValue().getBarcode()));
                    st.execute(sql);
                    File file=new File(imgurl);
                    file.delete();
                    cont.update.close();
                    DialogMaker.sucsessDialog("The Painting was Deleted successfully..",cont.stackPane);
                    con.commit();
                    con.close();
                } catch (Exception throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,cont.stackPane);
                }

            }
            cont.loadPainting();
        });

    }

    @FXML void update(){
        try {
            con = DriverManager.getConnection(url, "main","123456");
            String sql2 ="update painting set description=?,price=?,hight=?,width=?,Unit=?,genre=?,artid=?,createdate=To_Date('"+date.getValue().toString()+"','yyyy-mm-dd'),name=? where barcode=?";
            PreparedStatement ps = con.prepareStatement(sql2);
            ps.setString(1,desc.getText());
            ps.setDouble(2,Double.valueOf(price.getText()));
            ps.setDouble(3,Double.valueOf(hight.getText()));
            ps.setDouble(4,Double.valueOf(width.getText()));
            ps.setString(5,unit.getValue());
            ps.setString(6,genre.getValue());
            ps.setLong(7,this.loadArtist(artist.getValue()));
            ps.setString(8,name.getText());
            ps.setLong(9,Long.valueOf(cont.table.getSelectionModel().getSelectedItem().getValue().getBarcode()));
            ps.executeUpdate();
            cont.loadPainting();
            cont.update.close();
            DialogMaker.sucsessDialog("The painting was updated successfully",cont.stackPane);
            con.commit();
            con.close();
        } catch (SQLException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont.stackPane);
        }

    }

    public long loadArtist(String name){
        long id=0;
        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String sql = String.format("SELECT artistid  FROM artist where artistname ='%s'",name);
           ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                id= rs.getLong("artistid");
            }
        }
        catch (SQLException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont.stackPane);        }
        return id;
    }
}
