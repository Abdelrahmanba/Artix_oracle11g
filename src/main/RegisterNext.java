package main;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.CustomTextField;
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
import java.util.ResourceBundle;


public class RegisterNext implements Initializable {

    public FXMLLoader loader;
    @FXML private Pane signupNextPane;
    @FXML private ImageView pic;
    @FXML private Button browser;
    @FXML private CustomTextField browserField;
    @FXML private JFXTextField cardName;
    @FXML private JFXTextField cardNo;
    @FXML private JFXTextField expM;
    @FXML private JFXTextField cvv;
    @FXML private JFXTextField expY;
    @FXML private Label errorpic;
    @FXML private Label errorfill;
    public FileChooser chooser;
    public FileChooser.ExtensionFilter filter;
    private File photo;
    File  jarDir;
    CustomerHomeController contHome;
    static String username;
    private Connection con;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        JFXDepthManager.setDepth(pic,1);
        Utilite.addTextLimiter(cardName,20);
        Utilite.addAlphabetsOnly(cardName);
        Utilite.addTextLimiter(cvv,3);
        Utilite.addTextLimiter(expM,2);
        Utilite.addTextLimiter(expY,2);
        Utilite.addTextLimiter(cardNo,16);
        Utilite.addNumericOnly(expM);
        Utilite.addNumericOnly(expY);
        Utilite.addNumericOnly(cvv);
        Utilite.addNumericOnly(cardNo);

    }
    @FXML
    public void openBrowser() throws IOException, URISyntaxException {
        chooser=new FileChooser();
        filter=new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg","*.png");
        chooser.getExtensionFilters().add(filter);
        photo =chooser.showOpenDialog(signupNextPane.getScene().getWindow());

        if(photo!=null){
            jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"profilePic");
            jarDir.mkdir();
            browserField.setText(photo.toString());
            BufferedImage bi= ImageIO.read(new URL("file:///"+photo.toString()));
            if(bi!=null){pic.setImage(SwingFXUtils.toFXImage(bi,null));}

        }
    }

    @FXML public void skip() throws IOException {
        Main.control.setCustomerHome();
         contHome=AppController.customerHome.getController();
         contHome.username=username;

    }
    @FXML public void save(){

        if(cardName.getText().isEmpty()||cardNo.getText().isEmpty()|expM.getText().isEmpty()||expY.getText().isEmpty()||cvv.getText().isEmpty()){
            errorfill.setVisible(true);
            return;
        }
        else {
            errorfill.setVisible(false);
        }
        if(browserField.getText().isEmpty()){
            errorpic.setVisible(true);
            return;
        }
        else
            errorpic.setVisible(false);


        String name=String.format("%s%s",username, browserField.getText().substring(browserField.getText().lastIndexOf(".")));
        File f= new File(jarDir,name);
        try {
            Files.copy(Paths.get(photo.toString()),Paths.get(f.toString()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sql2 = String.format("update customer set ccnumber=%d,ccholdername='%s',ccv=%d,ccexpyear=%d,ccexpmonth=%d,profilepicurl='%s' where usernamec='%s'",Long.valueOf(cardNo.getText()),cardName.getText(),Integer.valueOf(cvv.getText()),Integer.valueOf(expY.getText()),Integer.valueOf(expM.getText()),jarDir.getPath()+"\\"+name,username);
        try {
            con = DriverManager.getConnection(url, "main","123456");
            Statement st = con.createStatement();
            st.executeUpdate(sql2);
            Main.control.setCustomerHome();
            contHome=AppController.customerHome.getController();
            contHome.username=username;
            con.commit();
            con.close();

        } catch (SQLException | IOException throwables) {
            RegisterController cont=AppController.registerLoad.getController();
            StringWriter sw = new StringWriter();
            throwables.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            DialogMaker.errorDialog(exception,cont.stackPane);        }

    }

}
