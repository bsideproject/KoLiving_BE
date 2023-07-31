package com.koliving.api.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.koliving.api.properties.ObjectStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final AmazonS3Client client;
    private final ObjectStorageProperties objectStorageProperties;

    public String getFileUrl(String filePath) {
        String string = null;
        try {
            string = client.getUrl(getBucketName(), filePath).toString();;
        } catch (SdkClientException e) {
            log.error("Error "); // TODO error handling
        }

        return string;
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
            log.error("Error "); // TODO error handling
        } catch(AmazonS3Exception e) {
            log.error("Error "); // TODO error handling
        } catch(SdkClientException e) {
            log.error("Error "); // TODO error handling
        }

        return true;
    }

    public boolean deleteFile(String filePath) {
        try {
            client.deleteObject(getBucketName(), filePath);
        } catch(AmazonS3Exception e) {
            log.error("Error "); // TODO error handling
        } catch(SdkClientException e) {
            log.error("Error "); // TODO error handling
        }

        return true;
    }

    private String getBucketName() {
        return objectStorageProperties.getS3().getBucket();
    }
}
