package com.griddynamics.inventory.controller;

import com.griddynamics.inventory.model.ProductEntity;
import com.griddynamics.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<ProductEntity>> getInventoryProductDataByUniqIdList(@RequestBody List<String> uniqIds) {
        List<ProductEntity> products = inventoryService.getInventoryProductDataByUniqIdList(uniqIds);
        return CollectionUtils.isEmpty(products)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(products);

    }

}
