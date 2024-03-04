package com.example.demo.sample00.model;

import lombok.Data;

@Data
public class User {
	private long id;
	private String first_name;
	private String last_name;
	private String email;
	private String gender;
	private String ip_address;
	private String country_code;
}
