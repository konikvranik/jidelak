package net.suteren.android.jidelak.model;

import java.io.Serializable;

public interface Identificable<T> extends Comparable<T>, Serializable {
	Long getId();

	void setId(Long id);
}
