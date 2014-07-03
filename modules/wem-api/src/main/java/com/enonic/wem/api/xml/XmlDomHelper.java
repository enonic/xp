package com.enonic.wem.api.xml;

import org.jdom2.input.DOMBuilder;
import org.jdom2.output.DOMOutputter;
import org.w3c.dom.Element;

public final class XmlDomHelper
{
    public static Element toElement( final org.jdom2.Element node )
    {
        try
        {
            final DOMOutputter outputter = new DOMOutputter();
            return outputter.output( node );
        }
        catch ( final Exception e )
        {
            throw handleException( e );
        }
    }

    public static org.jdom2.Element toJdomElement( final Element node )
    {
        final DOMBuilder builder = new DOMBuilder();
        return builder.build( node ).detach();
    }

    private static XmlException handleException( final Exception cause )
    {
        return new XmlException( cause, cause.getMessage() );
    }
}
