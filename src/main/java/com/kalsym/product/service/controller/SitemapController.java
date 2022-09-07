package com.kalsym.product.service.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.product.service.service.SiteMapService;

@Controller
@RestController()

public class SitemapController {

    @Value("${path.main.sitemapxml}")
    private String pathMainXml;

    @Autowired
    SiteMapService siteMapService;

    @GetMapping(path = {"/sitemap.xml"}, produces = {MediaType.APPLICATION_XML_VALUE,MediaType.TEXT_XML_VALUE}, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> serverFileList(HttpServletRequest request) throws IOException {

        File f = new File(pathMainXml);

        if(f.exists() && !f.isDirectory()) { 

            byte[] fileContent = Files.readAllBytes(Paths.get(pathMainXml));
        
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_XML);
            responseHeaders.setContentLength(fileContent.length);
    
            return new ResponseEntity<byte[]>(fileContent, responseHeaders,
                    HttpStatus.OK);

        }
        
        else{

            System.out.println("OPPPSSSS FILE NOT FOUND ::::::::");

            siteMapService.createFileSitemapLocation();
            String finalXml = siteMapService.generateLocationSitemap();
            siteMapService.overWriteFile(finalXml);

            byte[] byteArr = finalXml.getBytes();

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_XML);
    
            return new ResponseEntity<byte[]>(byteArr,responseHeaders,
                    HttpStatus.OK);

        }
      
    }

    
}
