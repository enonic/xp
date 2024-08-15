package com.enonic.xp.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class DomBuilder
{
    private final Document document;

    private final String namespace;

    private Element current;

    private DomBuilder( final Document document, final String namespace, final String name )
    {
        this.document = document;
        this.namespace = namespace;
        this.current = createElement( name );
        this.document.appendChild( this.current );
    }

    private Element createElement( final String name )
    {
        if ( this.namespace != null )
        {
            return this.document.createElementNS( this.namespace, name );
        }
        else
        {
            return this.document.createElement( name );
        }
    }

    public DomBuilder start( final String name )
    {
        final Element element = createElement( name );
        this.current.appendChild( element );
        this.current = element;
        return this;
    }

    public DomBuilder end()
    {
        this.current = (Element) this.current.getParentNode();
        return this;
    }

    public DomBuilder attribute( final String name, final String value )
    {
        this.current.setAttribute( name, value );
        return this;
    }

    public DomBuilder text( final String content )
    {
        this.current.appendChild( this.document.createTextNode( content ) );
        return this;
    }

    public static DomBuilder create( final String name )
    {
        return create( null, name );
    }

    public static DomBuilder create( final String namespace, final String name )
    {
        return new DomBuilder( DomHelper.newDocument(), namespace, name );
    }

    public Document getDocument()
    {
        return this.document;
    }
}
