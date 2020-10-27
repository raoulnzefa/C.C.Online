package com.leyou.order.repository;

import com.leyou.order.pojo.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query(value= "select * from tb_order o left join tb_order_status os on o.order_id = os.order_id where  o.user_id = ?1 order by o.create_time DESC ", nativeQuery = true)
    Page<Order> queryOrderList(Long id, Pageable pageable);

    @Query(value= "select * from tb_order o left join tb_order_status os on o.order_id = os.order_id where  o.user_id = ?1 and os.status = ?2 order by o.create_time DESC ", nativeQuery = true)
    Page<Order> queryOrderList(Long id, Integer status, Pageable pageable);

}
