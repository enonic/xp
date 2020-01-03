package com.enonic.xp.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class StatsAggregation
    extends MetricAggregation
{
    private final double avg;

    private final double count;

    private final double max;

    private final double min;

    private final double sum;

    private StatsAggregation( final Builder builder )
    {
        super( builder );
        avg = builder.avg;
        count = builder.count;
        max = builder.max;
        min = builder.min;
        sum = builder.sum;
    }

    public double getAvg()
    {
        return avg;
    }

    public double getCount()
    {
        return count;
    }

    public double getMax()
    {
        return max;
    }

    public double getMin()
    {
        return min;
    }

    public double getSum()
    {
        return sum;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static final class Builder
        extends Aggregation.Builder<Builder>
    {
        private double avg;

        private double count;

        private double max;

        private double min;

        private double sum;

        private Builder( final String name )
        {
            super( name );
        }

        public Builder avg( double avg )
        {
            this.avg = avg;
            return this;
        }

        public Builder count( double count )
        {
            this.count = count;
            return this;
        }

        public Builder max( double max )
        {
            this.max = max;
            return this;
        }

        public Builder min( double min )
        {
            this.min = min;
            return this;
        }

        public Builder sum( double sum )
        {
            this.sum = sum;
            return this;
        }

        public StatsAggregation build()
        {
            return new StatsAggregation( this );
        }
    }
}
