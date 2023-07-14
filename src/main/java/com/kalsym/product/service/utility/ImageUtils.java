package com.kalsym.product.service.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class ImageUtils {

    public static String imageUrlToBase64(String imageUrl) throws IOException {
        // Create a URL object from the image URL
        URL url = new URL(imageUrl);

        // Open a connection to the URL and get an input stream
        try (InputStream inputStream = url.openStream()) {
            // Read the image data into a byte array
            byte[] imageData = readImageData(inputStream);

            // Encode the image data as base64
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            return base64Image;
        }
    }

    private static byte[] readImageData(InputStream inputStream) throws IOException {
        // Create a byte array output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Read the image data from the input stream into the byte array output stream
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Close the streams
        outputStream.close();
        inputStream.close();

        // Get the byte array from the output stream
        return outputStream.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        String imageUrl = "https://example.com/image.jpg";
        String base64Image = imageUrlToBase64(imageUrl);
        System.out.println(base64Image);
    }
}
