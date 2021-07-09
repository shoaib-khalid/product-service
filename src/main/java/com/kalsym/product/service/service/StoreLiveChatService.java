package com.kalsym.product.service.service;

import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.model.livechatgroup.*;
import com.kalsym.product.service.utility.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author saros
 */
@Service
public class StoreLiveChatService {

    @Value("${livechat.store.agent.creation.url:http://209.58.160.20:3000/api/v1/groups.create}")
    private String livechatStoreGroupCreationUrl;

    @Value("${livechat.store.agent.deletion.url:http://209.58.160.20:3000/api/v1/groups.delete}")
    private String livechatStoreGroupDeletionUrl;

    @Value("${livechat.store.agent.deletion.url:http://209.58.160.20:3000/api/v1/groups.invite}")
    private String livechatStoreGroupInviteUrl;

    @Value("${livechat.token:GMmNIJTFglt3EW-D8CHj4c29AMSc74ix9vVJUPgN_RZ}")
    private String livechatToken;

    @Value("${livechat.userid:JEdxZxgW4R5Z53xq2}")
    private String livechatUserId;

    public StoreCreationResponse createGroup(String name) {
        String logprefix = Thread.currentThread().getStackTrace()[1].getMethodName();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", livechatToken);
        headers.add("X-User-Id", livechatUserId);

        class LiveChatGroup {

            String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

        }

        LiveChatGroup lcg = new LiveChatGroup();
        lcg.setName(name);

        HttpEntity<LiveChatGroup> entity;
        entity = new HttpEntity<>(lcg, headers);

        try {
            ResponseEntity<LiveChatResponse> res = restTemplate.exchange(livechatStoreGroupCreationUrl, HttpMethod.POST, entity, LiveChatResponse.class);

            if (res.getBody().success == true) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " created agent " + res.getBody());

                return res.getBody().group;
            } else {
                return null;
            }
        } catch (RestClientException e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " could not create agent", e);

        }
        return null;
    }

    public Object deleteGroup(String id) {
        String logprefix = Thread.currentThread().getStackTrace()[1].getMethodName();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", livechatToken);
        headers.add("X-User-Id", livechatUserId);

        class DeleteGroup {

            private String roomId;

            public String getRoomId() {
                return roomId;
            }

            public void setRoomId(String roomId) {
                this.roomId = roomId;
            }
        }

        DeleteGroup deleteGroup = new DeleteGroup();
        deleteGroup.setRoomId(id);
        HttpEntity<DeleteGroup> entity;
        entity = new HttpEntity<>(deleteGroup, headers);

        try {
            ResponseEntity<LiveChatResponse> res = restTemplate.exchange(livechatStoreGroupDeletionUrl, HttpMethod.POST, entity, LiveChatResponse.class);

            if (res.getBody().success == true) {
                Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " deleted agent " + res.getBody());

                return res.getBody().group;
            } else {
                return null;
            }
        } catch (RestClientException e) {
            Logger.application.error(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " could not delete agent", e);

        }
        return null;
    }

    public LiveChatGroupInviteResponse inviteAgent(LiveChatGroupInvite liveChatGroupInvite) {
        String logprefix = Thread.currentThread().getStackTrace()[1].getMethodName();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", livechatToken);
        headers.add("X-User-Id", livechatUserId);

        HttpEntity<LiveChatGroupInvite> entity;
        entity = new HttpEntity<>(liveChatGroupInvite, headers);

        ResponseEntity<LiveChatGroupInviteResponse> res = restTemplate.exchange(livechatStoreGroupInviteUrl, HttpMethod.POST, entity, LiveChatGroupInviteResponse.class);

        if (res.getBody().success == true) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " invited agent " + res.getBody());

            return res.getBody();
        } else {
            return null;
        }
    }
}
