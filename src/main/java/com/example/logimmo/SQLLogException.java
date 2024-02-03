package com.example.logimmo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class SQLLogException extends Exception{

    private LocalDateTime date;
    private int id;

    private String app = "TP Agence Immo (FX)";


    public SQLLogException(String message) {
        super(message);
        this.date = LocalDateTime.now();
        this.uploadLog();
    }

    private void uploadLog(){
        //DB de logs
        String LOGS_DB_URL = "jdbc:mysql://192.168.1.27/logsTpImmo"; //jdbc:mysql://172.19.0.38/logsTpImmo
        String LOGS_USER = "logsTpImmo";
        String LOGS_PASS = "qtv532ERaRnA2p22";

        try {
            Connection conn = DriverManager.getConnection(LOGS_DB_URL, LOGS_USER, LOGS_PASS);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `Logs`(`message`, `stackTrace`, `date`, `app`) VALUES ( ? , ? , ? , ?)");
            StringWriter errors = new StringWriter();
            this.printStackTrace(new PrintWriter(errors));
            stmt.setString(1,this.getMessage());
            stmt.setString(2,errors.toString().substring(0,2000));
            stmt.setString(3,this.getDate().toString());
            stmt.setString(4,app);
            stmt.execute();
            //this.getMessage()      +     errors     +       this.getDate()
        } catch (Exception e){
            System.out.println("L'upload de la log s'est mal passé : ");
            e.printStackTrace();
        }
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(int _id){
        this.id = _id;
    }

    public int getId(){
        return this.id;
    }


}
