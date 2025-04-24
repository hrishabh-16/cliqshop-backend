package com.cliqshop.websocket;

import com.cliqshop.dto.ProductDto;
import com.cliqshop.websocket.ProductUpdateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductUpdateHandler {

    private static final String PRODUCT_UPDATE_DESTINATION = "/topic/product-updates";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyProductCreated(ProductDto productDto) {
        messagingTemplate.convertAndSend(PRODUCT_UPDATE_DESTINATION, 
            new ProductUpdateMessage("CREATED", productDto));
    }

    public void notifyProductUpdated(ProductDto productDto) {
        messagingTemplate.convertAndSend(PRODUCT_UPDATE_DESTINATION, 
            new ProductUpdateMessage("UPDATED", productDto));
    }

    public void notifyProductDeleted(Long productId) {
        messagingTemplate.convertAndSend(PRODUCT_UPDATE_DESTINATION, 
            new ProductUpdateMessage("DELETED", productId));
    }
}