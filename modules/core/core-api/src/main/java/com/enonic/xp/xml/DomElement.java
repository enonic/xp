package com.enonic.xp.xml;

import java.util.List;
import java.util.function.Predicate;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

import com.enonic.xp.convert.Converters;

@Beta
public final class DomElement
{
    private final Element elem;

    private DomElement( final Element elem )
    {
        this.elem = elem;
    }

    public Element getWrapped()
    {
        return this.elem;
    }

    public String getTagName()
    {
        return this.elem.getTagName();
    }

    public DomElement getChild( final String name )
    {
        final List<DomElement> children = getChildren( name );
        return children.isEmpty() ? null : children.get( 0 );
    }

    public List<DomElement> getChildren( final String name )
    {
        return getChildren( element -> element.getTagName().equals( name ) );
    }

    public List<DomElement> getChildren()
    {
        return getChildren( element -> true );
    }

    private List<DomElement> getChildren( final Predicate<Element> filter )
    {
        final NodeList list = this.elem.getChildNodes();
        final List<DomElement> result = Lists.newArrayList();

        for ( int i = 0; i < list.getLength(); i++ )
        {
            final Node node = list.item( i );
            if ( ( node instanceof Element ) && filter.test( (Element) node ) )
            {
                result.add( DomElement.from( ( (Element) node ) ) );
            }
        }

        return result;
    }

    public String getValue()
    {
        final StringBuilder str = new StringBuilder();
        final NodeList list = this.elem.getChildNodes();

        for ( int i = 0; i < list.getLength(); i++ )
        {
            final Node item = list.item( i );
            if ( ( item instanceof CharacterData && !( item instanceof Comment ) ) || item instanceof EntityReference )
            {
                str.append( item.getNodeValue() );
            }
        }

        return str.toString().trim();
    }

    public List<Attr> getAttributes()
    {
        final List<Attr> result = Lists.newArrayList();
        final NamedNodeMap map = this.elem.getAttributes();

        for ( int i = 0; i < map.getLength(); i++ )
        {
            result.add( (Attr) map.item( i ) );
        }

        return result;
    }

    public String getAttribute( final String name )
    {
        return this.elem.getAttribute( name );
    }

    public String getAttribute( final String name, final String defValue )
    {
        final String value = getAttribute( name );
        return value != null ? value : defValue;
    }

    public <T> T getAttributeAs( final String name, final Class<T> type, final T defValue )
    {
        return convert( type, getAttribute( name ), defValue );
    }

    private <T> T convert( final Class<T> type, final String value, final T defValue )
    {
        if ( value == null )
        {
            return defValue;
        }

        final T converted = Converters.convert( value, type );
        return converted != null ? converted : defValue;
    }

    public String getChildValue( final String name )
    {
        return getChildValue( name, null );
    }

    public String getChildValue( final String name, final String defValue )
    {
        final DomElement elem = getChild( name );
        return elem != null ? elem.getValue() : defValue;
    }

    public String serializeBody()
    {
        return DomHelper.serializeBody( this.elem );
    }

    public <T> T getChildValueAs( final String name, final Class<T> type, final T defValue )
    {
        return convert( type, getChildValue( name ), defValue );
    }

    public static DomElement from( final Element elem )
    {
        return new DomElement( elem );
    }
}
