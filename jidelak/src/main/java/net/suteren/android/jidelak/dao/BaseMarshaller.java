package net.suteren.android.jidelak.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class BaseMarshaller<T> {

	protected Map<String, String> data = new HashMap<String, String>();

	Stack<Node> path = new Stack<Node>();

	protected abstract T marshallHelper(String prefix, Map<String, String> data);

	public void clean() {
		synchronized (path) {
			data = new HashMap<String, String>();
			path.clear();
		}
	}

	public T marshall(Node n) {
		return marshall(null, n);
	}

	public T marshall(String prefix, Node n) {
		synchronized (path) {
			while (n != null) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					path.push(n);
					if (n.hasAttributes())
						processAttributes(n);
					n = getNextNode(n);
				} else if (n.getNodeType() == Node.TEXT_NODE) {
					data.put(path(), n.getTextContent());
					n = getNextNode(n);
				}
			}
			path.clear();
		}
		return marshallHelper(prefix, data);
	}

	protected Node getNextNode(Node n) {
		if (n.hasChildNodes())
			return n = n.getFirstChild();
		n = n.getNextSibling();
		while (n == null && !path.isEmpty()) {
			n = path.pop().getNextSibling();
		}
		return n;
	}

	protected String path() {
		StringBuffer sb = new StringBuffer();
		for (Node element : path) {
			sb.append(element.getNodeName());
			sb.append(".");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	protected void processAttributes(Node n) {
		NamedNodeMap na = n.getAttributes();
		for (int i = 0; i < na.getLength(); i++) {
			n = na.item(i);
			data.put(path + "@" + n.getNodeName(), n.getNodeValue());
		}
	}

}
