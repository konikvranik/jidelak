package net.suteren.android.jidelak.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class BaseMarshaller<T> {

	protected Map<String, String> data = new HashMap<String, String>();

	Stack<Node> path = new Stack<Node>();

	protected abstract void unmarshallHelper(String prefix,
			Map<String, String> data, T object);

	public void clean() {
		synchronized (path) {
			data = new HashMap<String, String>();
			path.clear();
		}
	}

	public void unmarshall(Node n, T object) {
		unmarshall(null, n, object);
	}

	public void unmarshall(String prefix, Node n, T object) {
		synchronized (path) {
			while (n != null) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {

					boolean res;
					if (res = processElementHook((Element) n, object)) {
						path.push(n);
						if (n.hasAttributes())
							processAttributes(n);
					}
					n = getNextNode(n, res);
				} else if (n.getNodeType() == Node.TEXT_NODE) {
					data.put(path(), n.getTextContent());
					n = getNextNode(n);
				}
			}
			path.clear();
		}

		if (prefix == null)
			prefix = "";
		if (prefix.length() > 0 && prefix.charAt(prefix.length()) != '.')
			prefix += ".";

		unmarshallHelper(prefix, data, object);
	}

	protected boolean processElementHook(Element n, T object) {
		return true;
	}

	protected Node getNextNode(Node n) {
		return getNextNode(n, true);
	}

	protected Node getNextNode(Node n, boolean processChildren) {
		if (n.hasChildNodes() && processChildren)
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
