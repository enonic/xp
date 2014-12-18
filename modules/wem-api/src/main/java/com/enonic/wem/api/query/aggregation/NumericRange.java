package com.enonic.wem.api.query.aggregation;

public final class NumericRange
    extends Range
{
    private final Double from;

    private final Double to;

    private final String key;

    private NumericRange( final Builder builder )
    {
        super( builder );
        this.from = builder.from;
        this.to = builder.to;
        this.key = builder.key;
    }

    public Double getFrom()
    {
        return from;
    }

    public Double getTo()
    {
        return to;
    }

    @Override
    public String getKey()
    {
        return key;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends Range.Builder<Builder>
    {
        private Double from;

        private Double to;

        private String key;

        public Builder from( final Double from )
        {
            this.from = from;
            return this;
        }

        public Builder to( final Double to )
        {
            this.to = to;
            return this;
        }

        public Builder key( final String key )
        {
            this.key = key;
            return this;
        }

        public NumericRange build()
        {
            return new NumericRange( this );
        }


    }

}
