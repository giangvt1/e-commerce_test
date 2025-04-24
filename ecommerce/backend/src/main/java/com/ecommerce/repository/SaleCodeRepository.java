package com.ecommerce.repository;

import com.ecommerce.model.SaleCode;
import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleCodeRepository extends JpaRepository<SaleCode, Integer> {

}
