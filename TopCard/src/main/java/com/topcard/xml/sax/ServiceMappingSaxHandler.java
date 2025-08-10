package com.topcard.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class ServiceMappingSaxHandler extends DefaultHandler {
    private final Map<String, String> serviceMappings = new HashMap<>();
    private String currentElement;
    private String interfaceName;
    private String implementationName;

    public Map<String, String> getServiceMappings() {
        return serviceMappings;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String content = new String(ch, start, length).trim();
        if (currentElement == null || content.isEmpty()) return;

        if (currentElement.equalsIgnoreCase("interface")) {
            interfaceName = content;
        } else if (currentElement.equalsIgnoreCase("implementation")) {
            implementationName = content;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("service")) {
            if (interfaceName != null && implementationName != null) {
                serviceMappings.put(interfaceName, implementationName);
            }
            interfaceName = null;
            implementationName = null;
        }
        currentElement = null;
    }
}
