package com.koliving.api.file.infra;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.config.CloudConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.koliving.api.base.ServiceError.UPLOAD_FAIL;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class NCPRepository implements FileRepository {
    private static final String DEFAULT_PATH = "images";
    private final AmazonS3 amazonS3;
    private final CloudConfig config;

    @Override
    public String upload(MultipartFile file) {
        try {
            String filename = getFilePathname();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            AccessControlList acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

            final PutObjectRequest putObjectRequest = new PutObjectRequest(
                config.getBucketName(),
                filename,
                file.getInputStream(),
                objectMetadata
            );

            putObjectRequest
                .withAccessControlList(acl);

            amazonS3.putObject(putObjectRequest);

            return amazonS3.getUrl(config.getBucketName(), filename)
                .toString();
        } catch (SdkClientException | IOException  e) {
            log.error("file upload failed: ", e);
            throw new KolivingServiceException(UPLOAD_FAIL);
        }
    }

    private String getFilePathname() {
        return DEFAULT_PATH + File.separator + UUID.randomUUID();
    }
}
