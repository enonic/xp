package com.enonic.xp.query.aggregation;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class MissingAggregationQuery
    extends BucketAggregationQuery
{
    private final String fieldName;

    public MissingAggregationQuery( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().
            add( "name", getName() ).
            add( "fieldName", fieldName ).
            toString();
    }

    public static class Builder
        extends BucketAggregationQuery.Builder<Builder>
    {
        private String fieldName;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return this;
        }

        public MissingAggregationQuery build()
        {
            return new MissingAggregationQuery( this );
        }
    }
}
