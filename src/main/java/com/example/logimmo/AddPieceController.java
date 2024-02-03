package com.example.logimmo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.ArrayList;

public class AddPieceController {
    @FXML
    private TextField libelle;
    @FXML
    private TextField surface;
    @FXML
    private ListView photos;
    @FXML
    private ListView<Equipements> equipements;
    @FXML
    private Label labelEqu;
    @FXML
    private Label labelPho;
    @FXML
    private Button btnEd;
    @FXML
    private Button btnEa;
    @FXML
    private Button btnEe;
    @FXML
    private Button btnPa;
    @FXML
    private Button btnPd;

    private Piece currentPiece;

    private Biens currentBien;

    public void initialize(){
        // pour qu'il n'y est que des numeric

        //
        surface.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("[0-9.]")) {
                    surface.setText(newValue.replaceAll("[^0-9.]", ""));
                }
            }
        });


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

    public void deleteEquipement(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Etes vous sûr de vouloir supprimer " + equipements.getSelectionModel().getSelectedItem() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try{
                Equipements e = equipements.getSelectionModel().getSelectedItem();
                equipements.getItems().remove(equipements.getSelectionModel().getSelectedItem());
                //supprimer dans la base aussi
                DataBase db = new DataBase();
                db.deleteEquipement(e, currentPiece, currentBien);
                //peut etre faire un foreach recup les images et supprimer les images dans toutes les listes mais bon j'ai plus le temps
            } catch (SQLLogException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void addEquipement(ActionEvent actionEvent) {
        try{
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addEquipement-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            s.getIcons().add(new Image("logo.png"));
            s.setTitle("Modification d'un équipement");
            s.setScene(scene);
            s.initModality(Modality.NONE);
            s.setResizable(false);
            s.show();
            AddEquipementController ad = (AddEquipementController) fxmlLoader.getController();

            Equipements equ = new Equipements(currentPiece.getId());

            s.setOnHidden(e -> {
                if (equ.getId()!=-1){
                    equipements.getItems().add(equ);
                    photos.getItems().addAll(equ.getPhotosEquipements());
                }
            });

            ad.setAdd(equ);
        } catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public void editEquipement(ActionEvent actionEvent) {
        try{
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addEquipement-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            s.getIcons().add(new Image("logo.png"));
            s.setTitle("Modification d'un équipement");
            s.setScene(scene);
            s.initModality(Modality.NONE);
            s.setResizable(false);
            s.show();


            Equipements e = equipements.getSelectionModel().getSelectedItem();

            //pour que l'affichage soit dynamique
            s.setOnHidden(ev -> {
                equipements.refresh();

            });


            AddEquipementController ad = (AddEquipementController) fxmlLoader.getController();
            ad.setAll(e, currentPiece, currentBien);
        } catch (IOException e){
            e.printStackTrace();
        }
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

            Photo p = new Photo(currentPiece.getId_biens(),currentPiece.getId());

            s.setOnHidden(e -> {
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
            //deletebienrecurs grace à selecteditem
            photos.getItems().remove(photos.getSelectionModel().getSelectedItem());
            //delete de la base
        }
    }

    public void save(ActionEvent actionEvent) {
        try{
            if(libelle.getText().equals("") || surface.getText().equals("")){
                //c nul
            } else {
                DataBase db = new DataBase();
                currentPiece.setLibelle(libelle.getText());
                currentPiece.setSurface(Double.valueOf(surface.getText()));
                db.uploadPiece(currentPiece);
            }
            Stage stage = (Stage) photos.getScene().getWindow();
            stage.close();
        } catch (SQLLogException e){
            System.out.println(e.getMessage());
        }
    }

    public void setAll(Piece p, Biens b){
        libelle.setText(p.getLibelle());
        surface.setText(String.valueOf(p.getSurface()));
        equipements.getItems().setAll(p.getEquipementsPiece());
        photos.getItems().setAll(p.getPhotosPieces());
        this.currentPiece = p;
        this.currentBien = b;
    }

    public void setAdd(Piece p){
        //il faut cacher tout là
        this.currentPiece = p;

        labelEqu.setVisible(false);
        labelPho.setVisible(false);
        btnEa.setVisible(false);
        btnEd.setVisible(false);
        btnPa.setVisible(false);
        btnEe.setVisible(false);
        btnPd.setVisible(false);
        photos.setVisible(false);
        equipements.setVisible(false);
    }

}
