package com.griddynamics.product.controller;

import com.griddynamics.product.expection.ServiceUnavailableException;
import com.griddynamics.product.model.ProductEntity;
import com.griddynamics.product.service.ProductService;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/sku/{sku}")
    private List<ProductEntity> getAvailableProductsBySku(@PathVariable String sku) throws Exception {
        try {
            return productService.getAvailableProducts(sku);
        } catch (HystrixRuntimeException e) {
            throw poorPeopleExceptionHandler(e);
        }
    }

    private RuntimeException poorPeopleExceptionHandler(HystrixRuntimeException e) {
        if (e.getFallbackException().getCause() instanceof HystrixTimeoutException)
            return new ServiceUnavailableException("timed out");
        else if (e.getFallbackException().getCause() instanceof RuntimeException)
            return new ServiceUnavailableException("threshold has been reached");
        else
            return new RuntimeException(e.getFallbackException().getCause());
    }
}
