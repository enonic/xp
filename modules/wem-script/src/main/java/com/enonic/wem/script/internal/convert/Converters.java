package com.enonic.wem.script.internal.convert;

import java.util.Map;

import com.google.common.collect.Maps;

public final class Converters
{
    private final static Converters INSTANCE = new Converters();

    private final Map<Class, Converter> map;

    private Converters()
    {
        this.map = Maps.newConcurrentMap();
        addConverter( new StringConverter() );
        addConverter( new BooleanConverter() );
        addConverter( new ByteConverter() );
        addConverter( new DoubleConverter() );
        addConverter( new FloatConverter() );
        addConverter( new IntegerConverter() );
        addConverter( new LongConverter() );
        addConverter( new ShortConverter() );
        addConverter( new ResourceKeyConverter() );
        addConverter( new ContentPathConverter() );
        addConverter( new ContentIdConverter() );
    }

    @SuppressWarnings("unchecked")
    public static <S, T> T convert( final S source, final Class<T> toType )
    {
        if ( source == null )
        {
            return null;
        }

        final Converter converter = INSTANCE.findConverter( source, toType );
        return (T) INSTANCE.doConvert( converter, source );
    }

    private Object doConvert( final Converter converter, final Object source )
    {
        try
        {
            return converter.convert( source );
        }
        catch ( final ConvertException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw ConvertException.convertFailure( source.getClass(), converter.getType(), e );
        }
    }

    private Converter findConverter( final Object source, final Class type )
    {
        final Converter converter = this.map.get( type );
        if ( converter != null )
        {
            return converter;
        }

        throw ConvertException.noSuchConverter( source.getClass(), type );
    }

    private void addConverter( final Converter converter )
    {
        this.map.put( converter.getType(), converter );
    }
}
