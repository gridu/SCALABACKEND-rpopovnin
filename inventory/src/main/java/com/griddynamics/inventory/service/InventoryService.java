package com.griddynamics.inventory.service;

import com.griddynamics.inventory.model.ProductEntity;
import com.griddynamics.inventory.parser.ProductParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    @Value(value = "classpath:data/jcpenney_com-ecommerce_sample.csv")
    private Resource productsFile;

    private Map<String, ProductEntity> inventoryProductData;

    private final ProductParser productParser;

    @PostConstruct
    private void parseAndSaveToInMemoryStructure() throws IOException {

        List<ProductEntity> productEntityList;

        try (FileInputStream input = new FileInputStream(productsFile.getFile())) {
            productEntityList = productParser.parse(input);
        }

        Random random = new Random();

        inventoryProductData = productEntityList.stream()
                .peek(e -> e.setAvailable(random.nextInt(10)))
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity(), (id, idDup) -> id));
    }

    public List<ProductEntity> getInventoryProductDataByUniqIdList(List<String> uniqIds) {
        try {
            sleep(5000);
        } catch (InterruptedException ex) {
            log.error("error occurred while thread is sleeping", ex);
        }
        return uniqIds.stream()
                .map(e -> {
                    if (inventoryProductData.containsKey(e)) {
                        return inventoryProductData.get(e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }
}
