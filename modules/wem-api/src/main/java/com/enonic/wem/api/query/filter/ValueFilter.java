package com.enonic.wem.api.query.filter;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.data.Value;

public class ValueFilter
    extends Filter
{
    private final ImmutableSet<Value> values;

    ValueFilter( final String fieldName, final Set<Value> values )
    {
        super( fieldName );
        this.values = ImmutableSet.copyOf( values );
    }

    public ImmutableSet<Value> getValues()
    {
        return values;
    }
}
