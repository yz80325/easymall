package jp.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by 2021 on 2019/10/18.
 */
public class FTPUtil {

    private static  final Logger logger= LoggerFactory.getLogger(FTPUtil.class);
    private static String severIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String severUsername=PropertiesUtil.getProperty("ftp.user");
    private static String severPassword=PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    //端口
    private int port;
    private String username;
    private String pass;
    private FTPClient ftpClient;
    //外部掉的传数据方法
    public static boolean uploadFile(List<File>fileList) throws IOException {
        FTPUtil ftpUtil=new FTPUtil(severIp,severUsername,severPassword,21);
        logger.info("开始连接服务器");
        boolean connectFTP=ftpUtil.uploadFile("img",fileList);
        logger.info("结束上传，上传结果{}",connectFTP);
        return connectFTP;
    }

    //上传数据
    private boolean uploadFile(String remotePath,List<File>fileList) throws IOException {
        boolean uploadFile=true;
        FileInputStream fileInputStream=null;
        //连接服务器
        if (connectServer(this.ip,this.username,this.pass)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File filename:fileList){
                    fileInputStream=new FileInputStream(filename);
                    ftpClient.storeFile(filename.getName(),fileInputStream);
                }
            } catch (IOException e) {
                logger.error("上传数据异常",e);
                uploadFile=false;
                e.printStackTrace();
            }finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }else {
            logger.error("连接失败");
            uploadFile=false;
        }
        return uploadFile;
    }
    //连接服务器
    private boolean connectServer(String ip,String user,String psw){
       boolean isSuccess=false;
        ftpClient=new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess=ftpClient.login(user,psw);
        } catch (IOException e) {
            logger.error("ftp连接失败",e);
        }
        return isSuccess;
    }


    //封装
    public FTPUtil(String ip,String username,String pass,int port){
        this.ip=ip;
        this.username=username;
        this.pass=pass;
        this.port=port;
    }
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
