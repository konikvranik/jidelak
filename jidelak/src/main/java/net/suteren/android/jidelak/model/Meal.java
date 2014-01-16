package net.suteren.android.jidelak.model;

public class Meal implements Identificable, Comparable<Meal> {

	private String title;
	private String description;
	private String category;
	private Dish dish;
	private Restaurant restaurant;
	private Availability availability;
	private Long id;
	private String price;
	private Source source;
	private Integer position;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public void setPosition(Integer unpackColumnValue) {
		this.position = unpackColumnValue;
	}

	public Integer getPosition() {
		return position;
	}

	@Override
	public int compareTo(Meal another) {

		if (another == null)
			return 1;

		if (getDish() == null && another.getDish() != null)
			return -1;
		int r = getDish().compareTo(another.getDish());
		if (r != 0)
			return r;

		if (getPosition() == null && another.getPosition() != null)
			return -1;
		r = getPosition().compareTo(another.getPosition());
		if (r != 0)
			return r;

		if (getId() == null && another.getId() != null)
			return -1;
		r = getId().compareTo(another.getId());
		if (r != 0)
			return r;

		if (getTitle() == null && another.getTitle() != null)
			return -1;
		r = getTitle().compareTo(another.getTitle());
		if (r != 0)
			return r;

		if (getDescription() == null && another.getDescription() != null)
			return -1;
		r = getDescription().compareTo(another.getDescription());
		if (r != 0)
			return r;

		if (getPrice() == null && another.getPrice() != null)
			return -1;
		r = getPrice().compareTo(another.getPrice());
		if (r != 0)
			return r;

		return 0;
	}
}
