package gradle.demo.service;

import gradle.demo.util.file.UploadObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Author JingQ on 2017/12/25.
 */
public interface FileManageService {

    /**
     * 上传文件
     * @param object    文件对象
     * @return          上传路径
     */
    String upload(UploadObject object);

    /**
     * 下载文件
     * @param object    文件对象
     * @return          文件输入流
     */
    InputStream getInputStreamFromObject(UploadObject object);

    /**
     * 删除文件
     * @param objectKey bucketName+"/"+路径+全名
     */
    void deleteObject(String objectKey);

    /**
     * 上传文件，获得外链地址
     * @param file      文件
     * @param dirPath   文件目录
     * @return          外链
     */
    String upload(MultipartFile file, String dirPath);
}
