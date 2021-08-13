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
import com.kalsym.product.service.utility.Logger;

/**
 *
 * @author saros
 */
@Service
public class StoreLiveChatService {

    @Value("${livechat.store.agent.creation.url:https://live.symplified.biz/api/v1/groups.create}")
    private String livechatStoreGroupCreationUrl;

    @Value("${livechat.store.agent.deletion.url:https://live.symplified.biz/api/v1/groups.delete}")
    private String livechatStoreGroupDeletionUrl;

    @Value("${livechat.store.agent.invitation.url:https://live.symplified.biz/api/v1/groups.invite}")
    private String livechatStoreGroupInviteUrl;

//    @Value("${livechat.token:GMmNIJTFglt3EW-D8CHj4c29AMSc74ix9vVJUPgN_RZ}")
    private String liveChatToken;

    @Value("${livechat.userid:JEdxZxgW4R5Z53xq2}")
    private String liveChatUserId;

    @Value("${liveChatlogin.username:order}")
    private String liveChatLoginUsername;
    @Value("${liveChat.login.password:sarosh@1234}")
    private String liveChatLoginPassword;
    @Value("${liveChat.login.url:http://209.58.160.20:3000/api/v1/login}")
    private String liveChatLoginUrl;

    public StoreCreationResponse createGroup(String name) {
        if (!loginLiveChat()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, " created agent");
            return null;
        }
        String logprefix = Thread.currentThread().getStackTrace()[1].getMethodName();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", liveChatToken);
        headers.add("X-User-Id", liveChatUserId);

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
        if (!loginLiveChat()) {
//            logger.info("live chat not logged in");
            return "";
        }
        String logprefix = Thread.currentThread().getStackTrace()[1].getMethodName();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", liveChatToken);
        headers.add("X-User-Id", liveChatUserId);

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

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " livechatStoreGroupInviteUrl: " + livechatStoreGroupInviteUrl);
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
        if (!loginLiveChat()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, " created agent");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", liveChatToken);
        headers.add("X-User-Id", liveChatUserId);

        HttpEntity<LiveChatGroupInvite> entity;
        entity = new HttpEntity<>(liveChatGroupInvite, headers);

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " livechatStoreGroupInviteUrl: " + livechatStoreGroupInviteUrl);
        ResponseEntity<LiveChatGroupInviteResponse> res = restTemplate.exchange(livechatStoreGroupInviteUrl, HttpMethod.POST, entity, LiveChatGroupInviteResponse.class);

        if (res.getBody().success == true) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " invited agent " + res.getBody());

            return res.getBody();
        } else {
            return null;
        }
    }

    public boolean loginLiveChat() {
        String logprefix = "loginLiveChat";
        if (null != liveChatToken) {
            return true;
        }
        class LoginRequest {

            public String user;
            public String password;

            public LoginRequest() {
            }

            public LoginRequest(String user, String password, String code) {
                this.user = user;
                this.password = password;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            @Override
            public String toString() {
                return "LoginRequest{" + "user=" + user + ", password=" + password + '}';
            }
        }
        RestTemplate restTemplate = new RestTemplate();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUser(liveChatLoginUsername);
        loginRequest.setPassword(liveChatLoginPassword);
        HttpEntity<LoginRequest> httpEntity = new HttpEntity<>(loginRequest);
        try {
//            logger.info("liveChatLoginUrl: " + liveChatLoginUrl);
//            logger.info("httpEntity: " + httpEntity);
            ResponseEntity<LiveChatLoginReponse> res = restTemplate.exchange(liveChatLoginUrl, HttpMethod.POST, httpEntity, LiveChatLoginReponse.class);
//            logger.info("res: " + res);
            LiveChatLoginReponse liveChatLoginReponse = res.getBody();
            liveChatUserId = liveChatLoginReponse.getData().userId;
            liveChatToken = liveChatLoginReponse.getData().authToken;
            return true;
        } catch (Exception e) {
//            Logger.error("Error loging in livechat ", e);
            return false;
        }
    }
}
