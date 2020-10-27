package com.leyou.order.service;

import com.leyou.common.pojo.UserInfo;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.pojo.Address;
import com.leyou.order.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * save new address
     * @param address
     * @return
     */
    public void saveAddressByUserId(Address address) {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        address.setId(null);
        address.setUserId(userInfo.getId());
        setDefaultAddress(address);
        this.addressRepository.save(address);
    }

    private void setDefaultAddress(Address address) {
        if (address.isDefaultAddress()) {
            //if this default address exists, then the rest of addresses under this user cannot be default
            List<Address> addressList = this.queryAddressesByUserId();
            addressList.forEach(address1 -> {
                if (address1.isDefaultAddress()) {
                    address1.setDefaultAddress(false);
                    this.addressRepository.save(address1);
                }
            });
        }
    }

    /**
     * query list of addresses by userId
     * @return
     */
    public List<Address> queryAddressesByUserId() {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        return this.addressRepository.findByUserId(userInfo.getId());
    }

    /**
     * modify address
     * @param address
     * @return
     */
    public void updateAddressByUserId(Address address) {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        address.setUserId(userInfo.getId());
        setDefaultAddress(address);
        this.addressRepository.save(address);
    }

    /**
     * delete address
     * @param addressId
     * @return
     */
    public void deleteAddress(Long addressId) {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        Address address = this.addressRepository.findByUserIdAndId(userInfo.getId(), addressId);
        this.addressRepository.delete(address);
    }

    /**
     * query adrress by addressId
     * @param id
     * @return
     */
    public Address queryAddressById(Long id) {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        return this.addressRepository.findByUserIdAndId(userInfo.getId(), id);
    }
}
