package com.cliqshop.controller;

import com.cliqshop.entity.Address;
import com.cliqshop.entity.User;
import com.cliqshop.service.AddressService;
import com.cliqshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Address>> getAddressesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressesByUserId(userId));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Address> createAddress(@PathVariable Long userId, @RequestBody Address address) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        address.setUser(user);
        return ResponseEntity.ok(addressService.saveAddress(address));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long addressId, @RequestBody Address addressDetails) {
        Address existingAddress = addressService.getAddressById(addressId);
        
        existingAddress.setAddressLine1(addressDetails.getAddressLine1());
        existingAddress.setAddressLine2(addressDetails.getAddressLine2());
        existingAddress.setCity(addressDetails.getCity());
        existingAddress.setState(addressDetails.getState());
        existingAddress.setPostalCode(addressDetails.getPostalCode());
        existingAddress.setCountry(addressDetails.getCountry());
        existingAddress.setDefault(addressDetails.isDefault());
        existingAddress.setAddressType(addressDetails.getAddressType());
        
        return ResponseEntity.ok(addressService.saveAddress(existingAddress));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }		

    @GetMapping("/user/{userId}/default")
    public ResponseEntity<Address> getDefaultAddress(@PathVariable Long userId, 
                                                    @RequestParam(required = false) Address.AddressType type) {
        Address address;
        if (type != null) {
            address = addressService.getDefaultAddressByType(userId, type);
        } else {
            address = addressService.getDefaultAddress(userId);
        }
        return ResponseEntity.ok(address);
    }
    
    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<Address> setDefaultAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.setDefaultAddress(addressId));
    }
}