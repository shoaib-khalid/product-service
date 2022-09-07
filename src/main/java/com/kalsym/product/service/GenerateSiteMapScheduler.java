package com.kalsym.product.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.repository.LocationConfigRepository;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.utility.Logger;
import com.kalsym.product.service.model.LocationConfig;
import com.kalsym.product.service.model.store.Store;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class GenerateSiteMapScheduler {
    
    @Autowired
    LocationConfigRepository locationConfigRepository;
    
    @Value("${marketplace.url}")
    private String marketPlaceUrl;

    @Value("${path.main.sitemapxml}")
    private String pathMainXml;

    // one minute - 60000
    // @Scheduled(fixedRate = 60000)  

    //everyday at 11:00 pm
    @Scheduled(cron = "0 0 23 * * *")
    public void generateSitemapXml() throws Exception{

        List<LocationConfig> locationListing = locationConfigRepository.findAll();

        String mainXml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        +"\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
        ;
        String  locationXml = "";
        String endXml = "\n</urlset>";

        for(LocationConfig lc:locationListing){

            locationXml += 
            "\n\t<url>"
            +"\n\t\t<loc>"+marketPlaceUrl+"/location/"+lc.getCityId()+"</loc>"
            +"\n\t\t<lastmod>2022-07-26T08:56:02+00:00</lastmod>"
            +"\n\t</url>"
            ;

        }

        String finalXml = mainXml+locationXml+endXml;

        // System.out.println("CHECKING THE GENERATE SITEMAP ::::::"+finalXml);
        
        //delete first
        File fold = new File(pathMainXml);
        fold.delete();

        //then wrtite
        File fnew = new File(pathMainXml);

        try {
            FileWriter f2 = new FileWriter(fnew, false);
            f2.write(finalXml);
            f2.close();

        } catch (IOException e) {
            e.printStackTrace();
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
