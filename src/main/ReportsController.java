package main;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;


public class ReportsController implements Initializable {
    @FXML JFXHamburger ham;
    @FXML StackPane stackPane;
    HamburgerBasicCloseTransition burger;
    @FXML JFXDrawer drawer;
    @FXML private BarChart<String,Number> chartNoOfPaintings;
    @FXML private LineChart<String,Number> soldChart;
    Double salary;
    private Connection con;
    private Statement st;
    String url="jdbc:oracle:thin:@localhost:1521:orcl";




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        burger =new HamburgerBasicCloseTransition(ham);
        burger.setRate(-1);
        try {
            drawer.setSidePane((AnchorPane) FXMLLoader.load(getClass().getResource("../resources/views/drawerContant.fxml")));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        XYChart.Series <String,Number> series = new XYChart.Series<>();
        XYChart.Series <String,Number> series1 = new XYChart.Series<>();
        LocalDate date= LocalDate.now();
        salary=0.0;
        int count=0;
        for(int i=0;i<12;i++) {
            try {
                con = DriverManager.getConnection(url, "main","123456");
                st = con.createStatement();
                String SQL = "SELECT count(*) FROM painting WHERE purchasedate >To_date('" + date.minusMonths(12-i).toString() + "','yyyy-mm-dd') " +
                        "and purchasedate <=To_date('" + date.minusMonths(11-i).toString()+ "','yyyy-mm-dd') ";

                ResultSet rs = st.executeQuery(SQL);
                if (rs.next()) {
                    count= rs.getInt(1);
                }
                else
                    count=0;
                series.getData().add(new XYChart.Data(date.minusMonths(11-i).getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH),Integer.valueOf(count)));

                String SQL2 = "SELECT sum(price) FROM painting WHERE purchasedate >To_date('" + date.minusMonths(12-i).toString()+ "','yyyy-mm-dd') " +
                        "and purchasedate <=To_date('" +date.minusMonths(11-i).toString()+ "','yyyy-mm-dd') ";
                ResultSet rs2 = st.executeQuery(SQL2);
                int sold=0;
                if (rs2.next()) {
                     sold = rs2.getInt(1);
                }
                else sold=0;
                series1.getData().add(new XYChart.Data(date.minusMonths(11-i).getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH),Integer.valueOf(sold)));
                con.close();

            } catch (SQLException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exception = sw.toString();
                DialogMaker.errorDialog(exception,stackPane);
            }
        }

        chartNoOfPaintings.getData().add(series);
        soldChart.getData().add(series1);
    }
    public void hamEvent(){
        burger.setRate(burger.getRate()*-1);
        burger.play();
        if(drawer.isOpened())
            drawer.close();
        else
            drawer.open();
    }
}
