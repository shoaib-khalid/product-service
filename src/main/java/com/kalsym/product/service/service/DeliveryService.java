package com.kalsym.product.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.store.object.DeliveryServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import com.kalsym.product.service.utility.Logger;
import org.json.JSONObject;
import java.util.Date;
import java.sql.Time;

/**
 *
 * @author 7cu
 */
@Service
/**
 * Used to post the order in live.symplifed (rocket chat)
 */
public class DeliveryService {

    //@Autowired
    @Value("${deliveryService.createcentercode.URL:not-set}")
    String deliveryServiceCreateCenterCodeURL;
   
    public DeliveryServiceResponse createCenterCode(String storeId) {
        String logprefix = "createCenterCode";

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer accessToken");
            
            HttpEntity httpEntity = new HttpEntity(null, headers);
            
            String url = deliveryServiceCreateCenterCodeURL.replace("<storeId>", storeId);
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Calling delivery service url : "+url);
            ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Request sent to delivery service, responseCode: " + res.getStatusCode() + ", responseBody: " + res.getBody());

            if (res.getStatusCode() == HttpStatus.OK) {
                Gson gson = new Gson();
                DeliveryServiceResponse response = gson.fromJson(res.getBody(), DeliveryServiceResponse.class);
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "DeliveryServiceResponse:" + response.toString());
                return response;
            }
        } catch (RestClientException e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "Error creating center code: " + deliveryServiceCreateCenterCodeURL, e);
            return null;
        }
        return null;
    }


}
