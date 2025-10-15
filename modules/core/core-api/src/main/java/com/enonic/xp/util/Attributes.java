package com.enonic.xp.util;

import java.util.List;

import com.google.common.collect.ImmutableList;

public record Attributes(List<PropertyValue> list)
{
    public Attributes( final List<PropertyValue> list )
    {
        this.list = ImmutableList.copyOf( list );
    }

    public PropertyValue find( final String key )
    {
        return list.stream()
            .filter( entity -> entity.optional( "_key" ).orElseThrow().asString().equals( key ) )
            .findFirst()
            .orElse( null );
    }
}
