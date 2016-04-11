package net.suteren.android.jidelak.model;


public enum Dish {

	BREAKFAST("breakfast"), SNACK("snack"), STARTER("starter"), SOUP("soup"), DINNER(
			"dinner"), TRIMMINGS("trimmings"), DESSERT("desert"), LUNCH("lunch"), SUPPER(
			"supper"), MENU("menu"), DRINK("drink"), WINE("wine");

	private String resource;

	private Dish(String resource) {
		this.resource = resource;
	}

	public String getResourceName() {
		return resource;
	}

}
