package com.enonic.xp.portal.impl.xslt;

import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import com.enonic.wem.api.xml.DomBuilder;

final class MapToXmlConverter
{
    public static Source toSource( final Map<String, Object> map )
    {
        final DomBuilder builder = DomBuilder.create( "root" );
        serializeMap( builder, map );
        return new DOMSource( builder.getDocument() );
    }

    private static void serializeMap( final DomBuilder builder, final Map<?, ?> map )
    {
        for ( final Map.Entry<?, ?> item : map.entrySet() )
        {
            builder.start( item.getKey().toString() );
            serializeObject( builder, item.getValue() );
            builder.end();
        }
    }

    private static void serializeObject( final DomBuilder builder, final Object value )
    {
        if ( value instanceof List )
        {
            serializeList( builder, (List<?>) value );
        }
        else if ( value instanceof Map )
        {
            serializeMap( builder, (Map<?, ?>) value );
        }
        else
        {
            builder.text( value.toString() );
        }
    }

    private static void serializeList( final DomBuilder builder, final List<?> list )
    {
        for ( final Object item : list )
        {
            builder.start( "item" );
            serializeObject( builder, item );
            builder.end();
        }
    }
}
