package com.cfranc.irc.server;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String login;
	private String pwd;
	private int idSalon;
	private List<Salon> salons;
	
	public List<Salon> getSalons() {
		return salons;
	}

	public void setSalons(List<Salon> salons) {
		this.salons = salons;
	}

	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
		protected int getIdSalon() {
		return idSalon;
	}

	protected void setIdSalon(int idSalon) {
		this.idSalon = idSalon;
	}

	public User(String login, String pwd, int userSalon) {
		super();
		this.login = login;
		this.pwd = pwd;
		this.idSalon=userSalon;
		this.salons = new ArrayList<Salon>();
	}	
	
}
