package pl.jcommerce.activiti_test.foodorderer.service;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pl.jcommerce.activiti_test.foodorderer.entity.OrderItem;
import pl.jcommerce.activiti_test.foodorderer.repo.OrderItemRepository;

@Slf4j
@Component
public class ProcessService {

	@Autowired
	private OrderItemRepository orderItemRepository;

	public void prepare(DelegateExecution execution) {
		log.info(execution.getProcessDefinitionId() + " process start. With id=" + execution.getProcessInstanceId());

		execution.setVariable("orderId", execution.getProcessInstanceId());
	}

	public void saveOrderItem(String name, String userEmail, String orderId) {
		log.info("name={}, userEmail={}, orderId={}", name, userEmail, orderId);
		OrderItem orderItem = new OrderItem(null, name, userEmail, orderId);
		orderItemRepository.save(orderItem);
	}

	public List<String> getUserMailsByOrder(String orderId) {
		List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
		List<String> userMails = new ArrayList<>();
		orderItems.forEach(o -> userMails.add(o.getUserMail()));
		return userMails;
	}
}
