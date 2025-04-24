package com.cliqshop.repository;

import com.cliqshop.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserUserId(Long userId);
    List<Address> findByUserUserIdAndIsDefaultTrue(Long userId);
    List<Address> findByUserUserIdAndAddressTypeAndIsDefaultTrue(Long userId, Address.AddressType addressType);
}