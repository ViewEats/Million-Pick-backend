package com.youngchanteam.viewEats.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(Long id) {
        super("Restaurant not found: " + id);
    }
}