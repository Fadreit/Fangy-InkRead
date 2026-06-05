package com.fadreit.inkreadboot.Util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.fadreit.inkreadboot.Config.OssConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
public class AliyunOSSUtil {

    private static OssConfig ossConfig;

    private final OssConfig config;

    public AliyunOSSUtil(OssConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        ossConfig = this.config;
    }

    private static OSS createOssClient() {
        return new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
    }

    public static String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        if (!isValidImageType(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }

        String fileName = generateFileName(fileExtension);

        OSS ossClient = null;
        try {
            ossClient = createOssClient();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            ossClient.putObject(
                    ossConfig.getBucketName(),
                    fileName,
                    file.getInputStream(),
                    metadata
            );

            String fileUrl = ossConfig.getDomain() + "/" + fileName;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public String uploadFile(InputStream inputStream, String fileName, long size) {
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为空");
        }

        String fileExtension = getFileExtension(fileName);
        if (!isValidImageType(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }

        String objectName = generateFileName(fileExtension);

        OSS ossClient = null;
        try {
            ossClient = createOssClient();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);

            ossClient.putObject(
                    ossConfig.getBucketName(),
                    objectName,
                    inputStream,
                    metadata
            );

            String fileUrl = ossConfig.getDomain() + "/" + objectName;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String fileName = extractFileNameFromUrl(fileUrl);
        if (fileName == null) {
            return;
        }

        OSS ossClient = null;
        try {
            ossClient = createOssClient();
            ossClient.deleteObject(ossConfig.getBucketName(), fileName);
            log.info("文件删除成功: {}", fileName);
        } catch (Exception e) {
            log.error("文件删除失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private static boolean isValidImageType(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension) ||
                "png".equals(extension) || "gif".equals(extension) ||
                "bmp".equals(extension) || "webp".equals(extension);
    }

    private static String generateFileName(String extension) {
        String datePath = java.time.YearMonth.now().toString().replace("-", "/");
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "covers/" + datePath + "/" + uuid + "." + extension;
    }

    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(ossConfig.getDomain())) {
            return null;
        }
        return fileUrl.substring(ossConfig.getDomain().length() + 1);
    }
}

