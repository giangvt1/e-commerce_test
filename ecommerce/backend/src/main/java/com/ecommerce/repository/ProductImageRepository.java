package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

}
