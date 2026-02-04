package org.example.Event;

import org.example.Entities.Products;

public class StockChangeEvent {


    public StockChangeEvent(Products product) {
        this.product = product;
    }

    public Products getProduct() {
        return product;
    }

    private final Products product;





}
