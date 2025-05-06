package com.sasucare.controller;

import com.sasucare.model.Product;
import com.sasucare.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for handling search functionality
 */
@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    private final ProductService productService;
    
    @Autowired
    public SearchController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Search for products by keyword
     * 
     * @param query The search query
     * @param model The model to add attributes to
     * @return The search results page
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "query", required = false) String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            // If no query is provided, redirect to home page
            return "redirect:/";
        }
        
        logger.info("Searching for products with query: {}", query);
        
        // Search for products matching the query
        List<Product> searchResults = productService.searchProducts(query);
        
        // Filter out products that are not active
        searchResults = searchResults.stream()
                .filter(product -> "ACTIVE".equals(product.getStatus()))
                .toList();
        
        logger.info("Found {} active products matching query: {}", searchResults.size(), query);
        
        // Add the search results and query to the model
        model.addAttribute("products", searchResults);
        model.addAttribute("searchQuery", query);
        model.addAttribute("resultCount", searchResults.size());
        
        return "search-results";
    }
}
