package com.enonic.wem.portal.internal.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public final class DomHelper
{
    private static final DocumentBuilderFactory BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    public static Document newDocument()
        throws ParserConfigurationException
    {
        final DocumentBuilder builder = BUILDER_FACTORY.newDocumentBuilder();
        return builder.newDocument();
    }
}
