package com.griddynamics.catalog.service;

import com.griddynamics.catalog.model.ProductEntity;
import com.griddynamics.catalog.parser.ProductParser;
import com.griddynamics.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    @Value(value = "classpath:data/jcpenney_com-ecommerce_sample.csv")
    private Resource productsFile;

    private final ProductParser productParser;
    private final ProductRepository productRepository;

    @PostConstruct
    private void parseAndSaveAll() throws IOException {

        List<ProductEntity> productEntityList;

        try (FileInputStream input = new FileInputStream(productsFile.getFile())) {
            productEntityList = productParser.parse(input);
        }

        productRepository.saveAll(productEntityList);
        log.info("product list was successfully saved");
    }

    public ProductEntity getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cant find product by id"));
    }

    public List<ProductEntity> getProductListBySku(String sku) {
        return productRepository.findAllBySku(sku);
    }
}
