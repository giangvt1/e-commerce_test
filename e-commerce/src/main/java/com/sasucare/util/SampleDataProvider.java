package com.sasucare.util;

import com.sasucare.model.Product;
import com.sasucare.model.User;
import com.sasucare.model.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to provide sample data for demonstration purposes
 * This is used temporarily until the database integration is complete
 */
public class SampleDataProvider {

    /**
     * Generate sample products for the homepage
     * @return List of sample products
     */
    public static List<Product> getSampleProducts() {
        List<Product> products = new ArrayList<>();
        
        // Create a sample seller
        User seller = new User();
        seller.setId(1L);
        seller.setEmail("seller@example.com");
        seller.setShopName("Sample Shop");
        
        // Create sample categories
        Category clothingCategory = new Category();
        clothingCategory.setId(1L);
        clothingCategory.setName("Clothes");
        clothingCategory.setDescription("Clothing and apparel products");
        
        Category foodCategory = new Category();
        foodCategory.setId(2L);
        foodCategory.setName("Food");
        foodCategory.setDescription("Food and beverage products");
        
        Category electronicsCategory = new Category();
        electronicsCategory.setId(3L);
        electronicsCategory.setName("Electronics");
        electronicsCategory.setDescription("Electronic devices and gadgets");
        
        Category homeCategory = new Category();
        homeCategory.setId(4L);
        homeCategory.setName("Home & Garden");
        homeCategory.setDescription("Products for home and garden");
        
        // CLOTHING PRODUCTS
        
        // Sample Product 1 - Clothing
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("meo'omdau'");
        product1.setDescription("Premium embroidered black t-shirt with DIRTYCZN King design");
        product1.setPrice(new BigDecimal("155500"));
        product1.setStockQuantity(11);
        product1.setSku("122");
        product1.setSeller(seller);
        product1.setCategory(clothingCategory);
        product1.setStatus("ACTIVE");
        product1.setPrimaryImageUrl("https://via.placeholder.com/300x300/000000/ffffff?text=meo+omdau");
        product1.setBadgeText("New arrival");
        product1.setCreatedAt(LocalDateTime.now());
        product1.setUpdatedAt(LocalDateTime.now());
        products.add(product1);
        
        // FOOD PRODUCTS
        
        // Sample Product 2 - Food
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("meo heeeheh");
        product2.setDescription("Delicious cat treats");
        product2.setPrice(new BigDecimal("155500"));
        product2.setStockQuantity(1);
        product2.setSku("122");
        product2.setSeller(seller);
        product2.setCategory(foodCategory);
        product2.setStatus("ACTIVE");
        product2.setPrimaryImageUrl("https://via.placeholder.com/300x300/ffffff/000000?text=meo+heeeheh");
        product2.setBadgeText("Hot");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        products.add(product2);
        
        // More products for other categories
        
        // Sample Product 3 - Electronics
        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Smart Watch X1");
        product3.setDescription("Advanced smartwatch with health tracking features");
        product3.setPrice(new BigDecimal("1250000"));
        product3.setStockQuantity(8);
        product3.setSku("EL001");
        product3.setSeller(seller);
        product3.setCategory(electronicsCategory);
        product3.setStatus("ACTIVE");
        product3.setPrimaryImageUrl("https://via.placeholder.com/300x300/0066CC/ffffff?text=Smart+Watch");
        product3.setBadgeText("New Tech");
        product3.setCreatedAt(LocalDateTime.now());
        product3.setUpdatedAt(LocalDateTime.now());
        products.add(product3);
        
        // Sample Product 4 - Home & Garden
        Product product4 = new Product();
        product4.setId(4L);
        product4.setName("Exotic Plant Collection");
        product4.setDescription("Set of 3 rare indoor plants with decorative pots");
        product4.setPrice(new BigDecimal("350000"));
        product4.setStockQuantity(5);
        product4.setSku("HG003");
        product4.setSeller(seller);
        product4.setCategory(homeCategory);
        product4.setStatus("ACTIVE");
        product4.setPrimaryImageUrl("https://via.placeholder.com/300x300/00AA00/ffffff?text=Plants");
        product4.setBadgeText("Eco-friendly");
        product4.setCreatedAt(LocalDateTime.now());
        product4.setUpdatedAt(LocalDateTime.now());
        products.add(product4);
        
        return products;
    }
}
