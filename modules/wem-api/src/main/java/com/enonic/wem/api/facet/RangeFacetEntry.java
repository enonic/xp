package com.enonic.wem.api.facet;

public class RangeFacetEntry
{

    private String from;

    private String to;

    private Long count;

    private Double min;

    private Double max;

    private Double mean;

    private Double total;

    public Double getTotal()
    {
        return total;
    }

    public void setTotal( final Double total )
    {
        this.total = total;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom( final String from )
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo( final String to )
    {
        this.to = to;
    }

    public Long getCount()
    {
        return count;
    }

    public void setCount( final Long count )
    {
        this.count = count;
    }

    public Double getMin()
    {
        return min;
    }

    public void setMin( final Double min )
    {
        this.min = min;
    }

    public Double getMax()
    {
        return max;
    }

    public void setMax( final Double max )
    {
        this.max = max;
    }

    public Double getMean()
    {
        return mean;
    }

    public void setMean( final Double mean )
    {
        this.mean = mean;
    }
}
