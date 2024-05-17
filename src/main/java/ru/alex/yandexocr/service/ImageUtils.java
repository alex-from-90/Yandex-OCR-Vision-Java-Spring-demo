package ru.alex.yandexocr.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static byte[] correctOrientation(MultipartFile file) throws IOException, MetadataException, ImageProcessingException {
        // Чтение изображения
        BufferedImage image = ImageIO.read(file.getInputStream());

        // Чтение метаданных
        Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());

        // Получение ориентации из метаданных
        ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (directory != null) {
            int orientation = 1;
            if (directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }

            // Корректировка изображения в зависимости от ориентации
            switch (orientation) {
                case 1: // [Exif Orientation 1] Normal
                    break;
                case 3: // [Exif Orientation 3] Rotated 180 degrees
                    image = Scalr.rotate(image, Scalr.Rotation.CW_180);
                    break;
                case 6: // [Exif Orientation 6] Rotated 90 degrees CW
                    image = Scalr.rotate(image, Scalr.Rotation.CW_90);
                    break;
                case 8: // [Exif Orientation 8] Rotated 90 degrees CCW
                    image = Scalr.rotate(image, Scalr.Rotation.CW_270);
                    break;
                default:
                    break;
            }
        }

        // Запись корректированного изображения в байтовый массив
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}
