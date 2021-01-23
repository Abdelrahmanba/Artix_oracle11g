package main;

import animatefx.animation.FadeInUp;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;


public class Main extends Application {
    FXMLLoader loginLoad,rootLoad;
    FadeTransition fadIn,fadeOut;
    public static AppController control;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //loadfont
        Font.loadFont(getClass().getResourceAsStream("brush script mt kursiv.ttf"),48);
        //load splach screen
        AnchorPane splash=FXMLLoader.load(getClass().getResource("../resources/views/splash.fxml"));
        ImageView logo=(ImageView)splash.getChildren().get(0);
        FadeInUp fadeLogo =new FadeInUp(logo);
        fadeLogo.setSpeed(0.7);
        fadeLogo.play();
        //fade splash effects
        FadeTransition fadeIn = new FadeTransition();
        fadeIn.setNode(splash);
        fadeIn.setFromValue(1);
        fadeIn.setToValue(1);
        fadeIn.setDuration(Duration.seconds(3));
        fadeIn.play();
        fadeIn.setOnFinished((e) -> {
            try {
                control.setAnchor();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        //Pass Stage to App controller
        AppController.setRoot(primaryStage);

        //load main program
        rootLoad=new FXMLLoader((AppController.class.getResource("../resources/views/stage.fxml")));
        rootLoad.load();
        //scenes Manger
        control=(AppController) (rootLoad.getController());

        primaryStage.setScene(control.getShadowScene(splash));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Class.forName("oracle.jdbc.driver.OracleDriver");
        primaryStage.show();

    }

    public static void main(String[] args) { launch(args); }


}
