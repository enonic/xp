package com.enonic.wem.core.content;


public class XmlParsingException
    extends ParsingException
{
    public XmlParsingException( final String message, final Exception e )
    {
        super( message, e );
    }

    public XmlParsingException( final String message )
    {
        super( message );
    }
}
