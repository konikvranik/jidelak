package net.suteren.android.jidelak.model;

public interface Identificable<T> extends Comparable<T> {
	Long getId();

	void setId(Long id);
}
