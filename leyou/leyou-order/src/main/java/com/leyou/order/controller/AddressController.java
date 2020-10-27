package com.leyou.order.controller;

import com.leyou.order.pojo.Address;
import com.leyou.order.service.AddressService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("order/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * save new address
     * @param address
     * @return
     */
    @PostMapping
    @ApiOperation(value = "create address api",notes = "generate new address")
    @ApiImplicitParam(name = "address",required = true,value = "obj of address")
    @ApiResponses({
            @ApiResponse(code = 201, message = "address created"),
            @ApiResponse(code = 500,message = "internal server exception")
    })
    public ResponseEntity<Void> saveAddressByUserId (@RequestBody @Valid Address address) {
        this.addressService.saveAddressByUserId(address);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * query list of addresses by userId
     * @return
     */
    @GetMapping
    @ApiOperation(value = "return list of addresses",notes = "query addresses")
    @ApiResponses({
            @ApiResponse(code = 200, message = "list of addresses"),
            @ApiResponse(code = 404,message = "not found"),
            @ApiResponse(code = 500,message = "server exception")
    })
    public ResponseEntity<List<Address>> queryAddressesByUserId() {
        List<Address> addresses = this.addressService.queryAddressesByUserId();
        if (CollectionUtils.isEmpty(addresses)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(addresses);
    }

    /**
     * modify address
     * @param address
     * @return
     */
    @PutMapping
    @ApiOperation(value = "modify address",notes = "modify address")
    @ApiImplicitParam(name = "address", required=true, value = "obj of address")
    @ApiResponses({
            @ApiResponse(code = 204, message = "updated"),
            @ApiResponse(code = 500,message = "server exception")
    })
    public ResponseEntity<Void> updateAddressByUserId(@RequestBody Address address) {
        this.addressService.updateAddressByUserId(address);
        return ResponseEntity.noContent().build();
    }

    /**
     * delete address
     * @param addressId
     * @return
     */
    @ApiOperation(value = "delete address",notes = "delete address")
    @ApiImplicitParam(name = "addressId", required=true, value = "AddressId")
    @ApiResponses({
            @ApiResponse(code = 204, message = "deleted"),
            @ApiResponse(code = 500,message = "sever exception")
    })
    @DeleteMapping("{addressId}")
    public ResponseEntity<Void> deleteAddress (@PathVariable("addressId") Long addressId) {
        this.addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    /**
     * query adrress by addressId
     * @param id
     * @return
     */
    @GetMapping("{addressId}")
    @ApiOperation(value = "query adrress by addressId",notes = "query adrress by addressId")
    @ApiImplicitParam(name = "addressId", required=true, value = "adrressId")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 404, message = "failed"),
            @ApiResponse(code = 500,message = "exception")
    })
    public ResponseEntity<Address> queryAddressById(@PathVariable("addressId") Long id) {
        Address address = this.addressService.queryAddressById(id);
        if (address == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }



}
