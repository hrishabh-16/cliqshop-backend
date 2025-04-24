package com.cliqshop.service;

import com.cliqshop.entity.Address;
import com.cliqshop.repository.AddressRepository;
import com.cliqshop.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserUserId(userId);
    }

    @Override
    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    @Override
    @Transactional
    public Address saveAddress(Address address) {
        // If this address is set as default, unset any other default addresses of same type
        if (address.isDefault()) {
            List<Address> existingAddresses = addressRepository.findByUserUserIdAndAddressTypeAndIsDefaultTrue(
                    address.getUser().getUserId(), address.getAddressType());
            
            for (Address existingAddress : existingAddresses) {
                if (!existingAddress.getAddressId().equals(address.getAddressId())) {
                    existingAddress.setDefault(false);
                    addressRepository.save(existingAddress);
                }
            }
        }
        
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        return addressRepository.findByUserUserIdAndIsDefaultTrue(userId)
                .stream().findFirst().orElse(null);
    }

    @Override
    public Address getDefaultAddressByType(Long userId, Address.AddressType type) {
        return addressRepository.findByUserUserIdAndAddressTypeAndIsDefaultTrue(userId, type)
                .stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long addressId) {
        Address address = getAddressById(addressId);
        
        // Unset any other default addresses of same type
        List<Address> existingAddresses = addressRepository.findByUserUserIdAndAddressTypeAndIsDefaultTrue(
                address.getUser().getUserId(), address.getAddressType());
        
        for (Address existingAddress : existingAddresses) {
            existingAddress.setDefault(false);
            addressRepository.save(existingAddress);
        }
        
        // Set this address as default
        address.setDefault(true);
        return addressRepository.save(address);
    }
}