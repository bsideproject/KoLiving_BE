package com.koliving.api.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class ObjectStorageService {

    private String bucket;
    private final AmazonS3Client client;

    public ObjectStorageService(@Value("${cloud.aws.s3.bucket}") String bucket,
                                AmazonS3Client client) {
        this.bucket = bucket;
        this.client = client;
    }

    public String getFileUrl(String filePath) {
        return client.getUrl(bucket, filePath).toString();
    }

    public boolean uploadFile(MultipartFile file, String dirName)  {
        String uploadPath = dirName + "/" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            client.putObject(
                    new PutObjectRequest(bucket, uploadPath, file.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch(IOException e) {
            log.error("Error "); // TODO error message
        } catch(AmazonS3Exception e) {
            log.error("Error "); // TODO error message
        } catch(SdkClientException e) {
            log.error("Error "); // TODO error message
        }

        return true;
    }

    public boolean deleteFile(String filePath) {
        try {
            client.deleteObject(bucket, filePath);
        } catch(AmazonS3Exception e) {
            log.error("Error "); // TODO error message
        } catch(SdkClientException e) {
            log.error("Error "); // TODO error message
        }

        return true;
    }
}
