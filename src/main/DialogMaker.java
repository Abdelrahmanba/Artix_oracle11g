package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

public class DialogMaker {

    public static JFXDialog confirmDialog(JFXButton yes,JFXButton no, String label, String text, StackPane parent) {
        try {
            Pane pane = FXMLLoader.load(DialogMaker.class.getResource("../resources/views/ConfirmDialog.fxml"));
            Text l = (Text) pane.getChildren().get(0);
            l.setText(label);
            Label text1 = (Label) pane.getChildren().get(1);
            text1.setText(text);
            HBox hBox = (HBox) pane.getChildren().get(2);
            hBox.getChildren().addAll(no, yes);
            JFXDialog dialog = new JFXDialog(parent, pane, JFXDialog.DialogTransition.BOTTOM);
            dialog.show();
            return dialog;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static void errorDialog(String text, StackPane parent) {
        try {
            Pane pane = FXMLLoader.load(DialogMaker.class.getResource("../resources/views/ErrorDialog.fxml"));
            JFXTextArea l = (JFXTextArea) pane.getChildren().get(0);
            l.setText(text);
            JFXButton b=(JFXButton) pane.getChildren().get(1);
            JFXDialog dialog = new JFXDialog(parent, pane, JFXDialog.DialogTransition.BOTTOM);
            dialog.show();
            b.setOnAction(event -> dialog.close());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void sucsessDialog(String text,StackPane parent) {
        try {
            Pane pane = FXMLLoader.load(DialogMaker.class.getResource("../resources/views/Sucsess.fxml"));
            Text l = (Text) pane.getChildren().get(0);
            l.setText(text);
            JFXDialog dialog = new JFXDialog(parent, pane, JFXDialog.DialogTransition.CENTER);
            dialog.show();
            FadeTransition f=new FadeTransition();
            f.setNode(dialog);
            f.setFromValue(1);
            f.setToValue(1);
            f.setDuration(Duration.seconds(1.5));
            f.setOnFinished(event -> {
                dialog.close();
            });
            f.play();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void thankYou(StackPane parent) {
        try {
            Pane pane = FXMLLoader.load(DialogMaker.class.getResource("../resources/views/Sucsess.fxml"));
            Text te = (Text) pane.getChildren().get(0);
            te.setText("Your order was placed successful.. thank you for choosing Artix!");
            Text l = (Text) pane.getChildren().get(1);
            l.setText("Thank You!");
            JFXDialog dialog = new JFXDialog(parent, pane, JFXDialog.DialogTransition.CENTER);
            dialog.show();
            FadeTransition f=new FadeTransition();
            f.setNode(dialog);
            f.setFromValue(1);
            f.setToValue(1);
            f.setDuration(Duration.seconds(3));
            f.setOnFinished(event -> {
                dialog.close();
            });
            f.play();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
