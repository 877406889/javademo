package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
     /**
     * 将上传的文件名返回
     * @return
     */
    public String upload(MultipartFile file,String path){
        String fileName=file.getOriginalFilename();
        //扩展名
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        //防止用户上传的文件文件名相同
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名：{},上传的路径：{}，新文件名：{}",fileName,path,uploadFileName);
        /*
        创建目录如果先查看是否存在该目录文件夹
        如果不存在则赋予写的权限然后创建它
         */
        File fileDir=new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdir();
        }
        //将文件上传到指定文件夹
        File targetFile=new File(path,uploadFileName);
        try {
            //文件上传成功
            file.transferTo(targetFile);
            //上传到ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }

        return targetFile.getName();
    }

}
