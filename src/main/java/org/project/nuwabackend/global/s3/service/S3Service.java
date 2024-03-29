package org.project.nuwabackend.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.nuwa.file.dto.response.FileUploadResultDto;
import org.project.nuwabackend.nuwa.file.type.FilePathType;
import org.project.nuwabackend.nuwa.file.type.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.project.nuwabackend.global.response.type.ErrorMessage.FILE_EXTENSION_NOT_FOUND;
import static org.project.nuwabackend.nuwa.file.type.FilePathType.FILE_PATH;
import static org.project.nuwabackend.nuwa.file.type.FilePathType.IMAGE_PATH;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 (이미지 & 파일)
    public FileUploadResultDto upload(FileType fileType, List<MultipartFile> multipartFileList) {

        Map<String, Long> imageUrlMap = new HashMap<>();
        Map<String, Long> fileUrlMap = new HashMap<>();

        for (MultipartFile multipartFile : multipartFileList) {
            String originFileName = originFileName(multipartFile);

            String uploadFileName = createFileName(originFileName);
            FilePathType filePath = getFilePath(uploadFileName);
            long byteSize = multipartFile.getSize();

            String fileContentType = multipartFile.getContentType();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(byteSize);
            objectMetadata.setContentType(fileContentType);

            String fileUrl = createFileUrl(filePath, fileType, uploadFileName);
            uploadToS3(multipartFile, bucket, fileUrl, objectMetadata, filePath == IMAGE_PATH ? imageUrlMap : fileUrlMap, byteSize);
        }
        return new FileUploadResultDto(imageUrlMap, fileUrlMap);
    }

    private void uploadToS3(MultipartFile multipartFile, String bucket, String fileUrl,
                            ObjectMetadata objectMetadata, Map<String, Long> urlMap, Long byteSize) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileUrl, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            urlMap.put(amazonS3.getUrl(bucket, fileUrl).toString(), byteSize);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    // 파일 삭제
    public void deleteFile(String fileUrl, FileType fileType) {
        int lastIndex = fileUrl.lastIndexOf("/") + 1;
        String fileName = fileUrl.substring(lastIndex);
        FilePathType filePath = getFilePath(fileName);

        String decode = URLDecoder.decode(fileName, StandardCharsets.UTF_8).trim();

        String getFileUrl = createFileUrl(filePath, fileType, decode);

        boolean exist = amazonS3.doesObjectExist(bucket, getFileUrl);
        if (exist) {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, getFileUrl);
            amazonS3.deleteObject(deleteObjectRequest);
        }
    }

    // 기존 파일 이름
    private String originFileName(MultipartFile multipartFile) {
        return multipartFile.getOriginalFilename();
    }

    // 이미지 파일명 중복 방지
    private String createFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."))
                .concat("_")
                .concat(LocalDateTime.now().toString())
                .concat(getExtension(fileName));
    }

    // 확장자 가져오기
    private String getExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            throw new IllegalArgumentException(FILE_EXTENSION_NOT_FOUND.getMessage());
        }

        return fileName.substring(lastIndexOfDot);
    }

    // 확장자로 파일인지 이미지인지 확인
    private FilePathType getFilePath(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            throw new IllegalArgumentException(FILE_EXTENSION_NOT_FOUND.getMessage());
        }

        String fileExtension = fileName.substring(lastIndexOfDot).toLowerCase();
        if (!isValidImageExtension(fileExtension)) {
            return FILE_PATH;
        }

        return IMAGE_PATH;
    }

    // 확장자 확인
    private boolean isValidImageExtension(String fileName) {
        Set<String> validExtensions = new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".svg"));
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        return validExtensions.contains(fileExtension);
    }

    private String createFileUrl(FilePathType filePathType, FileType fileType, String fileName) {
        return filePathType.getValue() + fileType.getValue() + "/" + fileName;
    }
}
