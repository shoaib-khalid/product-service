package com.kalsym.product.service;

import com.kalsym.product.service.service.SiteMapService;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GenerateSiteMapScheduler {
    
    @Autowired
    SiteMapService siteMapService;

    @Value("${path.main.sitemapxml}")
    private String pathMainXml;

    // one minute - 60000
    // @Scheduled(fixedRate = 60000)  

    //everyday at 11:00 pm
    @Scheduled(cron = "0 0 23 * * *")
    public void generateSitemapXml() throws Exception{

        File f = new File(pathMainXml);

        if(f.exists() && !f.isDirectory()) { 
            String finalXml = siteMapService.generateLocationSitemap();

            siteMapService.overWriteFile(finalXml);
        }

         
    }


    // <?xml version="1.0" encoding="UTF-8"?>

    // <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    //     <url>
    //     <loc>https://www.layar.la/home</loc>
    //     <lastmod>2022-07-26T08:56:02+00:00</lastmod>
    //     </url>
    //     <url>
    //     <loc>https://www.layar.la/home/about-us</loc>
    //     <lastmod>2022-07-26T08:56:02+00:00</lastmod>
    //     </url>
    //     <url>
    //     <loc>https://www.layar.la/home/contact-us</loc>
    //     <lastmod>2022-07-26T08:56:02+00:00</lastmod>
    //     </url>
    //     <url>
    //     <loc>https://www.layar.la/pricing</loc>
    //     <lastmod>2022-07-26T08:56:02+00:00</lastmod>
    //     </url>
    // </urlset> 
}
