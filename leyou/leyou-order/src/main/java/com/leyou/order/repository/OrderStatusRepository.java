package com.leyou.order.repository;

import com.leyou.order.pojo.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long>, JpaSpecificationExecutor<OrderStatus> {
}
