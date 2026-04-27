package com.yunzhou.console.service;

import com.yunzhou.console.handler.MinioHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioHandler minioHandler;

    public String upload(MultipartFile file, String directory) {
        try {
            return minioHandler.uploadFile(file, directory);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    public String getFileUrl(String objectName) {
        try {
            return minioHandler.getFileUrl(objectName);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            throw new RuntimeException("获取文件URL失败: " + e.getMessage());
        }
    }

    public void delete(String objectName) {
        try {
            minioHandler.deleteFile(objectName);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }
}
