package com.enonic.xp.core.xml.serializer;

import org.w3c.dom.Node;

public interface XmlSerializer<X>
{
    public String serialize( X value );

    public X parse( String text );

    public X parse( Node node );
}
