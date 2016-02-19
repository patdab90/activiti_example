package pl.jcommerce.activiti_test;


import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import pl.jcommerce.activiti_test.foodorderer.entity.MenuItem;
import pl.jcommerce.activiti_test.foodorderer.entity.Restaurant;
import pl.jcommerce.activiti_test.foodorderer.repo.MenuItemRepository;
import pl.jcommerce.activiti_test.foodorderer.repo.RestaurantRepository;

@SpringBootApplication
public class ActivitiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivitiApplication.class, args);
	}

	@Bean
	InitializingBean usersAndGroupsInitializer(final IdentityService identityService) {

		return new InitializingBean() {
			@Autowired
			private RestaurantRepository restaurantRepository;
			@Autowired
			private MenuItemRepository menuItemRepository;
			
			public void afterPropertiesSet() throws Exception {
				new GroupAssembler().withId("user").withName("users").save();
				new UserAssemler().withId("admin").withPassword("admin").save();

				Group group = new GroupAssembler().withId("poznan-workers").withName("Poznan workers").save();
				User user1 = new UserAssemler().withId("user1").withPassword("1234").withEmail("patdab90@gmail.com").save();
				User user2 = new UserAssemler().withId("user2").withPassword("1234").withEmail("patdab90@gmail.com").save();
				User user3 = new UserAssemler().withId("user3").withPassword("1234").withEmail("patdab90@gmail.com").save();

				identityService.createMembership(user1.getId(), group.getId());
				identityService.createMembership(user2.getId(), group.getId());
				identityService.createMembership(user3.getId(), group.getId());
				
				Restaurant restaurant1 = new Restaurant(null, "Sam tluszcz", "url_test");
				Restaurant restaurant2 = new Restaurant(null, "Samo zdrowie", "url_test2");
				Restaurant restaurant3 = new Restaurant(null, "Pies z budą", "url_test3");
				
				restaurantRepository.save(restaurant1);
				restaurantRepository.save(restaurant2);
				restaurantRepository.save(restaurant3);
				
				MenuItem menuItem1 = new MenuItem(null,"Kotlet z psa",0.9,0L);
				MenuItem menuItem2 = new MenuItem(null,"Kisiel z kota",0.9,0L);
				MenuItem menuItem3 = new MenuItem(null,"Bółka z chlebem",0.9,1L);
				MenuItem menuItem4 = new MenuItem(null,"Chleb posmarowany nożem",0.9,1L);
				MenuItem menuItem5 = new MenuItem(null,"Suchy ryż",0.9,2L);
				MenuItem menuItem6 = new MenuItem(null,"Nałykam się powietrza",0.9,3L);
				
				menuItemRepository.save(menuItem1);
				menuItemRepository.save(menuItem2);
				menuItemRepository.save(menuItem3);
				menuItemRepository.save(menuItem4);
				menuItemRepository.save(menuItem5);
				menuItemRepository.save(menuItem6);
			}

			class UserAssemler {
				private String id;
				private String password;
				private String email;

				public UserAssemler withEmail(String email) {
					this.email = email;
					return this;
				}

				public UserAssemler withPassword(String password) {
					this.password = password;
					return this;
				}

				public UserAssemler withId(String id) {
					this.id = id;
					return this;
				}

				public User save() {
					User user = identityService.newUser(id);
					user.setPassword(password);
					user.setEmail(email);
					identityService.saveUser(user);
					return user;
				}
			}

			class GroupAssembler {
				private String name;
				private String id;
				private String type = "security-role";

				public GroupAssembler withType(String type) {
					this.type = type;
					return this;
				}

				public GroupAssembler withName(String name) {
					this.name = name;
					return this;
				}

				public GroupAssembler withId(String id) {
					this.id = id;
					return this;
				}

				public Group save() {
					Group group = identityService.newGroup(id);
					group.setName(name);
					group.setType(type);
					identityService.saveGroup(group);
					return group;
				}
			}
		};

	}

}
