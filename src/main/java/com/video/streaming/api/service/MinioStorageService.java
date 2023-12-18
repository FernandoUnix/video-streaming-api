package com.video.streaming.api.service;

import com.video.streaming.api.config.MinioConfig;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioStorageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.put-object-part-size}")
    private Long putObjectPartSize;

    public GetObjectResponse get(UUID uuid) throws Exception {
        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(MinioConfig.COMMON_BUCKET_NAME)
                        .object(uuid.toString())
                        .build()
        );
    }

    public void save(MultipartFile file, UUID uuid) throws Exception {
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(MinioConfig.COMMON_BUCKET_NAME)
                        .object(uuid.toString())
                        .stream(file.getInputStream(), file.getSize(), putObjectPartSize)
                        .build()
        );
    }

    public InputStream getInputStream(UUID uuid, long offset, long length) throws Exception {
        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(MinioConfig.COMMON_BUCKET_NAME)
                        .offset(offset)
                        .length(length)
                        .object(uuid.toString())
                        .build());
    }
}