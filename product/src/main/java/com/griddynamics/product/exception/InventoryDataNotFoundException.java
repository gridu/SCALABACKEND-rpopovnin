package com.griddynamics.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InventoryDataNotFoundException extends RuntimeException {

    public InventoryDataNotFoundException() {
        super();
    }

    public InventoryDataNotFoundException(String message) {
        super(message);
    }

    public InventoryDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryDataNotFoundException(Throwable cause) {
        super(cause);
    }

}
