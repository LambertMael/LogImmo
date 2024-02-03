package com.example.logimmo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddPhotoController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label errorLabel;

    private File f;

    private Photo currentPhoto;


    public void choose(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selectionnez une image :");
        chooser.setInitialDirectory(new File("c:\\"));

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images (*.jpg)", "*.jpg", "*.jpeg", "*.png");
        chooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) nameLabel.getScene().getWindow(); // on récupère le stage actuel pour afficher le filechooser
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            nameLabel.setText(file.getAbsolutePath()+ " sélectionné");
            this.f = file;
        } else {
            this.f = null;
            errorLabel.setText("Aucune image sélectionnée.");
        }
    }

    public void save(ActionEvent actionEvent) {
        if(this.f==null){
            //on show l'error
            errorLabel.setText("Vous ne pouvez pas sauvegarder, aucune image sélectionnée.");
        } else {
            //c'est good donc go upload
            currentPhoto.setChemin(f.getPath());
            DataBase db = new DataBase();
            try{
                db.uploadPhoto(currentPhoto);
            } catch (SQLLogException e){
                System.out.println(e.getMessage());
            }

            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.close();

        }
    }

    public void set(Photo photo) {
        this.currentPhoto = photo;
    }
}
