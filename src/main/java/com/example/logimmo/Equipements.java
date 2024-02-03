package com.example.logimmo;

import java.util.ArrayList;

public class Equipements {
    private int id;
    private String libelle;

    private int id_pieces;

    private ArrayList<Photo> photosEquipements = new ArrayList<Photo>();

    //when we load
    public Equipements(int id, String libelle, int id_pieces) {
        this.id = id;
        this.libelle = libelle;
        this.id_pieces = id_pieces;
    }

    //when we upload
    public Equipements(String libelle, int id_pieces) {
        this.id = -1;
        this.libelle = libelle;
        this.id_pieces = id_pieces;
    }

    public Equipements(int id_pieces){
        this.id = -1;
        this.id_pieces = id_pieces;
    }


    public int getId() {
        return id;
    }

    public void setId(int _id) {
        this.id=_id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getId_pieces() {
        return id_pieces;
    }

    public void setId_pieces(int id_pieces) {
        this.id_pieces = id_pieces;
    }


    public ArrayList<Photo> getPhotosEquipements() {
        return photosEquipements;
    }

    public void setPhotosEquipements(ArrayList<Photo> photosEquipements) {
        this.photosEquipements = photosEquipements;
    }

    //---------méthodes--------

    @Override
    public String toString(){
        return this.libelle;
    }


    public void addPhoto(Photo p){
        this.photosEquipements.add(p);
    }
}
