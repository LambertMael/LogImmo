package com.example.logimmo;

import javafx.scene.image.Image;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class DataBase {
    //DB de base pour gérer les biens
    private static final String DB_URL = "jdbc:mysql://192.168.1.27:3306/tpImmo"; //jdbc:mysql://172.19.0.38/tpImmo
    private static final String USER = "tpImmo";
    private static final String PASS = "v@$4E27q!tNfbTgo";
    private ArrayList<Biens> lesBiens = new ArrayList<Biens>();

    public ArrayList<Biens> init(){
        //load les biens
        try{
            loadBiens(-1);
        } catch (SQLLogException e){
            System.out.println(e.getMessage());
        }
        return lesBiens;
    }
    //on va load les biens selon un offset et limit
    //et on load récursivement
    public void loadBiens(int offset) throws SQLLogException{
        //on cherche les x premiers biens
        // on load les pièces de chaque bien, les équipements de chaque pièce
        //une fois tout chargé, on telecharge les image, qu'on précharge en tant qu'image
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String str;
            //on dit aurevoir la pagination, j'ai pas le temps

            if(offset==-1){
                str = "SELECT * FROM biens";
            } else {
                str = "SELECT * FROM biens LIMIT " + offset + ", "+ (offset+5);
            }

            ResultSet rs = stmt.executeQuery(str);
            while (rs.next()) {
                //Ici on fait un bien
                Biens b = new Biens(rs.getInt("id"),rs.getString("rue"),rs.getString("cp"),rs.getString("ville"),rs.getDouble("prix"),rs.getInt("anneeConstru"),rs.getString("description"),rs.getBoolean("libre"));

                //faut chopper les pièces du bien
                Statement stmtPiece = conn.createStatement();
                ResultSet rsPiece = stmtPiece.executeQuery("SELECT * FROM pieces WHERE id_biens="+b.getId());
                while(rsPiece.next()){
                    //pièces du bien
                    Piece p = new Piece(rsPiece.getInt("id"),rsPiece.getFloat("surface"),rsPiece.getString("libelle"), rsPiece.getInt("id_biens"));

                    //mtn faut les équipements de la pièce
                    Statement stmtEquipement = conn.createStatement();
                    ResultSet rsEquipement = stmtEquipement.executeQuery("SELECT * FROM equipements WHERE id_pieces="+p.getId());
                    while(rsEquipement.next()){
                        //équipements de la pièce
                        Equipements equ = new Equipements(rsEquipement.getInt("id"),rsEquipement.getString("libelle"),rsEquipement.getInt("id_pieces"));
                        p.addEquipement(equ);
                    }
                    //on peut enfin ajouter la pièce au bien
                    b.addPiece(p);
                }
                lesBiens.add(b);
            }
            loadPhotos();

            try{
                rs.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                stmt.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                conn.close();
            } catch (SQLException exc) { /* Ignored */}

        } catch (SQLException e) {
            throw new SQLLogException("Erreur lors du chargement des biens. " + e.getMessage());
        }
    }
    public void loadPhotos() throws SQLLogException{
        //on se base sur les biens déjà chargé
        for(Biens b : lesBiens){
            try{
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();
                String str = "SELECT * FROM photos WHERE id_biens = "+b.getId();

                ResultSet rs = stmt.executeQuery(str);
                while (rs.next()) {
                    Photo p = new Photo(rs.getInt("id") , rs.getInt("id_pieces") , rs.getInt("id_biens") , rs.getInt("id_equipements") , rs.getString("chemin"));
                    b.addPhoto(p);
                    int i = rs.getInt("id_pieces");
                    //ici fauit load
                    if(rs.getInt("id_pieces")!=0){
                        for(Piece pi : b.getPiecesBiens()){
                            if(pi.getId() == rs.getInt("id_pieces")){
                                pi.addPhoto(p);
                                if(rs.getInt("id_equipements")!=0){
                                    for(Equipements eq : pi.getEquipementsPiece()){
                                        if(eq.getId() == rs.getInt("id_equipements")){
                                            eq.addPhoto(p);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                try{
                    rs.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    stmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}
            } catch (Exception e){
                throw new SQLLogException("Erreur lors du chargement d'une photo depuis la base.");
            }
        }
    }
    public boolean connexionUser(String login, String pass) throws SQLLogException{
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement  pstmt = conn.prepareStatement("SELECT password FROM commercial WHERE login = ?");
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                //bon login
                if(BCrypt.checkpw(pass,rs.getString("password"))){
                    //bon mot de passe
                    return true;
                }
            }


            try{
                rs.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                pstmt.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                conn.close();
            } catch (SQLException exc) { /* Ignored */}

        } catch (Exception e){
            throw new SQLLogException("Erreur lors de la connexion d'un utilisateur.");
        }
        return false;
    }

    public void uploadBien(Biens b) throws SQLLogException{
        if(b.getId() != -1){
            try{
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                //on ajoute le bien
                PreparedStatement pstmt = conn.prepareStatement("UPDATE `biens` SET `rue`= ? ,`cp`= ? ,`ville`= ? ,`prix`= ? ,`anneeConstru`= ? ,`description`= ? ,`libre`= ? WHERE `id`= ? ");
                pstmt.setString(1, b.getRue());
                pstmt.setString(2, b.getCp());
                pstmt.setString(3, b.getVille());
                pstmt.setDouble(4, b.getPrix());
                pstmt.setInt(5, b.getAnneeConstru());
                pstmt.setString(6, b.getDescription());
                pstmt.setBoolean(7, b.isLibre());
                pstmt.setInt(8, b.getId());
                pstmt.execute();


                try {
                    pstmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}

            } catch (SQLException exc){
                throw new SQLLogException("Erreur lors de l'upload d'une pièce. " + exc.getMessage());
            }
        } else {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                //on ajoute le bien
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `biens`(`rue`, `cp`, `ville`, `prix`, `anneeConstru`, `description`, `libre`) VALUES ( ? , ? , ? , ? , ? , ? , ? )");
                pstmt.setString(1, b.getRue());
                pstmt.setString(2, b.getCp());
                pstmt.setString(3, b.getVille());
                pstmt.setDouble(4, b.getPrix());
                pstmt.setInt(5, b.getAnneeConstru());
                pstmt.setString(6, b.getDescription());
                pstmt.setBoolean(7, b.isLibre());
                pstmt.execute();

                //mtn récupe de l'id du bien pour add les pièces
                pstmt = conn.prepareStatement("SELECT id FROM biens WHERE rue = ? AND cp = ? AND ville = ? LIMIT 1");
                pstmt.setString(1, b.getRue());
                pstmt.setString(2, b.getCp());
                pstmt.setString(3, b.getVille());
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()){
                    b.setId(rs.getInt("id"));
                }


                try{
                    rs.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    pstmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}

            } catch (SQLException e) {
                throw new SQLLogException("Erreur lors de l'upload d'un bien. " + e.getMessage());
            }
        }
    }

    public void uploadPiece(Piece p) throws SQLLogException{
        //on prend le bien en param
        if(p.getId() != -1){
            //piece a déja été upload, il a déjà un id
            // donc update
            try{
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                //on ajoute l'equipement
                PreparedStatement pstmt = conn.prepareStatement("UPDATE `pieces` SET `surface`= ? ,`libelle`= ? ,`id_biens`= ? WHERE `id`= ? ");
                pstmt.setDouble(1, p.getSurface());
                pstmt.setString(2, p.getLibelle());
                pstmt.setInt(3, p.getId_biens());
                pstmt.setInt(4, p.getId());
                pstmt.execute();


                try {
                    pstmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}

            } catch (SQLException exc){
                throw new SQLLogException("Erreur lors de l'upload d'une pièce. " + exc.getMessage());
            }
        } else {
            try{

                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                //on ajoute la pièce
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `pieces`( `surface`, `libelle`, `id_biens`) VALUES ( ? , ? , ? )");
                pstmt.setDouble(1, p.getSurface());
                pstmt.setString(2, p.getLibelle());
                pstmt.setInt(3, p.getId_biens());
                pstmt.execute();

                //on récupe l'id de la pièce pour add les équipements
                pstmt = conn.prepareStatement("SELECT id FROM pieces WHERE surface = ? AND libelle = ? AND id_biens = ? LIMIT 1");
                pstmt.setDouble(1, p.getSurface());
                pstmt.setString(2, p.getLibelle());
                pstmt.setInt(3, p.getId_biens());
                ResultSet rs = pstmt.executeQuery();
                //le rs next c'est nul, il peut prendre du temps et bloquer mon programme, donc meme si y a qu'un truc on fait un while
                while(rs.next()){
                    p.setId(rs.getInt("id"));
                }

                try{
                    rs.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    pstmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}

            } catch (SQLException e){
                throw new SQLLogException("Erreur lors de l'upload d'une pièce. " + e.getMessage());
            }
        }
    }

    public void uploadEquipement(Equipements e) throws SQLLogException{
        //on prend le bien en param

        if(e.getId() != -1){
            //Equipement a déja été upload, il a déjà un id
            // donc update
            try{
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                //on ajoute la equipements
                PreparedStatement pstmt = conn.prepareStatement("UPDATE `equipements` SET `libelle`= ? ,`id_pieces`= ? WHERE `id`= ?");
                pstmt.setString(1, e.getLibelle());
                pstmt.setInt(2, e.getId_pieces());
                pstmt.setInt(3, e.getId());
                pstmt.execute();

                try {
                    pstmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}

            } catch (SQLException exc){
                throw new SQLLogException("Erreur lors de l'upload d'une pièce. " + exc.getMessage());
            }
        } else {
            try{
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //on ajoute la equipements
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `equipements`( `libelle`, `id_pieces`) VALUES ( ? , ? )");
                pstmt.setString(1, e.getLibelle());
                pstmt.setInt(2, e.getId_pieces());
                pstmt.execute();

                //on récupe l'id de l'équipements pour add les photos
                //on order par DESC au cas où que dans la pièce il y est plusieurs fois un équipement avec le meme libellé, en faisant ça ça nous assure de prendre le dernier
                pstmt = conn.prepareStatement("SELECT id FROM equipements WHERE libelle = ? AND id_pieces = ? ORDER BY id DESC LIMIT 1");
                pstmt.setString(1, e.getLibelle());
                pstmt.setInt(2, e.getId_pieces());
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()){
                    e.setId(rs.getInt("id"));
                }

                try{
                    rs.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    pstmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}

            } catch (SQLException exc){
                throw new SQLLogException("Erreur lors de l'upload d'une pièce. " + exc.getMessage());
            }
        }
    }

    public void uploadPhoto(Photo p) throws SQLLogException{
        //on va se mettre d'accord
        //si l'image n'a pas encore été upload, alors son attribut chemin sera le chemin local, quand elle est upload ce sera son chemin sur la machine
        if(p.getId() != -1){
            //a déja été upload, il a déjà un id
            throw new SQLLogException("Erreur : tentative d'upload d'une photo ayant déjà un id.");
        } else {
            try{
                FTPManager ft = new FTPManager();

                if(ft.init()){
                    System.out.println("On est co");
                } else {
                    System.out.println("On est PAS co");
                }
                //un uuid pour le nom du fichier (qu'il soit unique)
                String uuid = UUID.randomUUID().toString();
                String newChemin = ft.upload(new File(p.getChemin()), uuid);

                //le ft.upload renvoie le nouveau nom du fichier sur le serveur, si qqch se passe mal alors ce sera une chaine vide
                if(newChemin.equals("")){
                    throw new SQLLogException("Erreur : échec de l'upload de la photo (FTP).");
                } else {
                    p.setChemin(newChemin);
                    p.setImage(newChemin);
                }
                ft.close();

                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `photos`( `chemin`, `id_pieces`, `id_biens`, `id_equipements`) VALUES ( ? , ? , ? , ?)");
                pstmt.setString(1, p.getChemin());
                if(p.getId_pieces() != -1){
                    pstmt.setInt(2, p.getId_pieces());
                } else {
                    pstmt.setObject(2, null);
                }

                pstmt.setInt(3, p.getId_biens());

                if(p.getId_equipements() != -1){
                    pstmt.setInt(4, p.getId_equipements());
                } else {
                    pstmt.setObject(4, null);
                }

                pstmt.execute();

                pstmt = conn.prepareStatement("SELECT id FROM photos WHERE chemin = ? AND id_biens = ? LIMIT 1");
                pstmt.setString(1, p.getChemin());
                pstmt.setInt(2, p.getId_biens());
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()){
                    p.setId(rs.getInt("id"));
                }


                try{
                    rs.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    pstmt.close();
                } catch (SQLException exc) { /* Ignored */}
                try {
                    conn.close();
                } catch (SQLException exc) { /* Ignored */}

            } catch (Exception exc){
                throw new SQLLogException("Erreur lors de l'upload d'une photo. " + exc.getMessage());
            }
        }
    }

    public void deletePhoto(Photo p) throws SQLLogException{
        try{
            //on la supprime de la base et après on la supprime du serveur
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `photos` WHERE id = ? ");
            //ici on est sur que la photo a un id car quand elle est créée elle est upload CF uploadPhoto()
            pstmt.setInt(1, p.getId());
            pstmt.execute();

            FTPManager ft = new FTPManager();
            ft.init();
            ft.delete(p.getChemin());
            ft.close();

            try {
                pstmt.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                conn.close();
            } catch (SQLException exc) { /* Ignored */}

        } catch (Exception e){
            throw new SQLLogException("Erreur lors de la supression d'une photo dans la base.");
        }
    }

    public void deleteEquipement(Equipements e, Piece p, Biens b) throws SQLLogException{
        try{
            //on la supprime de la base et après on la supprime du serveur
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //on supprime les photos et c'est pour ça qu'il faut les p et b

            //on delete les photos
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `photos` WHERE id_equipements = ? ");
            pstmt.setInt(1, e.getId());
            pstmt.execute();

            //on delete l'equipement
            pstmt = conn.prepareStatement("DELETE FROM `equipements` WHERE id = ? ");
            pstmt.setInt(1, e.getId());
            pstmt.execute();

            FTPManager ft = new FTPManager();
            ft.init();

            for(Photo ph : e.getPhotosEquipements()){
                p.getPhotosPieces().remove(ph);
                b.getPhotosBiens().remove(ph);
                //on supprime les fichiers des photos
                ft.delete(ph.getChemin());
            }

            ft.close();
            p.getEquipementsPiece().remove(e);
            e.getPhotosEquipements().clear();

            try {
                pstmt.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                conn.close();
            } catch (SQLException exc) { /* Ignored */}

        } catch (Exception ex){
            throw new SQLLogException("Erreur lors de la supression d'un equipement dans la base.");
        }
    }

    public void deletePiece(Piece p, Biens b) throws SQLLogException{
        try{
            //on la supprime de la base et après on la remove du serveur
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //on supprime les photos et c'est pour ça qu'il faut le b

            //on delete les photos
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `photos` WHERE id_pieces = ? ");
            pstmt.setInt(1, p.getId());
            pstmt.execute();

            //on delete les equipements
            pstmt = conn.prepareStatement("DELETE FROM `equipements` WHERE id_pieces = ? ");
            pstmt.setInt(1, p.getId());
            pstmt.execute();

            //on delete la pièce
            pstmt = conn.prepareStatement("DELETE FROM `pieces` WHERE id = ? ");
            pstmt.setInt(1, p.getId());
            pstmt.execute();

            FTPManager ft = new FTPManager();
            ft.init();
            for(Photo ph : p.getPhotosPieces()){
                //on remove les photos de la pièce dans la liste du bien ça c'est juste pour le plaisir d'un truc dynamique
                b.getPhotosBiens().remove(ph);
                //on supprime les fichiers des photos
                ft.delete(ph.getChemin());
            }
            ft.close();
            b.getPiecesBiens().remove(p);


            try {
                pstmt.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                conn.close();
            } catch (SQLException exc) { /* Ignored */}



        } catch (Exception ex){
            throw new SQLLogException("Erreur lors de la supression d'une photo dans la base.");
        }
    }

    public void deleteBien(Biens b) throws SQLLogException{
        try{
            //on la supprime de la base et après on la remove du serveur
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //on supprime les photos et c'est pour ça qu'il faut le b

            //on delete les photos
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `photos` WHERE id_biens = ? ");
            pstmt.setInt(1, b.getId());
            pstmt.execute();

            //on delete les equipements
            for(Piece p : b.getPiecesBiens()){
                pstmt = conn.prepareStatement("DELETE FROM `equipements` WHERE id_pieces = ? ");
                pstmt.setInt(1, p.getId());
                pstmt.execute();
            }

            //on delete les pièces
            pstmt = conn.prepareStatement("DELETE FROM `pieces` WHERE id_biens = ? ");
            pstmt.setInt(1, b.getId());
            pstmt.execute();

            //on delete le bien
            pstmt = conn.prepareStatement("DELETE FROM `biens` WHERE id = ? ");
            pstmt.setInt(1, b.getId());
            pstmt.execute();

            //on supprime les fichiers des photos
            FTPManager ft = new FTPManager();
            ft.init();
            for(Photo ph : b.getPhotosBiens()){
                ft.delete(ph.getChemin());
            }


            //maintenant on ferme tout !
            ft.close();
            try {
                pstmt.close();
            } catch (SQLException exc) { /* Ignored */}
            try {
                conn.close();
            } catch (SQLException exc) { /* Ignored */}


        } catch (Exception ex){
            throw new SQLLogException("Erreur lors de la supression d'une photo dans la base.");
        }
    }

}
