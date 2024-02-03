package com.example.logimmo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AppController {
    @FXML
    private ListView<Biens> listView;
    @FXML
    private Label label;
    @FXML
    private ImageView image;
    public Biens currentBien;

    public void initialize() {
        DataBase db = new DataBase();
        listView.getItems().addAll(db.init());//dans le addAll il faut mettre les biens
        //permet à chaque lcique d'afficher le nom du bien en haut
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Biens>() {
            @Override
            public void changed(ObservableValue<? extends Biens> observableValue, Biens bien, Biens t1) {
                currentBien = listView.getSelectionModel().getSelectedItem();
                label.setText(currentBien.getDescription());
                if(currentBien.getPreview() != null){
                    image.setImage(currentBien.getPreview());
                } else {
                    image.setImage(null);
                }
            }
        });
        //au début je voulais afficher toutes les images mais ça lag trop

    }

    public void deleteBienPopup(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Etes vous sûr de vouloir supprimer " + listView.getSelectionModel().getSelectedItem() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try{
                Biens b = listView.getSelectionModel().getSelectedItem();
                listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
                //supprimer dans la base aussi
                DataBase db = new DataBase();
                db.deleteBien(b);

            } catch (SQLLogException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void addBienPopup() throws IOException{
        try{
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addBien-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            s.setTitle("Ajout d'un bien");
            s.setScene(scene);
            s.initModality(Modality.NONE);
            s.setResizable(false);
            s.getIcons().add(new Image("logo.png"));
            s.show();

            AddBienController ad = (AddBienController) fxmlLoader.getController();

            Biens b = new Biens();
            s.setOnHidden(e -> {
                if(b.getId()!=-1){
                    listView.getItems().add(b);
                }
            });

            ad.setAdd(b);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void editBienPopup() throws IOException{
        try{
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addBien-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            s.setTitle("Modification d'un bien");
            s.setScene(scene);
            s.initModality(Modality.NONE);
            s.setResizable(false);
            s.getIcons().add(new Image("logo.png"));
            s.show();

            //pour que l'affichage soit dynamique
            s.setOnHidden(e -> {
                listView.refresh();
            });

            AddBienController ad = (AddBienController) fxmlLoader.getController();
            ad.setAll(currentBien);
        } catch (IOException e){
            e.printStackTrace();
        }

        //rajouter un listener pour que quand on quitte ça recharge bien
    }
}
