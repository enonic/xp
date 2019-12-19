package com.enonic.xp.query.aggregation;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public class CompositeAggregationQuery
    extends BucketAggregationQuery
{
    private final String fieldName;

    private final int size;

    private final Map<String, Object> after;

    private CompositeAggregationQuery( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
        this.size = builder.size;
        this.after = builder.after;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public int getSize()
    {
        return size;
    }

    public Map<String, Object> getAfter()
    {
        return after;
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

        private int size;

        private Map<String, Object> after;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder after( Map<String, Object> after )
        {
            this.after = after;
            return this;
        }

        public CompositeAggregationQuery build()
        {
            return new CompositeAggregationQuery( this );
        }

    }
}
