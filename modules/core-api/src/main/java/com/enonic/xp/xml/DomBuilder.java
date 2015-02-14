package com.enonic.xp.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class DomBuilder
{
    private final Document document;

    private Element current;

    private DomBuilder( final Document document, final String name )
    {
        this.document = document;
        this.current = this.document.createElement( name );
        this.document.appendChild( this.current );
    }

    public DomBuilder start( final String name )
    {
        final Element element = this.document.createElement( name );
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

    public DomBuilder attribute( final String name, final boolean value )
    {
        return attribute( name, String.valueOf( value ) );
    }

    public DomBuilder text( final String content )
    {
        this.current.appendChild( this.document.createTextNode( content ) );
        return this;
    }

    public static DomBuilder create( final String name )
    {
        return new DomBuilder( DomHelper.newDocument(), name );
    }

    public Document getDocument()
    {
        return this.document;
    }
}
