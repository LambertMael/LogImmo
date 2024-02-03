package com.example.logimmo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AddEquipementController {
    @FXML
    private ListView<Photo> photos;
    @FXML
    private TextField libelle;
    @FXML
    private Label labelPhotos;
    @FXML
    private Button btnPa;
    @FXML
    private Button btnPd;

    private Equipements currentEquipement;

    private Piece currentPiece;

    private Biens currentBien;

    public void initialize(){
        //on va test
        photos.setCellFactory(param -> new ListCell<Photo>() {
            @Override
            public void updateItem(Photo p, boolean empty) {
                super.updateItem(p, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(new ImageView(p.getImage()));
                }
            }
        });
    }

    public void addPhoto(ActionEvent actionEvent) {
        try{
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addPhoto-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            s.getIcons().add(new Image("logo.png"));
            s.setTitle("Ajout d'une photo");
            s.setScene(scene);
            s.initModality(Modality.NONE);
            s.setResizable(false);
            s.show();

            //on créer une photo, on sait que ici la photo sera juste du bien donc juste id_bien
            Photo p = new Photo(currentPiece.getId_biens(),currentEquipement.getId_pieces(),currentEquipement.getId());

            s.setOnHidden(e -> {
                // process input here...
                if (p.getImage()!=null){
                    photos.getItems().add(p);
                }
            });
            AddPhotoController apc = (AddPhotoController) fxmlLoader.getController();
            apc.set(p);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void deletePhoto(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Etes vous sûr de vouloir supprimer " + photos.getSelectionModel().getSelectedItem() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Photo p = photos.getSelectionModel().getSelectedItem();
            photos.getItems().remove(photos.getSelectionModel().getSelectedItem());
            //supprimer dans la base
            DataBase db = new DataBase();
            try{
                db.deletePhoto(p);
                FTPManager ft = new FTPManager();
                ft.init();
                ft.delete(p.getChemin());
                ft.close();
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
    public void save(ActionEvent actionEvent) {
        try{
            if(libelle.getText().equals("") ){
                //c nul
            } else {
                DataBase db = new DataBase();
                currentEquipement.setLibelle(libelle.getText());
                db.uploadEquipement(currentEquipement);
            }

            Stage stage = (Stage) libelle.getScene().getWindow();
            stage.close();
        } catch (SQLLogException e){
            System.out.println(e.getMessage());
        }
    }

    public void setAll(Equipements e, Piece p, Biens b){
        this.currentPiece = p;
        libelle.setText(e.getLibelle());
        photos.getItems().setAll(e.getPhotosEquipements());
        this.currentEquipement = e;
        this.currentBien = b;
    }

    public void setAdd(Equipements equ) {
        //il faut cacher les trucs la
        this.currentEquipement = equ;
        labelPhotos.setVisible(false);
        btnPa.setVisible(false);
        btnPd.setVisible(false);
        photos.setVisible(false);
    }
}