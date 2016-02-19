package pl.jcommerce.activiti_test.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FoodorderController {

	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/foodorder-process", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String startFoodOrderProcess(@RequestBody(required = true) Map<String, Object> variables) {
		List<User> users = identityService.createUserQuery().memberOfGroup("poznan-workers").list();
		variables.put("usersCollection", users);

		ProcessInstance processInstance;
		try {
			identityService.setAuthenticatedUserId("admin");
			processInstance = runtimeService.startProcessInstanceByKey("food_order", variables);
		} finally {
			identityService.setAuthenticatedUserId(null);
		}
		return processInstance.getProcessInstanceId();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/foodorder-process/task/{taskId}", method = RequestMethod.POST)
	public void completeTask(@PathVariable String taskId, @RequestBody Map<String, Object> data) {
		log.info("Complete task with id ={}, variables data = {}", taskId, data);
		taskService.complete(taskId, data);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/task/{taskId}/assignee/{userId}", method = RequestMethod.POST)
	public void assigneeUser(@PathVariable String taskId, @PathVariable String userId) {
		log.info("Assigne user with id={} to task with id={}", taskId, userId);
		taskService.claim(taskId, userId);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/user/{userId}/task", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> userTasks(@PathVariable String userId,
			@RequestParam(defaultValue = "true", name = "candidate", required = false) boolean candidate) {
		log.info("Get tasks assigned to user with id={}, with candidate tasks={}", userId, candidate);

		List<Task> tasks = candidate ? taskService.createTaskQuery().taskCandidateOrAssigned(userId).list()
				: taskService.createTaskQuery().taskAssignee(userId).list();

		return tasks.stream().map(task -> task.getId()).collect(Collectors.toList());
	}

}