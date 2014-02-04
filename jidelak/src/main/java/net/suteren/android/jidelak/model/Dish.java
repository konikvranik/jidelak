package net.suteren.android.jidelak.model;

import net.suteren.android.jidelak.R;

public enum Dish {

	BREAKFAST(R.string.breakfast), SNACK(R.string.snack), STARTER(
			R.string.starter), SOUP(R.string.soup), DINNER(R.string.dinner), TRIMMINGS(
			R.string.trimmings), DESSERT(R.string.desert), LUNCH(R.string.lunch), SUPPER(
			R.string.supper), MENU(R.string.menu);

	private int resource;

	private Dish(int resource) {
		this.resource = resource;
	}

	public int getResource() {
		return resource;
	}

}
