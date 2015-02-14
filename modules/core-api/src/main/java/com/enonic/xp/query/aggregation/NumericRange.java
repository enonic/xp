package com.enonic.xp.query.aggregation;

public final class NumericRange
    extends Range
{
    private final Double from;

    private final Double to;

    private NumericRange( final Builder builder )
    {
        super( builder );
        this.from = builder.from;
        this.to = builder.to;
    }

    public Double getFrom()
    {
        return from;
    }

    public Double getTo()
    {
        return to;
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

        public NumericRange build()
        {
            return new NumericRange( this );
        }
    }
}
