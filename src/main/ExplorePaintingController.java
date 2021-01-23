package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.effects.JFXDepthManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.ResourceBundle;


public class ExplorePaintingController implements Initializable {

    @FXML private Label name;
    @FXML private ImageView image;
    @FXML private Label artist;
    @FXML private Label barcode;
    @FXML private Label di;
    @FXML private Label addedon;
    @FXML private Label price;
    @FXML private Label genre;
    @FXML private Label soldon;
    @FXML private Label createdon;
    @FXML private JFXTextArea desc;
    @FXML JFXButton x;
    private Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";
    InventoryController cont;





    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(AppController.InventroyAdmin!=null) {
       cont =AppController.InventroyAdmin.getController();
            long bar = Long.valueOf(cont.table.getSelectionModel().getSelectedItem().getValue().getBarcode());
            this.loadPainting(bar);
            x.setOnAction(event -> cont.explore.close());
        }
    }

    public void loadPainting(Long bar){
        try{
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            String SQL = String.format("SELECT * from painting where barcode=%d",bar);
            //ResultSet
            ResultSet rs =st.executeQuery(SQL);
            while(rs.next()){
                name.setText(rs.getString("name"));
                image.setImage(new Image("File:///"+rs.getString("imageurl"),437,359,true,true));
                image.setFitWidth(350);
                JFXDepthManager.setDepth(image,1);
                String SQL2 =String.format("SELECT artistname from artist where artistid= %d",rs.getInt("artid"));
                Statement st2=con.createStatement();
                ResultSet no =st2.executeQuery(SQL2);
                while(no.next())
                    artist.setText(no.getString("artistname"));
                barcode.setText(rs.getString("barcode"));
                di.setText(rs.getString("hight")+ "  X  " +rs.getString("width")+ "  " +rs.getString("unit"));
                //likes.setText(rs.getString("likes"));
                price.setText(rs.getString("price"));
                genre.setText(rs.getString("genre"));
                addedon.setText(rs.getDate("adddate").toString());
                Date sold=rs.getDate("purchasedate");
                if(sold==null)
                    soldon.setText("Not Sold Yet");
                else
                    soldon.setText(sold.toString());
                createdon.setText(rs.getDate("createdate").toString());
                desc.setText(rs.getString("description"));
            }
            con.close();

        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont.stackPane);
        }

    }


}
