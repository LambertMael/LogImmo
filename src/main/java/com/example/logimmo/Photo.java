package com.example.logimmo;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Photo {
    private String remotePath = "http://192.168.1.27/tpImmo/"; // mettre l'adresse du docker
    private int id;
    private int id_pieces;
    private int id_biens;
    private int id_equipements;
    private Image image;
    private String chemin;


    public Photo(int id, int id_pieces, int id_biens, int id_equipements, String chemin) {
        //constructeur pour quand on charge une photo
        this.id = id;
        this.id_pieces = id_pieces;
        this.id_biens = id_biens;
        this.id_equipements = id_equipements;
        this.chemin = chemin;
        //image ici pour précharger l'image comme ça elle est déjà download
        this.image = new Image(remotePath+chemin, 150, 150, true, false);
    }

    public Photo(int id_pieces, int id_biens, int id_equipements, String chemin) {
        this.id = -1;
        this.id_pieces = id_pieces;
        this.id_biens = id_biens;
        this.id_equipements = id_equipements;
        this.chemin = chemin;
        this.image = new Image(chemin, 150, 150, true, false);
        // faire gaffe ici chemin et pas remote ???
    }

    public Photo(int id_biens){
        //constructeur pour quand on ajoute une photo depuis un addBien
        this.id = -1;
        this.id_biens = id_biens;
        this.id_pieces = -1;
        this.id_equipements = -1;
    }

    public Photo(int id_biens, int id_pieces){
        //constructeur pour quand on ajoute une photo depuis un addPiece
        this.id = -1;
        this.id_biens = id_biens;
        this.id_pieces = id_pieces;
        this.id_equipements = -1;
    }

    public Photo(int id_biens, int id_pieces, int id_equipements){
        //constructeur pour quand on ajoute une photo depuis un addPiece
        this.id = -1;
        this.id_biens = id_biens;
        this.id_pieces = id_pieces;
        this.id_equipements = id_equipements;
    }


    public int getId() {
        return id;
    }
    public void setId(int _id){
        this.id = _id;
    }

    public int getId_pieces() {
        return id_pieces;
    }

    public void setId_pieces(int id_pieces) {
        this.id_pieces = id_pieces;
    }

    public int getId_biens() {
        return id_biens;
    }

    public void setId_biens(int id_biens) {
        this.id_biens = id_biens;
    }

    public int getId_equipements() {
        return id_equipements;
    }

    public void setId_equipements(int id_equipements) {
        this.id_equipements = id_equipements;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImage(String path){
        this.image = new Image(remotePath+chemin, 150, 150, true, false);
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    // ---- méthodes ----

    @Override
    public String toString(){
        return this.chemin;
    }
}
