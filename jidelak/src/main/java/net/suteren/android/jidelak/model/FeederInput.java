package net.suteren.android.jidelak.model;

import java.net.URL;

import javax.xml.transform.Templates;

public class FeederInput {
	URL url;
	Templates templates;

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Templates getTemplates() {
		return templates;
	}

	public void setTemplates(Templates templates) {
		this.templates = templates;
	}

}
