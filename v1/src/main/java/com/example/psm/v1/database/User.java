package com.example.psm.v1.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String name;
	private String email;
	private String houseID;
	private String area;
	private String address;
	private String identityCard;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHouseID(){
		return houseID;
	}

	public void setHouseID(String houseID){
		this.houseID = houseID;
	}

	public String getArea(){
		return this.area;
	}

	public void setArea(String area){
		this.area = area;
	}

	public String getAddress(){
		return address;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getIdentityCard(){
		return identityCard;
	}
	public void setIdentityCard(String identityCard){
		this.identityCard = identityCard;
	}


}