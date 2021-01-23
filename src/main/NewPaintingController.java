package main;

import animatefx.animation.SlideInRight;
import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static main.Main.control;

public class NewPaintingController implements Initializable {
    @FXML JFXHamburger ham;
    HamburgerBasicCloseTransition burger;
    @FXML JFXDrawer drawer;
    @FXML private JFXTextField barcode;
    @FXML private JFXTextField hight;
    @FXML private JFXTextField price;
    @FXML private JFXTextField namep;
    @FXML private JFXTextField width;
    @FXML private JFXDatePicker date;
    @FXML private JFXTextArea desc;
    @FXML private JFXComboBox<String> unit;
    @FXML private JFXButton cancel;
    @FXML private JFXButton save;
    @FXML private JFXComboBox<String> genere;
    @FXML public JFXComboBox<String> artist;
    @FXML private Hyperlink addArtist;
    @FXML private ImageView painting;
    @FXML private JFXButton browser;
    @FXML private JFXTextField browserField;
    @FXML StackPane stackPane;
    @FXML private Label errorName;
    @FXML private Label errorBarcode;
    @FXML private Label errorGenere;
    @FXML private Label errorHightWidth;
    @FXML private Label errorArtist;
    @FXML private Label errorPrice;
    @FXML private Label errrorDesc;
    @FXML private Label errorBarcode1;
    @FXML private Label errorImage;
    public JFXDialog addnewartist,done;
    public FileChooser chooser;
    public FileChooser.ExtensionFilter filter;
    private File photo,jarDir;
    BufferedImage bi;
    private Connection con;
    private  Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //hamburger and side panel
        burger =new HamburgerBasicCloseTransition(ham);
        burger.setRate(-1);
        try {
            drawer.setSidePane((AnchorPane) FXMLLoader.load(getClass().getResource("../resources/views/drawerContant.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new SlideInRight(painting).play();
        //fields limiter
        Utilite.addNumericOnly(barcode);
        Utilite.addTextLimiter(barcode,10);
        Utilite.addNumericOnlyWithDot(hight);
        Utilite.addTextLimiter(hight,8);
        Utilite.addNumericOnlyWithDot(width);
        Utilite.addTextLimiter(width,8);
        Utilite.addNumericOnlyWithDot(price);
        Utilite.addTextLimiter(price,9);
        Utilite.addTextLimiter(namep,32);
        genere.getItems().addAll( "animals", "birds", "flowers", "naturescapes", "vintage", "city", "realistic","portrait");
        unit.getItems().addAll("CM","METER","INCH");
        unit.setValue("CM");
        date.setValue(LocalDate.now());
        JFXDepthManager.setDepth(painting,1);

        //artist
        artist.setOnAction(event ->      {
            if(artist.getValue()=="Other")
            addArtist.setVisible(true);
        else
        addArtist.setVisible(false);
        });

        //update artist
        addArtist.setOnAction(event ->      {
            FXMLLoader dialogLoader= new FXMLLoader(getClass().getResource("../resources/views/addArtistDialog.fxml"));
                    StackPane pane= null;
                    try {
                        pane = dialogLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addnewartist=new JFXDialog(stackPane,pane, JFXDialog.DialogTransition.BOTTOM,true);
        addnewartist.setContent(pane);
        addnewartist.show();}
        );

        //cancel handlig
        cancel.setOnAction(event -> {
            try {
                Main.control.setSenceAdminMain();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //artist name box
        artist.getItems().add("Other");
        this.loadArtist();
        save.setOnAction(event -> {
            boolean nextN = true,nextB=true,nextD=true, nextP=true, nextG = true, nextA = true,nextAR=true,nextDesc=true,nextI=true;


            if (namep.getText().isEmpty()) {
                errorName.setVisible(true);
                nextN = false;
            } else {
                errorName.setVisible(false);
                nextN = true;
            }
            if (browserField.getText().isEmpty()) {
                errorImage.setVisible(true);
                nextI = false;
            } else {
                errorImage.setVisible(false);
                nextI = true;
            }
            if (barcode.getText().isEmpty()) {
                nextB = false;
               errorBarcode.setVisible(true);
            } else {
                errorBarcode.setVisible(false);
                nextB=true;
            }
            if (hight.getText().isEmpty() || width.getText().isEmpty()) {
                errorHightWidth.setVisible(true);
                nextD = false;
            } else {
                errorHightWidth.setVisible(false);
                nextD = true;
            }

            if (price.getText().isEmpty()) {
                errorPrice.setVisible(true);
                nextP = false;
            } else {
                    errorPrice.setVisible(false);
                nextP = true;
            }
            if(genere.getValue()==null){
                errorGenere.setVisible(true);
                nextG=false;
            }
            else{
                errorGenere.setVisible(false);
                nextG=true;
            }
            if(artist.getValue()==null||artist.getValue().equals("Other")){
                errorArtist.setVisible(true);
                nextAR=false;
            }
            else{
                errorArtist.setVisible(false);
                nextAR=true;
            }
            if (desc.getText().isEmpty()) {
                errrorDesc.setVisible(true);
                nextDesc = false;
            } else {
                errrorDesc.setVisible(false);
                nextDesc = true;
            }

            if (nextAR && nextA && nextB && nextD && nextDesc && nextG && nextN&&nextP) {
                String bar=barcode.getText();


                String sql = "SELECT barcode  FROM painting where barcode ='"+bar+"'";
                ResultSet rs = null;
                boolean found = false;
                //check for painting exits
                try {
                    con = DriverManager.getConnection(url, "main","123456");
                    st = con.createStatement();
                    rs = st.executeQuery(sql);
                    if (rs.next()) {
                         errorBarcode1.setVisible(true);
                            return;
                        }
                    else
                        errorBarcode1.setVisible(false);
                    con.close();
                    }
                 catch (SQLException throwables) {
                     StringWriter sw = new StringWriter();
                     throwables.printStackTrace(new PrintWriter(sw));
                     String exception = sw.toString();
                     DialogMaker.errorDialog(exception,stackPane);
                }

                String name=String.format("%s%s",barcode.getText(), browserField.getText().substring(browserField.getText().lastIndexOf(".")));
                File f= new File(jarDir,name);
                try {
                    Files.copy(Paths.get(photo.toString()),Paths.get(f.toString()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);
                }

                String sql2 = String.format("insert into Painting (imageurl,description,price,barcode, hight, width,Unit,genre,artid,createdate,name,adddate) values (?,?,?,?,?,?,?,?,?,To_Date('%s','yyyy-mm-dd'),?,To_Date('%s','yyyy-mm-dd'))",date.getValue(),LocalDate.now().toString());
                PreparedStatement ps;
                try {
                    con = DriverManager.getConnection(url, "main","123456");
                    ps = con.prepareStatement(sql2);
                    ps.setString(1,jarDir.getPath()+"\\"+name);
                    ps.setString(2,desc.getText());
                    ps.setDouble(3,Double.valueOf(price.getText()));
                    ps.setLong(4,Long.valueOf(barcode.getText()));
                    ps.setDouble(5,Double.valueOf(hight.getText()));
                    ps.setDouble(6,Double.valueOf(width.getText()));
                    ps.setString(7,unit.getValue());
                    ps.setString(8, genere.getValue());
                    ps.setInt(9,this.loadArtist(artist.getValue()));
                    ps.setString(10,namep.getText());
                    ps.executeQuery();
                    control.setInventoryAdmin();
                    InventoryController cont=AppController.InventroyAdmin.getController();
                    DialogMaker.sucsessDialog("The Painting was added successfully..",cont.stackPane);
                    con.close();

                } catch (SQLException | IOException throwables) {
                    StringWriter sw = new StringWriter();
                    throwables.printStackTrace(new PrintWriter(sw));
                    String exception = sw.toString();
                    DialogMaker.errorDialog(exception,stackPane);                }

            }

        });


    }
    public void openBrowser() throws IOException, URISyntaxException {
        chooser=new FileChooser();
        filter=new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg","*.png");
        chooser.getExtensionFilters().add(filter);
        photo =chooser.showOpenDialog(stackPane.getScene().getWindow());

        if(photo!=null){
            jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"Paintings");
            jarDir.mkdir();
            browserField.setText(photo.toString());
            bi= ImageIO.read(new URL("file:///"+photo.toString()));
            if(bi!=null){painting.setImage(SwingFXUtils.toFXImage(bi,null));}
        }
    }


    public void loadArtist(){

        String sql = "SELECT artistname  FROM artist";
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                artist.getItems().add(rs.getString("artistname"));

            }
            con.close();
        }
        catch (SQLException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);
        }
    }

    public int loadArtist(String name){
        String sql = String.format("SELECT artistid  FROM artist where artistname ='%s'",name);
        ResultSet rs = null;
        int id=0;
        try {
            con = DriverManager.getConnection(url, "main","123456");
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                 id= rs.getInt("artistid");
            }
            con.close();
        }
        catch (SQLException throwables) {
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,stackPane);        }
        return id;
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
}
