package com.leyou.repository;

import ch.qos.logback.core.boolex.EvaluationException;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query(value="SELECT o.order_id,o.actual_pay, o.total_pay,o.create_time, os.status, od.sku_id,od.title ,od.image, od.num , od.own_spec, od.price FROM tb_order o LEFT JOIN tb_order_status os ON os.order_id = o.order_id LEFT JOIN tb_order_detail od ON od.order_id = o.order_id WHERE o.user_id = ?1 AND os.status = ?2 ORDER BY o.create_time DESC",
            nativeQuery=true)
    Page<Order> findByUserIdAndStatus(Long id, Integer status, Pageable pageable);
}
