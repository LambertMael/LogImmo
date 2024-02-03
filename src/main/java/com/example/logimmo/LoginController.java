package com.example.logimmo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;


import java.io.IOException;


public class LoginController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button ConnexionButton;

    @FXML
    private Label passLabel;

    @FXML
    private Label loginLabel;

    @FXML
    private Text mainLabel;

    @FXML
    protected void onConnexionButtonClick() {
        //verif BCRYPT

        DataBase db = new DataBase();
        try{
            //Login : a
            //mdp : a
            if(db.connexionUser(loginField.getText(),passwordField.getText() )){
                //CHARGEMENT ASSEZ LONG
                mainLabel.setText("Chargement, merci de patienter.");
                Stage s = (Stage) loginField.getScene().getWindow();// le seul moyen de récupérer le stage actuel
                s.getIcons().add(new Image("logo.png"));
                s.setTitle("Chargement, merci de patienter.");
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("app-view.fxml"));
                try{
                    Scene firstScene = new Scene(fxmlLoader.load(), 900, 700);
                    s.setTitle("Logimmo - L'app de gestion immo");
                    s.setResizable(true);
                    s.setScene(firstScene);
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }
            } else {
                mainLabel.setText("Mauvais identifiants.");
            }
        } catch(SQLLogException e){
            System.out.println(e.getMessage());
        }



    }
}