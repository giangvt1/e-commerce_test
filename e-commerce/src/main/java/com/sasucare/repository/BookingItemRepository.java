package com.sasucare.repository;

import com.sasucare.model.Booking;
import com.sasucare.model.BookingItem;
import com.sasucare.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {
    List<BookingItem> findByBooking(Booking booking);
    List<BookingItem> findByProduct(Product product);
}
