package com.example.logimmo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTP;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException{
        Stage s = new Stage();
        s.getIcons().add(new Image("logo.png"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        s.setTitle("Connexion à Logimmo");
        s.setScene(scene);
        s.initModality(Modality.NONE);
        s.setResizable(false);
        s.show();
    }

    public static void main(String[] args) {
        launch();
    }
}