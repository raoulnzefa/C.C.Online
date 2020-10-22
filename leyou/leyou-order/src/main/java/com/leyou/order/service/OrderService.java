package com.leyou.order.service;


import com.leyou.common.pojo.PageResult;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.IdWorker;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.repository.OrderDetailRepository;
import com.leyou.order.repository.OrderRepository;
import com.leyou.order.repository.OrderStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public Long createOrder(Order order) {
        // generate orderId
        long orderId = idWorker.nextId();
        // get userInfo
        UserInfo user = LoginInterceptor.getLoginUser();
        // Initialize data
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        order.setCreateTime(new Date());
        order.setOrderId(orderId);
        order.setUserId(user.getId());
        // save order
        this.orderRepository.save(order);

        // save orderStatus
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setStatus(1);// initial status: order created(not be paid)

        this.orderStatusRepository.save(orderStatus);

        // add orderId into orderDetails
        order.getOrderDetails().forEach(od -> od.setOrderId(orderId));
        // save orderDetails with bulk
        this.orderDetailRepository.saveAll(order.getOrderDetails());
        //decrease stock


        logger.debug("create order, orderId:{}, userId:{}", orderId, user.getId());

        return orderId;
    }

    public Order queryById(Long id) {
        // order
        Optional<Order> optionalOrder = this.orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            // orderDetail
            List<OrderDetail> details = this.orderDetailRepository.findByOrderId(id);
            order.setOrderDetails(details);
            // orderStatus
            Optional<OrderStatus> orderStatusOptional = this.orderStatusRepository.findById(id);
            if (orderStatusOptional.isPresent()) {
                OrderStatus status = orderStatusOptional.get();
                order.setStatus(status.getStatus());
            }
            return order;
        }
        return null;
    }

    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status) {
        try {
            // get userInfo
            UserInfo user = LoginInterceptor.getLoginUser();
            Pageable pageable = PageRequest.of(page - 1, rows);
            Page<Order> orderList;
            if (status != null) {
                orderList = this.orderRepository.queryOrderList(user.getId(), status, pageable);
            } else {
                orderList = this.orderRepository.queryOrderList(user.getId(), pageable);
            }
            List<Order> orderListContent = orderList.getContent();
            orderListContent.forEach(order -> {
                List<OrderDetail> orderDetailList = this.orderDetailRepository.findByOrderId(order.getOrderId());
                order.setOrderDetails(orderDetailList);
            });
            return new PageResult<>(orderList.getTotalElements(), orderList.getTotalPages(), orderListContent);

        } catch (Exception e) {
            logger.error("error al buscar el listado de pedidos", e);
            return null;
        }
    }

    @Transactional
    public Boolean updateStatus(Long id, Integer status) {
        Optional<OrderStatus> optional = this.orderStatusRepository.findById(id);
        if (optional.isPresent()) {
            OrderStatus record = optional.get();
            record.setOrderId(id);
            record.setStatus(status);
            // edit time by various statuses
            switch (status) {
                case 2:
                    record.setPaymentTime(new Date());// pagado
                    break;
                case 3:
                    record.setConsignTime(new Date());// enviado
                    break;
                case 4:
                    record.setEndTime(new Date());// entregado
                    break;
                case 5:
                    record.setCloseTime(new Date());// cerrado
                    break;
                case 6:
                    record.setCommentTime(new Date());// comentado
                    break;
                default:
                    return null;
            }
            this.orderStatusRepository.save(record);
            return true;
        }
        return null;
    }

    /**
     * Find all the articles'id included in one order
     * @param id
     * @return
     */
    public List<Long> querySkuIdByOrderId(Long id) {
        List<OrderDetail> orderDetails = this.orderDetailRepository.findByOrderId(id);
        List<Long> ids = new ArrayList<>();
        orderDetails.forEach(orderDetail -> {
            ids.add(orderDetail.getSkuId());
        });
        return ids;
    }
}
