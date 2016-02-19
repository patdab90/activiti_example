package pl.jcommerce.activiti_test.foodorderer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "manuitems")
public class MenuItem {
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Long id;
	private String name;
	private Double price;
	@Column(nullable = false)
	private long restaurantId;
}
