package com.griddynamics.product.controller;

import com.griddynamics.product.model.ProductEntity;
import com.griddynamics.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

//    @GetMapping("/id/{id}")
//    private ProductEntity getProductById(@PathVariable String id) {
//    }

    @GetMapping("/sku/{sku}")
    private List<ProductEntity> getAvailableProductsBySku(@PathVariable String sku) throws ServiceUnavailableException {
        return productService.getAvailableProducts(sku);
    }

}
