package com.kalsym.product.service.controller;

import com.kalsym.product.service.model.livechatgroup.LiveChatGroupInvite;
import com.kalsym.product.service.ProductServiceApplication;
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.model.store.Store;
import com.kalsym.product.service.model.livechatgroup.LiveChatGroupInviteResponse;
import com.kalsym.product.service.repository.StoreRepository;
import com.kalsym.product.service.utility.Logger;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kalsym.product.service.service.StoreLiveChatService;

/**
 *
 * @author 7cu
 */
@RestController()
@RequestMapping("/stores/{storeId}/livechat")
public class StoreLiveChatController {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreLiveChatService storeLiveChatService;

    @PostMapping(path = {"/order-csr/agentinvite"}, name = "store-livechat-post-orderagentinvite")
    @PreAuthorize("hasAnyAuthority('store-livechat-post-orderagentinvite', 'all')")
    public ResponseEntity<HttpResponse> postOrderStoreAgentInvite(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestBody LiveChatGroupInvite body) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyInvite: " + body);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        if (body.getRoomId() == null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " roomId not attached in request");
            body.setRoomId(optStore.get().getLiveChatOrdersGroupId());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " roomId attached from store info: " + body.getRoomId());
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyInvite: " + body);

        LiveChatGroupInviteResponse liveChatGroupInviteResponse = storeLiveChatService.inviteAgent(body);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "liveChatGroupInviteResponse:" + liveChatGroupInviteResponse, "");

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "agent invited to store with roomId:" + storeId, "");

        response.setStatus(HttpStatus.CREATED);
        //response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = {"/complaint-csr/agentinvite"}, name = "store-livechat-post-complaintagentinvite")
    @PreAuthorize("hasAnyAuthority('store-livechat-post-complaintagentinvite', 'all')")
    public ResponseEntity<HttpResponse> postComplaintStoreAgentInvite(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestBody LiveChatGroupInvite body) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "storeId: " + storeId);
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyInvite: " + body);

        Optional<Store> optStore = storeRepository.findById(storeId);

        if (!optStore.isPresent()) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " NOT_FOUND storeId: " + storeId);
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("store not found");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " FOUND storeId: " + storeId);

        if (body.getRoomId() == null) {
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " roomId not attached in request");
            body.setRoomId(optStore.get().getLiveChatCsrGroupId());
            Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, " roomId attached from store info: " + body.getRoomId());
        }

        Logger.application.info(Logger.pattern, ProductServiceApplication.VERSION, logprefix, "bodyInvite: " + body);

        LiveChatGroupInviteResponse liveChatGroupInviteResponse = storeLiveChatService.inviteAgent(body);
        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "liveChatGroupInviteResponse:" + liveChatGroupInviteResponse, "");

        Logger.application.info(ProductServiceApplication.VERSION, logprefix, "agent invited to store with roomId:" + storeId, "");

        response.setStatus(HttpStatus.CREATED);
        //response.setData(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
