package com.enonic.wem.query.queryfilter;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.data.Value;

public class ValueQueryFilter
    extends QueryFilter
{
    private final ImmutableSet<Value> values;

    ValueQueryFilter( final String fieldName, final Set<Value> values )
    {
        super( fieldName );
        this.values = ImmutableSet.copyOf( values );
    }

    public ImmutableSet<Value> getValues()
    {
        return values;
    }
}
