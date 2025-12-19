package com.benecia.product_service.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productId; // ex: "CAT-ITEM-001"

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int unitPrice;

    public ProductEntity(String productId, String name, int stock, int unitPrice) {
        this.productId = productId;
        this.name = name;
        this.stock = stock;
        this.unitPrice = unitPrice;
    }

    public void decreaseStock(int qty) {
        this.stock -= qty;
    }

    public void increaseStock(int qty) {
        this.stock += qty;
    }
}
