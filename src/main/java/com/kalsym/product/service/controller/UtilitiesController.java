package com.kalsym.product.service.controller;

import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.ImageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/utilities")
public class UtilitiesController {

    @PostMapping(path = {"/convertImageToBase64"}, name = "store-assets-post", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('store-assets-post', 'all')")
    public ResponseEntity<HttpResponse> convertImageToBase64(HttpServletRequest request,
                                                     @RequestBody(required = true) List<String> imageUrls) throws IOException {
        HttpResponse response = new HttpResponse(request.getRequestURI());


        List<String> convertedImages = new ArrayList<>();
        try {
            for (String imageUrl : imageUrls) {
                // Check if imageUrl is valid
                if (imageUrl != null && isValidURL(imageUrl)){
                    String convertedImage = ImageUtils.imageUrlToBase64(imageUrl);
                    convertedImages.add(convertedImage);
                } else {
                    convertedImages.add(null);
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(convertedImages);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}
