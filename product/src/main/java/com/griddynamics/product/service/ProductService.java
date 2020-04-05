package com.griddynamics.product.service;

import com.griddynamics.product.dto.InventoryDTO;
import com.griddynamics.product.expection.ServiceUnavailableException;
import com.griddynamics.product.model.ProductEntity;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    //TODO: refactor using api gateway
    private static final String CATALOG_API_PATH = "http://catalog-service/catalog/products";
    private static final String INVENTORY_API_PATH = "http://inventory-service/inventory";

    private final RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallbackMethodTest",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "60000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3")
            })
    public List<ProductEntity> getAvailableProducts(String sku) {
        ResponseEntity<ProductEntity[]> productEntityResponseEntity;
        try {
            productEntityResponseEntity = restTemplate.getForEntity(CATALOG_API_PATH + "/sku/{sku}", ProductEntity[].class, sku);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalStateException("product not found");
            }
            throw new IllegalStateException(ex);
        }

        List<ProductEntity> availableProducts = Arrays.asList(productEntityResponseEntity.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> request = new HttpEntity<>(availableProducts.stream()
                .map(ProductEntity::getId)
                .collect(toList()), headers);

        ResponseEntity<InventoryDTO[]> inventoryProductEntityResponseEntity;
        try {
            inventoryProductEntityResponseEntity = restTemplate.postForEntity(INVENTORY_API_PATH, request, InventoryDTO[].class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalStateException("inventory product data not found");
            }
            throw new IllegalStateException(ex);
        }

        Map<String, Long> inventoryDataMap = Arrays.stream(inventoryProductEntityResponseEntity.getBody())
                .collect(toMap(InventoryDTO::getId, InventoryDTO::getAvailable));
        return availableProducts.stream()
                .filter(e -> inventoryDataMap.containsKey(e.getId()) && inventoryDataMap.get(e.getId()) > 0)
                .collect(Collectors.toList());
    }

    private List<ProductEntity> fallbackMethodTest(String sku) {
        log.info("fallback method was invoked, sku = {}", sku);

        throw new ServiceUnavailableException("service unavailable");
    }
}
