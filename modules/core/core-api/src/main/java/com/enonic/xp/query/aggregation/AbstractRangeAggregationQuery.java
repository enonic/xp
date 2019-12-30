package com.enonic.xp.query.aggregation;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class AbstractRangeAggregationQuery<R extends Range>
    extends BucketAggregationQuery
{
    private final String fieldName;

    private final ImmutableSet<R> ranges;

    AbstractRangeAggregationQuery( final Builder builder, final Collection<R> ranges )
    {
        super( builder );
        this.ranges = ImmutableSet.copyOf( ranges );
        this.fieldName = builder.fieldName;
    }

    public ImmutableSet<R> getRanges()
    {
        return ranges;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public abstract static class Builder<T extends Builder, R extends Range>
        extends BucketAggregationQuery.Builder<Builder>
    {
        private String fieldName;

        public Collection<R> ranges = new HashSet<>();

        public Builder( final String name )
        {
            super( name );
        }

        public T addRange( final R range )
        {
            ranges.add( range );
            return (T) this;
        }

        public T setRanges( final Collection<R> ranges )
        {
            this.ranges = ranges;
            return (T) this;
        }

        public T fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return (T) this;
        }
    }
}

