package com.enonic.wem.api.value;

import java.util.Map;
import java.util.function.Function;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.util.GeoPoint;

final class ValueConverter
{
    private final static ValueConverter INSTANCE = new ValueConverter();

    private final Map<String, Function> map;

    private ValueConverter()
    {
        this.map = Maps.newHashMap();

        // Register object -> * converters
        register( Object.class, String.class, ValueConverters::objectToString );

        // Register string -> * converters
        register( String.class, Boolean.class, ValueConverters::stringToBoolean );
        register( String.class, Double.class, ValueConverters::stringToDouble );
        register( String.class, Long.class, ValueConverters::stringToLong );
        register( String.class, EntityId.class, ValueConverters::stringToEntityId );
        register( String.class, ContentId.class, ValueConverters::stringToContentId );
        register( String.class, GeoPoint.class, ValueConverters::stringToGeoPoint );
        register( String.class, LocalDate.class, ValueConverters::stringToLocalDate );
        register( String.class, DateTime.class, ValueConverters::stringToDateTime );

        // Register number -> * converters
        register( Number.class, Long.class, ValueConverters::numberToLong );
        register( Number.class, Double.class, ValueConverters::numberToDouble );
    }

    private String composeKey( final Class from, final Class to )
    {
        return from.getName() + "->" + to.getName();
    }

    private <A, B> void register( final Class<A> from, final Class<B> to, Function<A, B> converter )
    {
        final String key = composeKey( from, to );
        this.map.put( key, converter );
    }

    private Function findConverter( final Class from, final Class to )
    {
        final String key = composeKey( from, to );
        final Function converter = this.map.get( key );

        if ( converter != null )
        {
            return converter;
        }

        final Class superClass = from.getSuperclass();
        if ( superClass != null )
        {
            return findConverter( superClass, to );
        }

        return null;
    }

    private Object doConvert( final Object from, final Class to )
    {
        final Class fromClass = from.getClass();
        if ( fromClass == to )
        {
            return from;
        }

        final Function converter = findConverter( fromClass, to );
        if ( converter == null )
        {
            throw new ValueException( "Conversion of %s to %s is not supported", fromClass.getName(), to.getName() );
        }

        @SuppressWarnings("unchecked")
        final Object result = converter.apply( from );
        if ( result != null )
        {
            return result;
        }

        throw new ValueException( "Could not convert %s to %s", fromClass.getName(), to.getName() );
    }

    @SuppressWarnings("unchecked")
    private static <T> T typecast( final Object value )
    {
        return (T) value;
    }

    public static <A, B> B convert( final A from, final Class<B> to )
    {
        try
        {
            final Object result = INSTANCE.doConvert( from, to );
            return typecast( result );
        }
        catch ( final ValueException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ValueException( "Could not convert [%s] to [%s] (%s)", from.getClass().getName(), to.getName(), e.toString() );
        }
    }
}
