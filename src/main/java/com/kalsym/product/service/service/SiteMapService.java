package com.kalsym.product.service.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.LocationConfig;
import com.kalsym.product.service.repository.LocationConfigRepository;

@Service
public class SiteMapService {

    @Autowired
    LocationConfigRepository locationConfigRepository;

    @Value("${marketplace.url}")
    private String marketPlaceUrl;

    @Value("${path.main.sitemapxml}")
    private String pathMainXml;

    public String generateLocationSitemap(){

        List<LocationConfig> locationListing = locationConfigRepository.findAll();

        String mainXml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        +"\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
        ;
        String  locationXml = "";
        String endXml = "\n</urlset>";

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);   // 2021-03-24 16:48:05.591
        System.out.println(sdf2.format(timestamp));  // 2021-03-24T16:48:05.591+08:00


        for(LocationConfig lc:locationListing){

            locationXml += 
            "\n\t<url>"
            +"\n\t\t<loc>"+marketPlaceUrl+"/location/"+lc.getCityId()+"</loc>"
            +"\n\t\t<lastmod>"+sdf2.format(timestamp)+"</lastmod>"
            +"\n\t</url>"
            ;

        }

        String finalXml = mainXml+locationXml+endXml;

        return finalXml;

    }

    public void overWriteFile(String finalXml){
            
        File fold = new File(pathMainXml);
        fold.delete();

        File fnew = new File(pathMainXml);
        
        try {
            FileWriter f2 = new FileWriter(fnew, false);
            f2.write(finalXml);
            f2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }   

    }

    public void createFileSitemapLocation() throws IOException{

        File file = new File(pathMainXml);
        boolean result;  

        try   
        {  
            result = file.createNewFile(); 
            if(result)      
            {  
            System.out.println("CREATED :::::::: "+file.getCanonicalPath());   
            }  
            else  
            {  
            System.out.println("ALREADY EXISTS :::::::: "+file.getCanonicalPath());  
            }  
        }   
        catch (IOException e)   
        {  
            e.printStackTrace(); 
        }     

    }
    
}
