package com.enonic.xp.xml.parser;

import org.w3c.dom.Element;

import com.enonic.xp.xml.DomHelper;

final class XmlParserHelper
{
    public static String getChildElementAsString( final Element root, final String name, final String defValue )
    {
        final String value = DomHelper.getChildElementValueByTagName( root, name );
        return value != null ? value : defValue;
    }

    public static boolean getChildElementAsBoolean( final Element root, final String name, final boolean defValue )
    {
        final String value = getChildElementAsString( root, name, null );
        return value != null ? value.equals( "true" ) : defValue;
    }

    public static String getAttributeAsString( final Element root, final String name, final String defValue )
    {
        final String value = root.getAttribute( name );
        return value != null ? value : defValue;
    }

    public static int getAttributeAsInteger( final Element root, final String name, final int defValue )
    {
        final String value = getAttributeAsString( root, name, null );

        try
        {
            return Integer.parseInt( value );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }
}
