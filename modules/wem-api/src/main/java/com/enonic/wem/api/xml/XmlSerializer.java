package com.enonic.wem.api.xml;

import org.w3c.dom.Node;

public interface XmlSerializer<X extends XmlObject>
{
    String serialize( final X value );

    Node serializeToNode( final Object value );

    X parse( final String text );

    X parse( final Node element );
}
