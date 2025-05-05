package com.sasucare.controller;

import com.sasucare.model.Category;
import com.sasucare.model.Product;
import com.sasucare.service.CategoryService;
import com.sasucare.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for handling homepage and general navigation
 */
@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Handles requests to the home page
     * Loads products and categories from database and displays them on the homepage
     */
    @GetMapping(value = {"", "/", "/home"})
    public String home(Model model, @RequestParam(required = false) Long categoryId) {
        // Get all active products
        List<Product> products;
        
        if (categoryId != null) {
            // Filter products by category if categoryId is provided
            products = productService.getProductsByCategory(categoryId);
        } else {
            // Otherwise get all available products
            products = productService.getAvailableProducts();
        }
        
        // Get all categories
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        
        return "home";
    }

    /**
     * Redirect old shop page to home
     */
    @GetMapping("/shop")
    public String shop() {
        return "redirect:/home";
    }

    /**
     * Display about us page
     */
    @GetMapping("/about-us")
    public String aboutUs() {
        return "about-us";
    }

    /**
     * Display contact page
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    /**
     * Display collaborations page
     */
    @GetMapping("/collabs")
    public String collabs() {
        return "collabs";
    }

    /**
     * Display the FAQ page
     */
    @GetMapping("/faqs")
    public String faqs() {
        return "faqs";
    }
}
