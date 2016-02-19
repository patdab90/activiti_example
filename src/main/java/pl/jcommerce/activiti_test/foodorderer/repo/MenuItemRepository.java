package pl.jcommerce.activiti_test.foodorderer.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.jcommerce.activiti_test.foodorderer.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
	List<MenuItem> findByRestaurantId(long restaurantId);
}
