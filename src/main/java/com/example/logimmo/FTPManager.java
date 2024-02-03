package com.example.logimmo;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class FTPManager {
    private String serveur = "192.168.1.27"; //au lycée 172.19.0.38
    private String user = "sio";
    private String password = "0550002D";
    private int port = 21;
    private String remotePath = "./tpImmo/"; //./tpImmo/

    private String clientPath = "C:\\_tmp_\\"; // il faudra vérif l'existence de ce dossier, sinon le créer lors du constructeur
    FTPClient ftpClient;

    public FTPManager(){
        ftpClient = new FTPClient();
    }

    public boolean init() throws IOException {
        this.ftpClient.connect(serveur, port);
        boolean b = this.ftpClient.login(user, password );
        this.ftpClient.enterLocalPassiveMode();
        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return b;
    }

    public boolean upload(File file) throws IOException{
        //classic upload
        String chemin = remotePath + file.getName();
        InputStream inputStream = new FileInputStream(file);
        boolean res = ftpClient.storeFile(chemin, inputStream);
        inputStream.close();
        return res;
    }

    public String upload(File file, String uuid) throws IOException{

        String name = file.getName();
        String[] exts = name.split("\\.");

        String ext = exts[exts.length-1];

        String chemin = remotePath + uuid + "." + ext;
        InputStream inputStream = new FileInputStream(file);

        if(ftpClient.storeFile(chemin, inputStream)){
            inputStream.close();
            return uuid+"."+ext;
        } else {
            inputStream.close();
            return "";
        }
    }

    public void delete(String path) throws IOException{
        ftpClient.deleteFile(remotePath+path);
    }

    public void download(String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(clientPath + filename);
        this.ftpClient.retrieveFile(remotePath + filename, fos);
    }

    public void close() throws IOException{
        if (this.ftpClient.isConnected()) {
            this.ftpClient.logout();
            this.ftpClient.disconnect();
        }
    }


}
