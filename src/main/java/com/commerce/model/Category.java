package com.commerce.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_generator")
    @SequenceGenerator(name = "category_generator",
            sequenceName = "category_seq",
            allocationSize = 1) // allocationSize define o incremento do valor
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    // O método setId pode ser removido para evitar a alteração manual do ID
    // public void setId(Long id) {
    //     this.id = id;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
