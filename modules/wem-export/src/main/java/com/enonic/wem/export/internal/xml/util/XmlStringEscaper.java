package com.enonic.wem.export.internal.xml.util;

import com.google.common.xml.XmlEscapers;

public class XmlStringEscaper
{
    private static final String ESCAPE_START = "<![CDATA[";

    private static final String ESCAPE_STOP = "]]>";


    public static final String escapeContent( final String value )
    {
        return XmlEscapers.xmlContentEscaper().escape( value );
    }


    public static final String escapeAttribute( final String value )
    {
        return XmlEscapers.xmlAttributeEscaper().escape( value );
    }


}
