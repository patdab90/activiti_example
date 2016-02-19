package pl.jcommerce.activiti_test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pl.jcommerce.activiti_test.foodorderer.entity.OrderItem;
import pl.jcommerce.activiti_test.foodorderer.repo.OrderItemRepository;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderItemController {
	@Autowired
	private OrderItemRepository orderItemRepository;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<OrderItem> orderItems(@PathVariable String orderId) {
		log.info("Get order items by order id = {}", orderId);
		return orderItemRepository.findByOrderId(orderId);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<OrderItem> orderItems() {
		log.info("Get all order items.");
		return orderItemRepository.findAll();
	}
}
