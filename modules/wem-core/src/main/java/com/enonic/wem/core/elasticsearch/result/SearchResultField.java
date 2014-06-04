package com.enonic.wem.core.elasticsearch.result;

import java.util.List;

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
}
