package com.enonic.wem.query.queryfilter;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.data.Value;

public abstract class QueryFilter
{
    private final String fieldName;

    private final ImmutableSet<Value> values;

    QueryFilter( final String fieldName, final Set<Value> values )
    {
        this.fieldName = fieldName;
        this.values = ImmutableSet.copyOf( values );
    }

    public static ContentTypeQueryFilter.Builder newContentTypeFilter()
    {
        return new ContentTypeQueryFilter.Builder();
    }

    public static GenericQueryFilter.Builder newQueryFilter()
    {
        return new GenericQueryFilter.Builder();
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public ImmutableSet<Value> getValues()
    {
        return values;
    }

}
