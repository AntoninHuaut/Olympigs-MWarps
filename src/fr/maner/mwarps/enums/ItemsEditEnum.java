package fr.maner.mwarps.enums;

public enum ItemsEditEnum {
	
	ITEM("Item", "l'"),
	NOM("Nom", "le"),
	LORES("Lores", "les");
	
	private String nom;
	private String deter;
	
	private ItemsEditEnum(String nom, String deter) {
		this.nom = nom;
		this.deter = deter;
	}

	public String getNom() {
		return nom;
	}
	
	@Override
	public String toString() {
		return deter + (deter.charAt(deter.length() - 1) == '\'' ? "" : " ") + nom;
	}
}
