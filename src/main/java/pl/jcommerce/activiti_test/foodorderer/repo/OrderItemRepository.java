package pl.jcommerce.activiti_test.foodorderer.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.jcommerce.activiti_test.foodorderer.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	List<OrderItem> findByOrderId(String orderId);
}
