package pl.jcommerce.activiti_test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.subethamail.wiser.Wiser;

import pl.jcommerce.activiti_test.foodorderer.entity.MenuItem;
import pl.jcommerce.activiti_test.foodorderer.entity.Restaurant;
import pl.jcommerce.activiti_test.foodorderer.repo.MenuItemRepository;
import pl.jcommerce.activiti_test.foodorderer.repo.RestaurantRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ActivitiApplication.class })
@WebIntegrationTest
public class FoodOrderProcessIntegrationTest {
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;

	private Wiser wiser;
	private Restaurant restaurant;
	private List<MenuItem> menuItems;

	@Before
	public void setup() {
		wiser = new Wiser();
		wiser.setPort(1025);
		wiser.start();

		restaurant = restaurantRepository.findAll().get(0);
		menuItems = menuItemRepository.findByRestaurantId(restaurant.getId());
		assertThat(menuItems.size(), is(2));
	}

	@After
	public void cleanup() {
		wiser.stop();
	}

	@Test
	public void testHappyPath() {
		// Start process
		List<User> users = identityService.createUserQuery().memberOfGroup("poznan-workers").list();
		assertThat(users.size(), is(3));
		Map<String, Object> processVariables = new HashMap<String, Object>();
		processVariables.put("usersCollection", users);

		ProcessInstance processInstance = startProcessAs("admin","food_order",processVariables);

		// First task - find Fast food
		Task findFastfoodTask = taskService.createTaskQuery().processInstanceId(processInstance.getId())
				.taskCandidateUser("admin").singleResult();
		assertThat(findFastfoodTask.getName(), is("Find FastFood"));

		// End of selection restaurant
		LocalDateTime orderEndDate = LocalDateTime.now().plusMinutes(10);

		Map<String, Object> findFastfoodtaskVariables = new HashMap<String, Object>();
		findFastfoodtaskVariables.put("restaurantName", restaurant.getName());
		findFastfoodtaskVariables.put("restaurantUrl", restaurant.getUrl());
		findFastfoodtaskVariables.put("endTime", orderEndDate.toString());
		taskService.complete(findFastfoodTask.getId(), findFastfoodtaskVariables);

		// Verify first email
		assertThat(wiser.getMessages().size(), is(3));

		// Vote tasks
		List<Task> voteTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().list();
		assertThat(voteTasks, notNullValue());
		assertThat(voteTasks.size(), is(3));

		Map<String, Object> voteTaskVariables = new HashMap<String, Object>();
		voteTaskVariables.put("vote", true);
		
		taskService.complete(voteTasks.get(0).getId(), voteTaskVariables);

		List<Task> voteTasks2 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().list();
		assertThat(voteTasks2.size(), is(2));
		assertThat(voteTasks2.get(0).getName(), is("Vote"));
		assertThat(voteTasks2.get(1).getName(), is("Vote"));
		
		taskService.complete(voteTasks2.get(0).getId(), voteTaskVariables);
		
		List<Task> selectMealTasks = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertThat(selectMealTasks.get(0).getName(), is("Select meal"));
		assertThat(selectMealTasks.get(1).getName(), is("Select meal"));
		assertThat(selectMealTasks.get(2).getName(), is("Select meal"));

		// End vote tasks
		Map<String, Object> selectMealTaskVariables1 = new HashMap<String, Object>();
		selectMealTaskVariables1.put("menuItemName", menuItems.get(0).getName());
		taskService.complete(selectMealTasks.get(0).getId(), selectMealTaskVariables1);

		Map<String, Object> selectMealTaskVariables2 = new HashMap<String, Object>();
		selectMealTaskVariables1.put("menuItemName", menuItems.get(1).getName());
		taskService.complete(selectMealTasks.get(1).getId(), selectMealTaskVariables2);

		Map<String, Object> selectMealTaskVariables3 = new HashMap<String, Object>();
		selectMealTaskVariables1.put("menuItemName", menuItems.get(1).getName());
		taskService.complete(selectMealTasks.get(2).getId(), selectMealTaskVariables3);

		// Order task
		Task orderTask = taskService.createTaskQuery().orderByTaskName().asc().singleResult();
		assertThat(orderTask.getName(), is("Order food"));

		Map<String, Object> orderFoodVariables = new HashMap<String, Object>();
		orderFoodVariables.put("deliveryTime", LocalTime.now().plusHours(1).toString());
		taskService.complete(orderTask.getId(), orderFoodVariables);

		// Verify second email
		assertThat(wiser.getMessages().size(), is(6));
	}

	private ProcessInstance startProcessAs(String userId,String processKey, Map<String, Object> variables) {
		ProcessInstance processInstance;
		try {
			identityService.setAuthenticatedUserId(userId);
			processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);
		} finally {
			identityService.setAuthenticatedUserId(null);
		}
		assertThat(processInstance, notNullValue());
		return processInstance;
	}

}
