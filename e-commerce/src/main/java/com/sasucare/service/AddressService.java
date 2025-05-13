package com.sasucare.service;

import com.sasucare.model.Address;
import com.sasucare.model.User;
import com.sasucare.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Find all addresses for a user
     */
    public List<Address> findByUser(User user) {
        return addressRepository.findByUser(user);
    }

    /**
     * Find address by ID
     */
    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    /**
     * Save an address
     */
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Delete an address
     */
    public void delete(Address address) {
        addressRepository.delete(address);
    }

    /**
     * Create a new address for a user
     */
    public Address createAddress(User user, String street, String streetAddress, 
                                String city, String state, String postalCode, String country) {
        Address address = new Address();
        address.setUser(user);
        address.setStreet(street);
        address.setStreetAddress(streetAddress);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        address.setCreatedAt(java.time.LocalDateTime.now());
        address.setUpdatedAt(java.time.LocalDateTime.now());
        
        return addressRepository.save(address);
    }
}
