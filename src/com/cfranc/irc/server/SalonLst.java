package com.cfranc.irc.server;

import java.util.ArrayList;

public class SalonLst  {

	private ArrayList <Salon> lstSalons; // list des salons
	
	public ArrayList<Salon> getLstSalons() {
		return lstSalons;
	}

	public SalonLst(){
		this.lstSalons = new ArrayList<Salon>();

		// Cr�ation du salon "G�n�ral"
		this.lstSalons.add(new Salon("G�n�ral", "", false));

	}

	public Salon get(int i) {
		
		return (Salon) lstSalons.get(i);
		
	}

	public int findSalonIndexByName(String salonName) {
		int res=0;
		for(int i=0;i<this.lstSalons.size();i++) {
			if(this.lstSalons.get(i).getNomSalon().equals(salonName)) {
				res=i;
				break;
			}
		}
		return res;
	}

}
