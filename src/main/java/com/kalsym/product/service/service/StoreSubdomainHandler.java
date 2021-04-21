package com.kalsym.product.service.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.Logger;
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

    @Value("${store.subdomain.creation.url:https://api.godaddy.com/v1/domains/symplified.store/records/CNAME}")
    private String storeSubDomainCreationUrl;

    @Value("${store.subdomain.token:not-set}")
    private String storeSubDomainToken;

    public String generateDomainName(String storeName) {
        storeName = storeName.replace(" ", "-");
        return storeName;
    }

    public String createSubDomain(String name) {

        String logprefix = "createSubDomain";

        DomainCreationRequestBody dcrb = new DomainCreationRequestBody();
        dcrb.setData("@");
        dcrb.setName("beta");
        dcrb.setPriority(0);
        dcrb.setTtl(600);
        dcrb.setWeight(0);
        List<DomainCreationRequestBody> list = new ArrayList<>();
        String url = storeSubDomainCreationUrl + "/" + name;
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "url: " + url, "");

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", storeSubDomainToken);

            HttpEntity<Object> entity;
            entity = new HttpEntity<>(list, headers);

            //restTemplate.postForEntity(storeSubDomainCreationUrl + "/" + name, list, String.class);
            Object res = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            //restTemplate.exchange(requestEntity, String.class);

//            Object res = webClient.post()
//                    .uri(name)
//                    .header("Authorization", storeSubDomainToken)
//                    .body(list, List.class)
//                    .retrieve()
//                    .bodyToMono(Object.class)
//                    .timeout(Duration.ofSeconds(10));
        } catch (RestClientException e) {
            Logger.application.warn(ProductServiceApplication.VERSION, logprefix, "Error creating domain" + storeSubDomainCreationUrl, e);
            return null;
        }
        return name;
    }

    @Getter
    @Setter
    @ToString
    public class DomainCreationRequestBody {

        private String data;
        private String name;
        private int priority;
        private int ttl;
        private int weight;
    }
}
