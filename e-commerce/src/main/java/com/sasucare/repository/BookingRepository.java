package com.sasucare.repository;

import com.sasucare.model.Booking;
import com.sasucare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer(User customer);
    List<Booking> findBySeller(User seller);
    List<Booking> findByBookingStatus(String status);
    List<Booking> findBySellerAndBookingStatus(User seller, String status);
    List<Booking> findByCustomerAndBookingStatus(User customer, String status);
    Optional<Booking> findByBookingNumber(String bookingNumber);
}
