package de.aivot.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlDocumentUtils {

    private final Document document;

    private XmlDocumentUtils(Document document) {
        this.document = document;
    }

    public Element addElement(Node parent, String namespace, String tag, String content, String... attributes) {
        return addElement(parent, null, namespace, tag, content, attributes);
    }

    public Element addElement(Node parent, Node before, String namespace, String tag, String content, String... attributes) {
        Element element = document.createElement(namespace + ":" + tag);
        if (parent != null) {
            if (before != null) {
                parent.insertBefore(element, before);
            } else {
                parent.appendChild(element);
            }
        }

        if (content != null) {
            element.setTextContent(content);
        }

        for (int i = 0; i < attributes.length; i += 2) {
            element.setAttribute(attributes[i], attributes[i + 1]);
        }

        return element;
    }

    public static XmlDocumentUtils create(Document document) {
        return new XmlDocumentUtils(document);
    }
}
