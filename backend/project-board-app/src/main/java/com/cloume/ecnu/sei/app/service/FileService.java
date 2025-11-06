package com.cloume.ecnu.sei.app.service;

import io.minio.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Log4j2
@Service
public class FileService {
    @Value("${minio.endpoint:http://127.0.0.1:9004}")
    private String endpoint;

    @Value("${minio.accessKey:xgb_sca}")
    private String accessKey;

    @Value("${minio.accessValue:pl,okm123}")
    private String accessValue;

    @Value("${minio.bucket:sca}")
    private String bucket;

    @Value(("#{'${minio.fileType}'.split(',')}"))
    private List<String> whiteFileTypeList;

    private MinioClient minioClient;

    @Value("${templateUrl.url}")
    private String templateUrl;

    @Value(("#{'${minio.imgType}'.split(',')}"))
    private List<String> imgTypeList;

    @Value("${templateUrl.fileUrl}")
    private String templateFileUrl;

    /**
     * 将文件上传至minio
     *
     * @param fileInputStream
     * @param fileName        上传文件名
     * @return
     */
    public ObjectWriteResponse minioUpload(InputStream fileInputStream, String fileName) {
        try {
            if (minioClient == null) {
                minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, accessValue).build();
                boolean bucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!bucketExist) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("minio init success, bucket: {}", bucket);
                }
            }
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(fileInputStream, -1, 10485760)
                            .build());
            return objectWriteResponse;
        } catch (Exception e) {
            log.error("文件上传失败：{}", e);
            return null;
        }
    }

    /**
     * 根据文件名从minio获取InputStream
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public InputStream getFileInputStream(String fileName) throws Exception {
        if (minioClient == null) {
            minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, accessValue).build();
            boolean bucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("minio init success, bucket: {}", bucket);
            }
        }
        InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(fileName).build());
        return inputStream;
    }
}
