package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.List;

public class FTPUtil {

    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.pwd=pwd;
    }
    public static boolean uploadFile(List<File> fileList){
        FTPUtil ftpUtil=new FTPUtil(ftpIp,21,ftpUser,ftpPass);

    }

    private boolean uploadFile(){
        //未完待续
    }
    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
