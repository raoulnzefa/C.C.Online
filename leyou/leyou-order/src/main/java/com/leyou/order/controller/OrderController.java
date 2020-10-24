package com.leyou.order.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.service.OrderService;
import com.leyou.utils.PayHelper;
import com.leyou.utils.PayState;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("order")
@Api("Order-service")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayHelper payHelper;

    /**
     * crear pedido
     * @param order
     * @return
     */
    @PostMapping
    @ApiOperation(value = "create order, return orderId", notes = "create order")
    @ApiImplicitParam(name = "order", required = true, value = "json, include order items and logistic info.")
    public ResponseEntity<Long> createOrder(@RequestBody @Valid Order order) {
        Long id = this.orderService.createOrder(order);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /**
     * orderId -> order
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "Query order by orderId", notes = "Query Order")
    @ApiImplicitParam(name = "id", required = true, value = "Order Id")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id) {
        Order order = this.orderService.queryById(id);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(order);
    }

    /**
     * Query orderStatus by orderId
     * @param orderId
     * @return
     */
    @GetMapping("orderStatus/{id}")
    @ApiOperation(value = "Query orderStatus by orderId", notes = "Query Order status")
    @ApiImplicitParam(name = "id", required = true, value = "Order Id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "orderStatus"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "query failed")})
    public ResponseEntity<OrderStatus> queryOrderStatusById(@PathVariable("id") Long orderId) {
        OrderStatus orderStatus = this.orderService.queryOrderStatusById(orderId);
        if (orderStatus == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderStatus);
    }

    /**
     * Query orders of current user by pages
     *
     * @param status
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value = "Query orders of current user by pages and can be filtered by order status", notes = "Query orders of current user by pages")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "current page", defaultValue = "1", type = "Integer"),
            @ApiImplicitParam(name = "rows", value = "page size", defaultValue = "5", type = "Integer"),
            @ApiImplicitParam(name = "status", value = "order status: 1.creado, 2.pagado, 3. enviado, 4.entregado, 5.cerrado, 6.comentado", type = "Integer"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "pagination result"),
            @ApiResponse(code = 404, message = "no result"),
            @ApiResponse(code = 500, message = "query failed"),
    })
    public ResponseEntity<PageResult<Order>> queryUserOrderList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "status", required = false) Integer status) {
        PageResult<Order> result = this.orderService.queryUserOrderList(page, rows, status);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * update order status
     *
     * @param id
     * @param status
     * @return
     */
    @PutMapping("{id}/{status}")
    @ApiOperation(value = "update order status", notes = "update order status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "order id", type = "Long"),
            @ApiImplicitParam(name = "status", value = "order status: 1.creado, 2.pagado, 3. enviado, 4.entregado, 5.cerrado, 6.comentado", type = "Integer"),
    })

    @ApiResponses({
            @ApiResponse(code = 204, message = "true: succeed; false：failed"),
            @ApiResponse(code = 400, message = "error with parameters"),
            @ApiResponse(code = 500, message = "failed in querying")
    })
    public ResponseEntity<Boolean> updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        Boolean boo = this.orderService.updateStatus(id, status);
        if (boo == null) {
            // 400
            return ResponseEntity.badRequest().build();
        }
        // 204
        return ResponseEntity.noContent().build();
    }

    /**
     * generate link for payments
     *
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
    @ApiOperation(value = "generate link for payment by scanning QR code", notes = "generate link for payments")
    @ApiImplicitParam(name = "id", value = "order id", type = "Long")
    @ApiResponses({
            @ApiResponse(code = 200, message = "link generated"),
            @ApiResponse(code = 404, message = "failed in generating the qr code"),
            @ApiResponse(code = 500, message = "server exception"),
    })
    public ResponseEntity<String> generateUrl(@PathVariable("id") Long orderId) {
        // generate the link
        String url = this.payHelper.createPayUrl(orderId);
        if (StringUtils.isBlank(url)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(url);
    }

    /**
     * query payment status
     *
     * @param orderId
     * @return 0, fallo en consultar 1,pagado 2,operación fallada
     */
    @GetMapping("state/{id}")
    @ApiOperation(value = "query payment status", notes = "query payment status")
    @ApiImplicitParam(name = "id", value = "order id", type = "Long")
    @ApiResponses({
            @ApiResponse(code = 200, message = "0, fallo en consultar 1,pagado 2,operación fallada"),
            @ApiResponse(code = 500, message = "server exception"),
    })
    public ResponseEntity<Integer> queryPayState(@PathVariable("id") Long orderId) {
        PayState payState = this.payHelper.queryOrder(orderId);
        return ResponseEntity.ok(payState.getValue());
    }

    /**
     * Find all the articles'id included in one order
     * @param id
     * @return
     */
    @GetMapping("skuId/{id}")
    @ApiOperation(value = "query the list of all goods id by the orderid", notes = "query goods id")
    @ApiImplicitParam(name = "id", value = "orderId", type = "Long")
    @ApiResponses({
            @ApiResponse(code = 200, message = "list of goodsID"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<Long>> querySkuIdByOrderId(@PathVariable("id") Long id) {
        List<Long> skuIdList = this.orderService.querySkuIdByOrderId(id);
        if (CollectionUtils.isEmpty(skuIdList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skuIdList);
    }
}
