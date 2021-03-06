package net.suteren.android.jidelak.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Meal implements Identificable<Meal> {

	private static final long serialVersionUID = -5185501120218440148L;
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

		int r = 0;
		if (getDish() == null) {
			if (another.getDish() != null)
				return -1;
		} else {
			if (another.getDish() == null)
				return 1;
			if (getDish() == Dish.MENU && another.getDish() != Dish.MENU)
				return -1;
			if (getDish() != Dish.MENU && another.getDish() == Dish.MENU)
				return 1;
			r = getDish().compareTo(another.getDish());
		}
		if (r != 0)
			return r;

		if (getCategory() == null) {
			if (another.getCategory() != null)
				return -1;
		} else {
			if (another.getCategory() == null)
				return 1;
			r = getCategory().compareTo(another.getCategory());
			if (r != 0)
				return r;
		}
		if (getPosition() == null) {
			if (another.getPosition() != null)
				return -1;
		} else {
			if (another.getPosition() == null)
				return 1;
			r = getPosition().compareTo(another.getPosition());
			if (r != 0)
				return r;
		}
		if (getId() == null) {
			if (another.getId() != null)
				return -1;
		} else
			r = getId().compareTo(another.getId());
		if (r != 0)
			return r;

		if (getTitle() == null) {
			if (another.getTitle() != null)
				return -1;
		} else
			r = getTitle().compareTo(another.getTitle());
		if (r != 0)
			return r;

		if (getDescription() == null) {
			if (another.getDescription() != null)
				return -1;
		} else
			r = getDescription().compareTo(another.getDescription());
		if (r != 0)
			return r;

		if (getPrice() == null) {
			if (another.getPrice() != null)
				return -1;
		} else
			r = getPrice().compareTo(another.getPrice());
		if (r != 0)
			return r;

		return 0;
	}

	private void readObject(ObjectInputStream aInputStream)
			throws ClassNotFoundException, IOException {
		aInputStream.defaultReadObject();
	}

	private void writeObject(ObjectOutputStream aOutputStream)
			throws IOException {
		// perform the default serialization for all non-transient, non-static
		// fields
		aOutputStream.defaultWriteObject();
		// aOutputStream.writeObject(resource);
		// aOutputStream.writeObject(args);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getPosition() != null) {
			sb.append(String.valueOf(getPosition()));
			sb.append(") ");
		}
		if (getAvailability() != null) {
			sb.append(getAvailability().toString());
			sb.append(": ");
		}
		if (getDish() != null) {
			sb.append(getDish().name());
			sb.append(" ");
		}
		sb.append(getTitle());
		if (getDescription() != null) {
			sb.append(" (");
			sb.append(getDescription());
			sb.append(") ");
		}
		if (getPrice() != null) {
			sb.append(" /");
			sb.append(getPrice());
			sb.append("/");
		}
		if (getCategory() != null) {
			sb.append(" - ");
			sb.append(getCategory());
		}
		if (getId() != null) {
			sb.append(" - ");
			sb.append(getId());
		}
		return sb.toString();
	}
}
