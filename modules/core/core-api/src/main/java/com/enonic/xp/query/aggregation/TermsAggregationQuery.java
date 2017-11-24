package com.enonic.xp.query.aggregation;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public class TermsAggregationQuery
    extends BucketAggregationQuery
{
    public final static int TERM_DEFAULT_SIZE = 10;

    private final String fieldName;

    private final int size;

    private final Direction orderDirection;

    private final Type orderType;

    private final long minDocCount;

    private TermsAggregationQuery( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
        this.size = builder.size;
        this.orderDirection = builder.direction;
        this.orderType = builder.type;
        this.minDocCount = builder.minDocCount;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public int getSize()
    {
        return size;
    }

    public Direction getOrderDirection()
    {
        return orderDirection;
    }

    public Type getOrderType()
    {
        return orderType;
    }

    public long getMinDocCount()
    {
        return minDocCount;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().
            add( "name", getName() ).
            add( "fieldName", fieldName ).
            add( "size", size ).
            add( "orderDirection", orderDirection ).
            add( "orderType", orderType ).
            add( "minDocCount", minDocCount ).
            toString();
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends BucketAggregationQuery.Builder<Builder>
    {
        private Direction direction = Direction.ASC;

        private Type type = Type.TERM;

        private String fieldName;

        private long minDocCount = 1;

        public Builder( final String name )
        {
            super( name );
        }

        private int size = TERM_DEFAULT_SIZE;

        public Builder fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return this;
        }

        public Builder size( final Integer size )
        {
            this.size = size;
            return this;
        }

        public Builder minDoccount( final long minDocCount )
        {
            this.minDocCount = minDocCount;
            return this;
        }

        public TermsAggregationQuery build()
        {
            return new TermsAggregationQuery( this );
        }

        public Builder orderDirection( final Direction direction )
        {
            this.direction = direction;
            return this;
        }

        public Builder orderType( final Type type )
        {
            this.type = type;
            return this;
        }
    }

    public enum Direction
    {
        ASC, DESC
    }

    public enum Type
    {
        TERM, DOC_COUNT
    }


}
