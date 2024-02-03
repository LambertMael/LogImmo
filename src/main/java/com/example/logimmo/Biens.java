package com.example.logimmo;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class Biens {
    private int id;
    private String rue;
    private String cp;
    private String ville;
    private double prix;
    private int anneeConstru;
    private String description;
    private boolean libre;

    private ArrayList<Photo> photosBiens = new ArrayList<Photo>();


    private ArrayList<Piece> piecesBiens = new ArrayList<Piece>();

    public Biens(int id, String rue, String cp, String ville, double prix, int anneeConstru, String description, boolean libre) {
        this.id = id;
        this.rue = rue;
        this.cp = cp;
        this.ville = ville;
        this.prix = prix;
        this.anneeConstru = anneeConstru;
        this.description = description;
        this.libre = libre;
    }

    public Biens(){
        //hehehehehe
        this.id = -1;
    }

    public Biens(String rue, String cp, String ville, double prix, int anneeConstru, String description, boolean libre) {
        this.id = -1;
        this.rue = rue;
        this.cp = cp;
        this.ville = ville;
        this.prix = prix;
        this.anneeConstru = anneeConstru;
        this.description = description;
        this.libre = libre;
    }

    public int getId() {
        return id;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getAnneeConstru() {
        return anneeConstru;
    }

    public void setAnneeConstru(int anneeConstru) {
        this.anneeConstru = anneeConstru;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isLibre() {
        return libre;
    }

    public void setId(int _id){
        this.id = _id;
    }

    public void setLibre(boolean libre) {
        this.libre = libre;
    }

    public ArrayList<Photo> getPhotosBiens() {
        return photosBiens;
    }

    public void setPhotosBiens(ArrayList<Photo> photosBiens) {
        this.photosBiens = photosBiens;
    }

    public ArrayList<Piece> getPiecesBiens() {
        return piecesBiens;
    }

    public void setPiecesBiens(ArrayList<Piece> piecesBiens) {
        this.piecesBiens = piecesBiens;
    }

    // ------------méthodes------------------

    public void addPhoto(Photo p){
        this.photosBiens.add(p);
    }

    public void addPiece(Piece p){
        this.piecesBiens.add(p);
    }
    public void removePiece(Piece p){this.piecesBiens.remove(p);}//remove récursivemment

    public void removeAllPiece(){this.piecesBiens.clear();}//remove les equipements aussi là

    public boolean createAddPiece(Double surface, String libelle){
        //faut pas que le bien n'ai pas d'id
        if(this.id != -1) {
            this.piecesBiens.add(new Piece(surface, libelle, this.id));
            return true;
        }
        return false;
    }

    public float getSurface(){
        double surface = 0;
        for(Piece p : piecesBiens){
            surface += p.getSurface();
        }
        return (float) surface;
    }

    public Image getPreview(){
        //Pas forcement de photo
        if(photosBiens.size()==0){
            return null;
        } else {
            return this.photosBiens.get(0).getImage();
        }
    }

    @Override
    public String toString(){
        return this.rue;
    }




}
