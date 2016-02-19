package pl.jcommerce.activiti_test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Applicant {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String email;

	private String phoneNumber;

	public Applicant() {

	}

	public Applicant(String name, String email, String phoneNumber) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

}