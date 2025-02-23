package com.all_i.allibe.s3.application;

import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.region}")
    private String region;

    public void downloadFileFromS3(
            String objectKey,
            String downloadFilePath
    ) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try {
            s3Client.getObject(
                    getObjectRequest,
                    Paths.get(downloadFilePath)
            );
            log.info("File downloaded successfully to " + downloadFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to download the file: " + e.getMessage());
        }
    }

    /**
     * @param multipartFile 업로드를 위한 파일
     * @param dirName 업로드 디렉토리명
     * @return 업로드된 S3 Object Key(파일 이름)
     *
     * */
    public String uploadFile(
            MultipartFile multipartFile,
            String dirName
    ) {
        try {
            String fileName =
                dirName
                    + "/"
                    + System.currentTimeMillis()
                    + "_"
                    + multipartFile.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(multipartFile.getContentType())
                .build();

            RequestBody requestBody = RequestBody.fromInputStream(
                    multipartFile.getInputStream(),
                    multipartFile.getSize()
            );

            s3Client.putObject(
                    putObjectRequest,
                    requestBody
            );

            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName,
                    region,
                    fileName
            );
        } catch (IOException e) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST);
        }
    }

    public List<String> uploadFiles(List<MultipartFile> multipartFiles, String dirName) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String url = uploadFile(multipartFile, dirName);
            fileUrls.add(url);
        }
        return fileUrls;
    }

    public String getFileUrl(String fileName) {
        return "https://"+bucketName+".s3.ap-northeast-2.amazonaws.com/"+fileName;
    }
}
