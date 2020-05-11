package com.griddynamics.product.service;

import com.griddynamics.product.dto.InventoryDTO;
import com.griddynamics.product.exception.InventoryDataNotFoundException;
import com.griddynamics.product.exception.ServiceUnavailableException;
import com.griddynamics.product.model.ProductEntity;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.exception.HystrixTimeoutException;
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
            commandKey = "getAvailableProducts",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "30000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
            },ignoreExceptions = {InventoryDataNotFoundException.class, HttpClientErrorException.NotFound.class})
    //ignoreExceptions = {InventoryDataNotFoundException.class}
    //raiseHystrixExceptions = {HystrixException.RUNTIME_EXCEPTION}
    public ResponseEntity getAvailableProducts(String sku) throws ServiceUnavailableException {
        ResponseEntity<ProductEntity[]> productEntityResponseEntity =
                restTemplate.getForEntity(CATALOG_API_PATH + "/sku/{sku}", ProductEntity[].class, sku);

        List<ProductEntity> availableProducts = Arrays.asList(productEntityResponseEntity.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> request = new HttpEntity<>(availableProducts.stream()
                .map(ProductEntity::getId)
                .collect(toList()), headers);

        ResponseEntity<InventoryDTO[]> inventoryProductEntityResponseEntity;
        inventoryProductEntityResponseEntity = restTemplate.postForEntity(INVENTORY_API_PATH, request, InventoryDTO[].class);

        if (inventoryProductEntityResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error("inventory product data not found");
            throw new InventoryDataNotFoundException("inventory product data not found");
        }

        Map<String, Long> inventoryDataMap = Arrays.stream(inventoryProductEntityResponseEntity.getBody())
                .collect(toMap(InventoryDTO::getId, InventoryDTO::getAvailable));
        return ResponseEntity.ok(availableProducts.stream()
                .filter(e -> inventoryDataMap.containsKey(e.getId()) && inventoryDataMap.get(e.getId()) > 0)
                .collect(Collectors.toList()));
    }

    private ResponseEntity fallbackMethodTest(String sku, Throwable e) {
        log.error("fallback method was invoked, sku = {}", sku);
        if (e instanceof HystrixTimeoutException) {
            log.error("timed out", e);
        } else if (e instanceof InventoryDataNotFoundException || e instanceof HttpClientErrorException) {
            //this branch won't be called since we ignore such exceptions. handling in controller
            //therefore wont affect threshold
            log.error("inventory data isn't found", e);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (e instanceof RuntimeException) {
            log.error("threshold has been reached", e);
        } else {
            log.error("something else", e);
        }
        return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
