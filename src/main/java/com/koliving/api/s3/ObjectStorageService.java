package com.koliving.api.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.koliving.api.properties.ObjectStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class ObjectStorageService {

    private final ObjectStorageProperties objectStorageProperties;
    private final AmazonS3Client client;

    public ObjectStorageService(ObjectStorageProperties objectStorageProperties, AmazonS3Client client) {
        this.objectStorageProperties = objectStorageProperties;
        this.client = client;
    }

    public String getFileUrl(String filePath) {
        return client.getUrl(getBucketName(), filePath).toString();
    }

    public boolean uploadFile(MultipartFile file, String dirName)  {
        String uploadPath = dirName + "/" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            client.putObject(
                    new PutObjectRequest(getBucketName(), uploadPath, file.getInputStream(), metadata)
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
            client.deleteObject(getBucketName(), filePath);
        } catch(AmazonS3Exception e) {
            log.error("Error "); // TODO error message
        } catch(SdkClientException e) {
            log.error("Error "); // TODO error message
        }

        return true;
    }

    private String getBucketName() {
        return objectStorageProperties.getS3().getBucket();
    }
}
