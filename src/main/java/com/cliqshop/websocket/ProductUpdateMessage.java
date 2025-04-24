package com.cliqshop.websocket;

import com.cliqshop.dto.ProductDto;

public class ProductUpdateMessage {
    private String action;
    private Object payload;

    public ProductUpdateMessage() {}

    public ProductUpdateMessage(String action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

    // Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}