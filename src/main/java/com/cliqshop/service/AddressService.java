package com.cliqshop.service;

import com.cliqshop.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> getAddressesByUserId(Long userId);
    Address getAddressById(Long addressId);
    Address saveAddress(Address address);
    void deleteAddress(Long addressId);
    Address getDefaultAddress(Long userId);
    Address getDefaultAddressByType(Long userId, Address.AddressType type);
    Address setDefaultAddress(Long addressId);
}