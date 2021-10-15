package com.shoppingmall.domain.utils;

import com.shoppingmall.domain.items.ImageFile;
import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.members.AttachedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileStoreUtil {

    @Value("${file.attached-dir}")
    private String attachedDir;

    @Value("${file.image-dir}")
    private String imageDir;

    public String getAttachedFullPath(String filename) {
        return attachedDir + filename;
    }

    public String getImageFullPath(String filename) {
        return imageDir + filename;
    }

    public AttachedFile storeAttachedFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFileName);

        multipartFile.transferTo(new File(getAttachedFullPath(storeFileName)));

        return new AttachedFile(originalFileName, storeFileName);
    }

    public List<ImageFile> storeImageFiles(List<MultipartFile> multipartFiles, Item item) throws IOException {
        List<ImageFile> imageFiles = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                ImageFile imageFile = storeImageFile(multipartFile, item);
                imageFiles.add(imageFile);
            }
        }
        return imageFiles;
    }

    public ImageFile storeImageFile(MultipartFile multipartFile, Item item) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFileName);

        multipartFile.transferTo(new File(getImageFullPath(storeFileName)));

        return new ImageFile(originalFileName, storeFileName, item);
    }

    private String createStoreFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFileName);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFileName) {
        int position = originalFileName.lastIndexOf(".");
        return originalFileName.substring(position + 1);
    }
}
