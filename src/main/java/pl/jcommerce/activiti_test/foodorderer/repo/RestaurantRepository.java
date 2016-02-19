package pl.jcommerce.activiti_test.foodorderer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.jcommerce.activiti_test.foodorderer.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}
