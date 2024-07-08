package com.motive.rest.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ImageRepo imageRepo;

    public Image uploadImage(MultipartFile file) throws IOException {
       return  imageRepo.save(new Image(ImageUtil.compressImage(file.getBytes()), file.getContentType()));
    }

    public byte[] getImage(UUID id) {
        Optional<Image> dbImage = imageRepo.findById(id);
        byte[] image = ImageUtil.decompressImage(dbImage.get().getData());
        return image;
    }
}