package com.enonic.wem.repo.internal.index.result;

import java.util.List;

import com.google.common.collect.Lists;

public class SearchResultFieldValue
{
    private final List<Object> values;

    public Object getValue()
    {
        return values.get( 0 );
    }

    public List<Object> getValues()
    {
        return values;
    }

    private SearchResultFieldValue( final List<Object> values )
    {
        this.values = values;
    }

    public static SearchResultFieldValue value( final Object value )
    {
        return new SearchResultFieldValue( Lists.newArrayList( value ) );
    }

    public static SearchResultFieldValue values( final List<Object> values )
    {
        return new SearchResultFieldValue( values );

    }


}
