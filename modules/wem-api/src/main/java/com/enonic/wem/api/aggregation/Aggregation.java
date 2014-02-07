package com.enonic.wem.api.aggregation;

public abstract class Aggregation
{
    private final String name;

    public Aggregation( final Builder builder )
    {
        this.name = builder.name;
    }

    public String getName()
    {
        return name;
    }

    public static BucketAggregation.Builder bucketAggregation( final String name )
    {
        return new BucketAggregation.Builder( name );
    }


    public static class Builder<T extends Builder>
    {
        public Builder( final String name )
        {
            this.name = name;
        }

        private String name;
    }
}
