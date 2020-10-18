package jp.mmall.service.Impl;

import com.google.common.collect.Lists;
import jp.mmall.service.IFileService;
import jp.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * Created by 2021 on 2019/10/18.
 */
@Service("iFileServiceImp")
public class IFileServiceImp implements IFileService{
    private static Logger logger= LoggerFactory.getLogger(IFileServiceImp.class);
/*
*
*参数：1多批的文件，2路径
* */
    public String upload(MultipartFile multipartFile,String path){
    String fileName=multipartFile.getOriginalFilename();
        //获取拓展名
        String fileExtendtionsNmae=fileName.substring(fileName.lastIndexOf(".")+1);
        //防止重名覆盖
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtendtionsNmae;
        logger.info("文件已经开始上传，上传的文件名{}，上传的文件路径{}，新上传的文件名{}",fileName,path,uploadFileName);
        File fileDir=new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile=new File(path,uploadFileName);
        try {
            //传入tomcat
            multipartFile.transferTo(targetFile);
            //上传到FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));//已经上传成功
            //删除upload文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件失败",e);
            return null;
        }
        return targetFile.getName();
    }
}
