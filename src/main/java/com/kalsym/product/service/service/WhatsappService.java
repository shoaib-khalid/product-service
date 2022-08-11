/*
 * Copyright (C) 2021 taufik
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kalsym.product.service.service;

import com.kalsym.product.service.ProductServiceApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.kalsym.product.service.model.WhatsappMessage;
import com.kalsym.product.service.model.Template;
import com.kalsym.product.service.utility.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author taufik
 */

@Service
public class WhatsappService {
        
    @Value("${whatsapp.service.push.url:https://waw.symplified.it/360dialog/callback/templatemessage/push}")
    private String whatsappServiceUrl;
    
    @Value("${whatsapp.service.template.new.store:symplified_new_store_notification}")
    private String whatsappServiceNewStoreTemplate;
    
    @Value("${whatsapp.service.push.url:60133429331}")
    private String whatsappServiceReferenceId;
    
    public boolean sendWhatsappMessage(String[] recipients, String username, String password, String event) throws Exception {
        String templateName = whatsappServiceNewStoreTemplate;
        String logprefix = "sendWhatsappMessage";
        if (event.equals("NEWSTORE")) {
            templateName = whatsappServiceNewStoreTemplate;
        }
        RestTemplate restTemplate = new RestTemplate();        
        HttpHeaders headers = new HttpHeaders();
        WhatsappMessage request = new WhatsappMessage();
        request.setGuest(false);
        request.setRecipientIds(recipients);
        request.setRefId(recipients[0]);
        request.setReferenceId(whatsappServiceReferenceId);
        Template template = new Template();
        template.setName(templateName);
        String[] message = {username};
        template.setParameters(message);
        request.setTemplate(template);
        HttpEntity<WhatsappMessage> httpEntity = new HttpEntity<>(request, headers);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "url: " + whatsappServiceUrl, "");
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "httpEntity: " + httpEntity, "");

        ResponseEntity<String> res = restTemplate.postForEntity(whatsappServiceUrl, httpEntity, String.class);

        if (res.getStatusCode() == HttpStatus.ACCEPTED || res.getStatusCode() == HttpStatus.OK) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "res: " + res.getBody(), "");
            return true;
        } else {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "could not send verification email res: " + res, "");
            return false;
        }

    }
}
