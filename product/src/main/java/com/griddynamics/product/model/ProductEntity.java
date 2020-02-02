package com.griddynamics.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    private String id;

    private String sku;

    private String nameTitle;

    private String description;

    private String listPrice;

    private String salePrice;

    private String category;

    private String categoryTree;

    private String averageProductRating;

    private String productUrl;

    private String productImageUrls;

    private String brand;

    private Long totalNumberReviews;

    private String reviews;

}
