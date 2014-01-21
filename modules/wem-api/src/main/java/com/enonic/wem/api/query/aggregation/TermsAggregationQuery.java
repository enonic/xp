package com.enonic.wem.api.query.aggregation;

public class TermsAggregationQuery
    extends AggregationQuery
{
    public final static int TERM_DEFAULT_SIZE = 10;

    private final String fieldName;

    private final int size;

    public TermsAggregationQuery( final Builder builder )
    {
        super( builder.name );
        this.fieldName = builder.fieldName;
        this.size = builder.size;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public int getSize()
    {
        return size;
    }

    public static class Builder
        extends AggregationQuery.Builder
    {
        private String fieldName;

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

        public TermsAggregationQuery build()
        {
            return new TermsAggregationQuery( this );
        }

    }

}
