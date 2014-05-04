package com.enonic.wem.api.converter;

import java.util.Map;

import com.google.common.collect.Maps;

final class ConverterRegistry
{
    private final Map<String, Converter> map;

    public ConverterRegistry()
    {
        this.map = Maps.newHashMap();
    }

    private String composeKey( final Class from, final Class to )
    {
        return from.getName() + "->" + to.getName();
    }

    public void addAll( final ConverterRegistry registry )
    {
        this.map.putAll( registry.map );
    }

    public <A, B> void register( final Class<A> from, final Class<B> to, Converter<A, B> converter )
    {
        final String key = composeKey( from, to );
        this.map.put( key, converter );
    }

    @SuppressWarnings("unchecked")
    public <A, B> Converter<A, B> find( final Class<A> from, final Class<B> to )
    {
        return doFind( from, to );
    }

    private Converter doFind( final Class from, final Class to )
    {
        final String key = composeKey( from, to );
        final Converter converter = this.map.get( key );

        if ( converter != null )
        {
            return converter;
        }

        final Class superClass = from.getSuperclass();
        if ( superClass != null )
        {
            return doFind( superClass, to );
        }

        return null;
    }
}
