package com.enonic.xp.query.aggregation;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public class RareTermsAggregationQuery
    extends BucketAggregationQuery
{
    private final String fieldName;

    private final int numOfPartitions;

    private final int partition;

    private final long maxDocCount;

    private RareTermsAggregationQuery( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
        this.maxDocCount = builder.maxDocCount;
        this.numOfPartitions = builder.numOfPartitions;
        this.partition = builder.partition;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public long getMaxDocCount()
    {
        return maxDocCount;
    }

    public int getNumOfPartitions()
    {
        return numOfPartitions;
    }

    public int getPartition()
    {
        return partition;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().
            add( "name", getName() ).
            add( "fieldName", fieldName ).
            add( "maxDocCount", maxDocCount ).
            toString();
    }

    public static class Builder
        extends BucketAggregationQuery.Builder<Builder>
    {
        private String fieldName;

        private int maxDocCount = 1;

        private int numOfPartitions = 1;

        private int partition = 0;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return this;
        }

        public Builder maxDocCount( final int maxDocCount )
        {
            this.maxDocCount = maxDocCount;
            return this;
        }

        public Builder partition( final int partition )
        {
            this.partition = partition;
            return this;
        }

        public Builder numOfPartitions( final int num )
        {
            this.numOfPartitions = num;
            return this;
        }

        public RareTermsAggregationQuery build()
        {
            return new RareTermsAggregationQuery( this );
        }

    }
}
