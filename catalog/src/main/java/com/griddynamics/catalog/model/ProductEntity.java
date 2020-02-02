package com.griddynamics.catalog.model;

import com.griddynamics.catalog.converter.StringWithCommaToLongConverter;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    @Id
    @Column(name = "uniq_id")
    @Parsed(field = "uniq_id")
    private String id;

    @Column(name = "sku")
    @Parsed
    private String sku;

    @Column(name = "name_title")
    @Parsed(field = "name_title")
    private String nameTitle;

    @Column(name = "description")
    @Parsed
    @Lob
    private String description;

    @Column(name = "list_price")
    @Parsed(field = "list_price")
    private String listPrice;

    @Column(name = "sale_price")
    @Parsed(field = "sale_price")
    private String salePrice;

    @Column(name = "category")
    @Parsed
    private String category;

    @Column(name = "category_tree")
    @Parsed(field = "category_tree")
    private String categoryTree;

    @Column(name = "average_product_rating")
    @Parsed(field = "average_product_rating")
    private String averageProductRating;

    @Column(name = "product_url")
    @Lob
    @Parsed(field = "product_url")
    private String productUrl;

    @Column(name = "product_image_urls")
    @Lob
    @Parsed(field = "product_image_urls")
    private String productImageUrls;

    @Column(name = "brand")
    @Parsed
    private String brand;

    @Column(name = "total_number_reviews")
    @Parsed(field = "total_number_reviews", applyDefaultConversion = false)
    @Convert(conversionClass = StringWithCommaToLongConverter.class)
    private Long totalNumberReviews;

    @Column(name = "reviews")
    @Lob
    @Parsed
    private String reviews;

}
