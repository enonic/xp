package com.enonic.wem.api.converter;

import com.enonic.wem.api.value.ValueException;

public abstract class Converters
{
    private final ConverterRegistry registry;

    public Converters()
    {
        this.registry = new ConverterRegistry();
    }

    public final void addAll( final Converters converters )
    {
        this.registry.addAll( converters.registry );
    }

    public final <S, T> void register( Class<S> source, Class<T> target, Converter<S, T> converter )
    {
        this.registry.register( source, target, converter );
    }

    public final <A, B> B convert( final A from, final Class<B> to )
    {
        try
        {
            final Object result = doConvert( from, to );
            return typecast( result );
        }
        catch ( final ValueException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ConvertException( "Could not convert [%s] to [%s] (%s)", from.getClass().getName(), to.getName(), e.toString() );
        }
    }

    private Object doConvert( final Object from, final Class to )
    {
        final Class fromClass = from.getClass();
        if ( fromClass == to )
        {
            return from;
        }

        final Converter converter = this.registry.find( fromClass, to );
        if ( converter == null )
        {
            throw new ConvertException( "Conversion of %s to %s is not supported", fromClass.getName(), to.getName() );
        }

        @SuppressWarnings("unchecked")
        final Object result = converter.convert( from );
        if ( result != null )
        {
            return result;
        }

        throw new ConvertException( "Could not convert %s to %s", fromClass.getName(), to.getName() );
    }

    @SuppressWarnings("unchecked")
    private <T> T typecast( final Object value )
    {
        return (T) value;
    }
}
