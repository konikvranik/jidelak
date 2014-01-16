package net.suteren.android.jidelak.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.suteren.android.jidelak.JidelakException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class BaseMarshaller<T> {

	protected Map<String, String> data = new HashMap<String, String>();

	Stack<Node> path = new Stack<Node>();

	private Node root;

	protected abstract void unmarshallHelper(String prefix,
			Map<String, String> data, T object) throws JidelakException;

	public void clean() {
		synchronized (path) {
			data = new HashMap<String, String>();
			path.clear();
		}
	}

	public void unmarshall(Node n, T object) throws JidelakException {
		unmarshall(null, n, object);
	}

	public void unmarshall(String prefix, Node n, T object)
			throws JidelakException {
		synchronized (path) {

			root = n;
			path.push(n);

			while (n != null) {
				boolean res = true;
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					if (res = processElementHook((Element) n, object)) {
						if (n.hasAttributes())
							processAttributes(n);
					}
				} else if (n.getNodeType() == Node.TEXT_NODE) {
					data.put(path(), n.getTextContent());
				}
				n = getNextNode(n, res);
			}
			path.clear();
		}

		if (prefix == null)
			prefix = "";
		if (prefix.length() > 0 && prefix.charAt(prefix.length() - 1) != '.')
			prefix += ".";

		unmarshallHelper(prefix, data, object);
	}

	protected boolean processElementHook(Element n, T object)
			throws JidelakException {
		return true;
	}

	protected Node getNextNode(Node n) {
		return getNextNode(n, true);
	}

	protected Node getNextNode(Node n, boolean processChildren) {

		if (n.hasChildNodes() && processChildren) {
			n = n.getFirstChild();
			if (n.getNodeType() == Node.ELEMENT_NODE)
				path.push(n);
			return n;
		}

		do {
			n = path.pop();
			if (n == root)
				return null;
			n = n.getNextSibling();

		} while (n == null && !path.isEmpty());

		if (n == null)
			return null;

		if (n.getNodeType() == Node.ELEMENT_NODE)
			path.push(n);

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
			data.put(path() + "@" + n.getNodeName(), n.getNodeValue());
		}
	}

}
