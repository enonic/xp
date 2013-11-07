package com.enonic.wem.xml;

public interface XmlSerializer<X extends XmlObject>
{
    public String serialize( X value );

    public X parse( String text );
}
