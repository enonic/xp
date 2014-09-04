package com.enonic.wem.api.query.filter;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Value;

public class ValueFilter
    extends FieldFilter
{
    private final ImmutableSet<Value> values;

    ValueFilter( final Builder builder )
    {
        super( builder );
        this.values = ImmutableSet.copyOf( builder.values );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public ImmutableSet<Value> getValues()
    {
        return values;
    }

    public static class Builder
        extends FieldFilter.Builder<Builder>
    {
        private final Set<Value> values = Sets.newHashSet();

        public Builder addValue( final Value value )
        {
            this.values.add( value );
            return this;
        }

        public ValueFilter build()
        {
            return new ValueFilter( this );
        }

    }
}
