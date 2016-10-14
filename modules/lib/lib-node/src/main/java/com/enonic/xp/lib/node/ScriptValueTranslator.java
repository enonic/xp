package com.enonic.xp.lib.node;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

class ScriptValueTranslator
{
    public static PropertyTree translate( final ScriptValue value )
    {
        final Map<String, Object> map = value.getMap();

        final PropertyTree tree = new PropertyTree();

        handleMap( tree.getRoot(), map );

        return tree;
    }

    private static void handleElement( final PropertySet parent, final String name, final Object value )
    {
        if ( value instanceof Map )
        {
            final PropertySet set = parent.addSet( name );
            handleMap( set, (Map) value );
        }
        else if ( value instanceof Collection )
        {
            handleArray( parent, name, (Collection) value );
        }
        else
        {
            handleValue( parent, name, value );
        }
    }

    private static void handleMap( final PropertySet parent, final Map map )
    {
        for ( final Object key : map.keySet() )
        {
            handleElement( parent, key.toString(), map.get( key ) );
        }
    }

    private static void handleArray( final PropertySet parent, final String name, final Collection values )
    {
        for ( final Object value : values )
        {
            handleElement( parent, name, value );
        }
    }

    private static void handleValue( final PropertySet parent, final String name, final Object value )
    {
        if ( value instanceof Instant )
        {
            parent.addInstant( name, (Instant) value );
        }
        else if ( value instanceof GeoPoint )
        {
            parent.addGeoPoint( name, (GeoPoint) value );
        }
        else if ( value instanceof Double )
        {
            parent.addDouble( name, (Double) value );
        }
        else if ( value instanceof Integer )
        {
            parent.addLong( name, ( (Integer) value ).longValue() );
        }
        else if ( value instanceof Boolean )
        {
            parent.addBoolean( name, (Boolean) value );
        }
        else if ( value instanceof LocalDateTime )
        {
            parent.addLocalDateTime( name, (LocalDateTime) value );
        }
        else if ( value instanceof LocalDate )
        {
            parent.addLocalDate( name, (LocalDate) value );
        }
        else if ( value instanceof LocalTime )
        {
            parent.addLocalTime( name, (LocalTime) value );
        }
        else if ( value instanceof Reference )
        {
            parent.addReference( name, (Reference) value );
        }
        else
        {
            parent.addString( name, value.toString() );
        }

    }
}
