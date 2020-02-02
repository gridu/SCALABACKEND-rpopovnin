package com.griddynamics.product.controller;

import com.griddynamics.product.model.InventoryDTO;
import com.griddynamics.product.model.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private static final String CATALOG_API_PATH = "http://localhost:8181/api/catalog/products";
    private static final String INVENTORY_API_PATH = "http://localhost:8282/api/inventory";

    private final RestTemplate restTemplate;

//    @GetMapping("/id/{id}")
//    private ProductEntity getProductById(@PathVariable String id) {
//    }

    @GetMapping("/sku/{sku}")
    private List<ProductEntity> getAvailableProductsBySku(@PathVariable String sku) {
        ResponseEntity<ProductEntity[]> productEntityResponseEntity =
                restTemplate.getForEntity(CATALOG_API_PATH + "/sku/{sku}", ProductEntity[].class, sku);

        if (productEntityResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("something wrong with catalog service");
        }

        List<ProductEntity> availableProducts = Arrays.asList(productEntityResponseEntity.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> request = new HttpEntity<>(availableProducts.stream()
                .map(ProductEntity::getId)
                .collect(toList()), headers);

        ResponseEntity<InventoryDTO[]> inventoryProductEntityResponseEntity =
                restTemplate.postForEntity(INVENTORY_API_PATH, request, InventoryDTO[].class);

        if (inventoryProductEntityResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("something wrong with inventory service");
        }

        Map<String, Long> inventoryDataMap = Arrays.stream(inventoryProductEntityResponseEntity.getBody())
                .collect(toMap(InventoryDTO::getId, InventoryDTO::getAvailable));
        return availableProducts.stream()
                .filter(e -> inventoryDataMap.containsKey(e.getId()) && inventoryDataMap.get(e.getId()) > 0)
                .collect(Collectors.toList());
    }

}
