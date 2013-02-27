package com.enonic.wem.core.support.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public final class JdomHelper
{
    private final SAXBuilder saxBuilder;

    public JdomHelper()
    {
        this.saxBuilder = new SAXBuilder();
    }

    public Document parse( final String xml )
        throws IOException, JDOMException
    {
        return parse( new StringReader( xml ) );
    }

    public Document parse( final Reader reader )
        throws IOException, JDOMException
    {
        return this.saxBuilder.build( reader );
    }

    public Document parse( final InputStream in )
        throws IOException, JDOMException
    {
        return this.saxBuilder.build( in );
    }

    public String serialize( final Document node, final boolean prettyPrint )
    {
        return newSerializer( prettyPrint ).outputString( node );
    }

    public String serialize( final Element node, final boolean prettyPrint )
    {
        return newSerializer( prettyPrint ).outputString( node );
    }

    private XMLOutputter newSerializer( final boolean prettyPrint )
    {
        final Format format = prettyPrint ? Format.getPrettyFormat() : Format.getCompactFormat();
        return new XMLOutputter( format );
    }
}
