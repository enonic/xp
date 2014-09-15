package com.enonic.wem.core.index.result;

import java.util.List;

import com.google.common.collect.Lists;

public class SearchResultField
{
    private final String name;

    private final List<Object> values;


    public String getName()
    {
        return name;
    }

    public Object getValue()
    {
        return values.get( 0 );
    }

    public List<Object> getValues()
    {
        return values;
    }

    public SearchResultField( final String name, final List<Object> values )
    {
        this.name = name;
        this.values = values;
    }

    public SearchResultField( final String name, final Object value )
    {
        this.name = name;
        this.values = Lists.newArrayList( value );
    }
}
