package com.mmall.service.impl;

import com.mmall.service.IFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
        //将文件上传到指定文件
        File targetFile=new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件上传成功

            //todo 将targetFile上传到我们的FTP服务器上

            //todo 上传完之后，删除upload下面的文件


        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }

        return targetFile.getName();
    }
//    //测试
//    public static void main(String [] arg){
//        String fileName="abc.jpg";
//        System.out.println(fileName.substring(fileName.lastIndexOf(".")+1));
//    }

}
