package com.kalsym.product.service.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.Logger;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author saros
 */
@Service
public class StoreSubdomainHandler {

    @Value("${store.subdomain.creation.url:https://api.godaddy.com/v1/domains/<base-domain>/records/CNAME}")
    private String storeSubDomainCreationUrl;

    @Value("${store.subdomain.token:not-set}")
    private String storeSubDomainToken;

    @Value("${store.subdomain.config.path:/etc/nginx/conf.d/var}")
    private String storeSubDomainConfigPath;

    @Value("${store.subdomain.config.allowed:false}")
    private Boolean ngInxWithDomain;

    public String generateDomainName(String storeName) {
        storeName = storeName.replace(" ", "-");
        return storeName;
    }

    public String createSubDomain(String name, String verticalId, String baseDomain) throws Exception {

        String logprefix = "createSubDomain";

        DomainCreationRequestBody dcrb = new DomainCreationRequestBody();
        dcrb.setData("@");
        dcrb.setPriority(0);
        dcrb.setTtl(600);
        dcrb.setWeight(0);
        List<DomainCreationRequestBody> list = new ArrayList<>();
        list.add(dcrb);
        String url = storeSubDomainCreationUrl + "/" + name;
        url = url.replaceAll("<base-domain>", baseDomain);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "url: " + url, "");

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", storeSubDomainToken);

            HttpEntity<Object> entity;
            entity = new HttpEntity<>(list, headers);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "entity: " + entity, "");

            //restTemplate.postForEntity(storeSubDomainCreationUrl + "/" + name, list, String.class);
            Object res = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "domain created: " + res, "");

            //restTemplate.exchange(requestEntity, String.class);
//            Object res = webClient.post()
//                    .uri(name)
//                    .header("Authorization", storeSubDomainToken)
//                    .body(list, List.class)
//                    .retrieve()
//                    .bodyToMono(Object.class)
//                    .timeout(Duration.ofSeconds(10));
            if (ngInxWithDomain) {
                configureNginxWithDomain(name, baseDomain);
            }

        } catch (RestClientException e) {
            Logger.application.warn(ProductServiceApplication.VERSION, logprefix, "Error creating domain" + storeSubDomainCreationUrl, e);
            return null;
        }
        return name;
    }

    public void configureNginxWithDomain(String subDomaiprefix, String baseDomain) throws Exception {
        String logprefix = "configureNginxWithDomain";

        String subdomain = storeSubDomainConfigPath + "/" + subDomaiprefix + "."+baseDomain+".conf";
        FileWriter fileWriter = new FileWriter(subdomain);

        String text = configText.replace("<domain>", subDomaiprefix);
        text = text.replaceAll("<base-domain>", baseDomain);
        
        fileWriter.write(text);
        fileWriter.close();

        String testCommand = "nginx -t";

        Process proc = Runtime.getRuntime().exec(testCommand);
        int exitCode = proc.waitFor();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "test result: " + exitCode, "");

        String reloadCommand = "nginx -s reload";

        proc = Runtime.getRuntime().exec(reloadCommand);
        exitCode = proc.waitFor();
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "reload result: " + exitCode, "");
    }

    @Getter
    @Setter
    @ToString
    public class DomainCreationRequestBody {

        private String data;
        //private String name;
        private int priority;
        private int ttl;
        private int weight;
    }

    private String configText = "server {\n"
            + "    listen 80;\n"
            + "    server_name <domain>.<base-domain>;\n"
            + "    return 301 https://$host$request_uri;\n"
            + "}\n"
            + "\n"
            + "server {\n"
            + "    listen      443 ssl http2;\n"
            + "    server_name <domain>.<base-domain>;\n"
            + "    root        /var/www/html/simplify-fe;\n"
            + "\n"
            + "    ssl_certificate     /root/.getssl/*.<base-domain>/fullchain.crt;\n"
            + "    ssl_certificate_key /root/.getssl/*.<base-domain>/*.<base-domain>.key; \n"
            + "\n"
            + "    index       index.php index.html index.htm;\n"
            + "    access_log  /var/log/nginx/domains/*.<base-domain>.log combined;\n"
            + "    access_log  /var/log/nginx/domains/*.<base-domain>.bytes bytes;\n"
            + "    error_log   /var/log/nginx/domains/*.<base-domain>.error.log error;\n"
            + "\n"
            + "    location / {\n"
            + "\n"
            + "        try_files    $uri $uri/ /index.html;\n"
            + "\n"
            + "        location ~* ^.+\\.(jpeg|jpg|png|gif|bmp|ico|svg|css|js)$ {\n"
            + "            expires     max;\n"
            + "        }\n"
            + "\n"
            + "\n"
            + "        location ~ [^/]\\.php(/|$) {\n"
            + "            fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;\n"
            + "            if (!-f $document_root$fastcgi_script_name) {\n"
            + "                return  404;\n"
            + "            }\n"
            + "\n"
            + "            fastcgi_pass    127.0.0.1:9002;\n"
            + "            fastcgi_index   index.php;\n"
            + "            include         /etc/nginx/fastcgi_params;\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "    error_page  403 /error/404.html;\n"
            + "    error_page  404 /error/404.html;\n"
            + "    error_page  500 502 503 504 /error/50x.html;\n"
            + "\n"
            + "    location /error/ {\n"
            + "        alias   /home/admin/web/symplified.biz/document_errors/;\n"
            + "    }\n"
            + "\n"
            + "    location ~* \"/\\.(htaccess|htpasswd)$\" {\n"
            + "        deny    all;\n"
            + "        return  404;\n"
            + "    }\n"
            + "}";
}
