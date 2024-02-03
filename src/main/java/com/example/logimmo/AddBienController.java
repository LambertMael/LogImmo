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

public class AddBienController {
    @FXML
    private ListView<Photo> photos;
    @FXML
    private ListView<Piece> pieces;
    @FXML
    private TextField rue;
    @FXML
    private TextField cp;
    @FXML
    private TextField prix;
    @FXML
    private TextField ville;
    @FXML
    private TextField annee;
    @FXML
    private TextArea description;
    @FXML
    private CheckBox isLibre;
    @FXML
    private Label labelPieces;
    @FXML
    private Label labelPhotos;
    @FXML
    private Button btnDPi;
    @FXML
    private Button btnAPi;
    @FXML
    private Button btnEPi;
    @FXML
    private Button btnAph;
    @FXML
    private Button btnDph;
    private Biens currentBien;

    public void initialize(){
        // pour qu'il n'y est que des numeric
        annee.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    annee.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        // pour qu'il n'y est que des float
        prix.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("[0-9.]")) {
                    prix.setText(newValue.replaceAll("[^0-9.]", ""));
                }
            }
        });

        //pour avoir une preview
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

    public void setAll(Biens b){
        rue.setText(b.getRue());
        cp.setText(b.getCp());
        prix.setText(String.valueOf(b.getPrix()));
        annee.setText(String.valueOf(b.getAnneeConstru()));
        ville.setText(b.getVille());
        description.setText(b.getDescription());
        isLibre.setSelected(b.isLibre());
        //charger pièces
        pieces.getItems().setAll(b.getPiecesBiens());
        photos.getItems().setAll(b.getPhotosBiens());

        currentBien = b;
    }
    public void deletePiece(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Etes vous sûr de vouloir supprimer " + pieces.getSelectionModel().getSelectedItem() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try{
                //supprimer de la liste
                Piece p = pieces.getSelectionModel().getSelectedItem();
                pieces.getItems().remove(pieces.getSelectionModel().getSelectedItem());
                //supprimer dans la base aussi
                DataBase db = new DataBase();
                db.deletePiece(p, currentBien);
            } catch (SQLLogException e){
                System.out.println(e.getMessage());
            }
        }
    }
    public void addPiece(ActionEvent actionEvent) {
        try{
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addPiece-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            s.setTitle("Ajout d'une pièce");
            s.getIcons().add(new Image("logo.png"));
            s.setScene(scene);
            s.initModality(Modality.NONE);
            s.setResizable(false);
            s.show();
            AddPieceController ad = (AddPieceController) fxmlLoader.getController();

            //on créer une pièce qu'on envoit sur le controller
            Piece p = new Piece(currentBien.getId());

            s.setOnHidden(e -> {
                if (p.getId()!=-1){
                    pieces.getItems().add(p);
                }
            });
            ad.setAdd(p);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void editPiece(ActionEvent actionEvent) {
        try{
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addPiece-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            s.setTitle("Modification d'une pièce");
            s.getIcons().add(new Image("logo.png"));
            s.setScene(scene);
            s.initModality(Modality.NONE);
            s.setResizable(false);
            s.show();
            AddPieceController ad = (AddPieceController) fxmlLoader.getController();

            Piece p = pieces.getSelectionModel().getSelectedItem();

            //pour que l'affichage soit dynamique
            s.setOnHidden(e -> {
                pieces.refresh();
            });

            ad.setAll(pieces.getSelectionModel().getSelectedItem(), currentBien);
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

            //on créer une photo, on sait que ici la photo sera juste du bien donc juste id_bien
            Photo p = new Photo(currentBien.getId());

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
        //on upload le bien
        try{
            if(rue.getText().equals("") || cp.getText().equals("") || prix.getText().equals("") || annee.getText().equals("") || ville.getText().equals("") || description.getText().equals("")){
                //c nul
            } else {
                DataBase db = new DataBase();
                currentBien.setRue(rue.getText());
                currentBien.setCp(cp.getText());
                currentBien.setPrix(Double.valueOf(prix.getText()));
                currentBien.setAnneeConstru(Integer.valueOf(annee.getText()));
                currentBien.setVille(ville.getText());
                currentBien.setDescription(description.getText());
                currentBien.setLibre(isLibre.isSelected());

                db.uploadBien(currentBien);
            }

            Stage stage = (Stage) btnDph.getScene().getWindow();
            stage.close();
        } catch (SQLLogException e){
            System.out.println(e.getMessage());
        }
    }

    public void setAdd(Biens b) {
        photos.setVisible(false);
        pieces.setVisible(false);
        labelPhotos.setVisible(false);
        labelPieces.setVisible(false);
        btnDPi.setVisible(false);
        btnAPi.setVisible(false);
        btnEPi.setVisible(false);
        btnAph.setVisible(false);
        btnDph.setVisible(false);
        this.currentBien = b;
    }
}
