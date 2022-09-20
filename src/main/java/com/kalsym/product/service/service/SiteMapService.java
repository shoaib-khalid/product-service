package com.kalsym.product.service.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.kalsym.product.service.model.LocationConfig;
import com.kalsym.product.service.model.store.ParentCategory;
import com.kalsym.product.service.model.store.StoreCategory;
import com.kalsym.product.service.repository.LocationConfigRepository;

@Service
public class SiteMapService {

    @Autowired
    LocationConfigRepository locationConfigRepository;

    @Autowired
    StoreCategoryService storeCategoryService;

    @Value("${marketplace.url}")
    private String marketPlaceUrl;

    @Value("${protocol.subdomain}")
    private String protocolSubdomain;

    @Value("${path.main.sitemapxml}")
    private String pathMainXml;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public String indexSiteMap(){

        String mainXml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        +"\n<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
        ;

        String childSiteMap = 
        "\n\t<sitemap>"
        +"\n\t\t<loc>"+protocolSubdomain+contextPath+"/location.xml"+"</loc>"
        +"\n\t</sitemap>";

        String endXml = "\n</sitemapindex>";

        String finalXml = mainXml+childSiteMap+endXml;

        return finalXml;    
    }

    public String generateLocationSitemap(){

        List<LocationConfig> locationListing = locationConfigRepository.findAll();

        String mainXml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        +"\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
        ;
        String  locationXml = "";
        String endXml = "\n</urlset>";

        // SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);   // 2021-03-24 16:48:05.591
        // System.out.println(sdf2.format(timestamp));  // 2021-03-24T16:48:05.591+08:00
        System.out.println(sdf3.format(timestamp));         // 2021-03-24 16:48:05


        for(LocationConfig lc:locationListing){

            locationXml += 
            "\n\t<url>"
            +"\n\t\t<loc>"+marketPlaceUrl+"/location/"+lc.getCityId()+"</loc>"
            +"\n\t\t<lastmod>"+sdf3.format(timestamp)+"</lastmod>"
            +"\n\t</url>"
            ;

        }

        String finalXml = mainXml+locationXml+endXml;

        return finalXml;

    }

    public String generateParentCategory(){
       
        //Mlaaysia vertical code
        List<String> verticalCode = new ArrayList<>();
        verticalCode.add("FnB");
        verticalCode.add("E-Commerce");
        Page<StoreCategory> searchParentCategory = storeCategoryService.searchWithCriteria(verticalCode,0,30);

        List<StoreCategory> storeCategoryList = searchParentCategory.getContent();

        String mainXml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        +"\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
        ;
        String  categoryXml = "";
        String endXml = "\n</urlset>";

        // SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        for(StoreCategory sc:storeCategoryList){

            categoryXml += 
            "\n\t<url>"
            +"\n\t\t<loc>"+marketPlaceUrl+"/category/"+sc.getId()+"</loc>"
            +"\n\t\t<lastmod>"+sdf3.format(timestamp)+"</lastmod>"
            +"\n\t</url>"
            ;

        }

        String finalXml = mainXml+categoryXml+endXml;

        return finalXml;

    }

    public void overWriteFile(String fullPath,String finalXml){
            
        File fold = new File(fullPath);
        fold.delete();

        File fnew = new File(fullPath);
        
        try {
            FileWriter f2 = new FileWriter(fnew, false);
            f2.write(finalXml);
            f2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }   

    }

    public void createFileSitemapLocation(String fullPath) throws IOException{

        File file = new File(fullPath);
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
