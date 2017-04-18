package com.cfranc.irc.server;

import java.util.HashSet;

public class Salon {

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Salon other = (Salon) obj;
		if (bPrivate != other.bPrivate)
			return false;
		if (nomSalon == null) {
			if (other.nomSalon != null)
				return false;
		} else if (!nomSalon.equals(other.nomSalon))
			return false;
		return true;
	}

	private String nomSalon = null;
	private boolean bPrivate = false;

	public String getNomSalon() {
		return nomSalon;
	}

	public void setNomSalon(String nomSalon) {
		this.nomSalon = nomSalon;
	}

	public boolean isbPrivate() {
		return bPrivate;
	}

	public void setbPrivate(boolean bPrivate) {
		this.bPrivate = bPrivate;
	}

	public Salon(String nomSalon, String userCreator, boolean bPrivate) {
		super();
		this.nomSalon = nomSalon;
		this.bPrivate = bPrivate;
		this.userCreator = userCreator;
		this.hUsersLogin = new HashSet<String>();
	}

	//protected User userCreator = null; // need it ?

	protected String userCreator;
	
	public String getUserCreator() {
		return userCreator;
	}

	public void setUserCreator(String userCreator) {
		this.userCreator = userCreator;
	}

	protected HashSet<String> hUsersLogin;

	public HashSet<String> gethUsersLogin() {
		return hUsersLogin;
	}

}
