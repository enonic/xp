package com.enonic.wem.api.query.aggregation;

public class DateInterval
    extends Interval
{
    private final String value;

    private DateInterval( final String value )
    {
        this.value = value;
    }

    public static DateInterval from( final String dateInterval )
    {
        return new DateInterval( dateInterval );
    }

    @Override
    public String toString()
    {
        return value;
    }
}
