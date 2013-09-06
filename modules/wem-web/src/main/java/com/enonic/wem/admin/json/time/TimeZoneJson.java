package com.enonic.wem.admin.json.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

public class TimeZoneJson
{
    private final DateTimeZone model;

    public TimeZoneJson( final DateTimeZone model )
    {
        this.model = model;
    }

    public String getId()
    {
        return this.model.getID();
    }

    public String getHumanizedId()
    {
        return this.model.getID().replaceAll( "_", " " );
    }

    public String getShortName()
    {
        return this.model.getShortName( new DateTime().getMillis() );
    }

    public String getName()
    {
        return this.model.getName( new DateTime().getMillis() );
    }

    public String getOffset()
    {
        final DateTime now = new DateTime();
        final DateTime local = now.plus( model.getOffsetFromLocal( now.getMillis() ) );
        final Period offsetPeriod = new Period( now, local );

        return getHoursAsHumanReadable( offsetPeriod );
    }

    private String getHoursAsHumanReadable( final Period offsetPeriod )
    {
        final StringBuilder s = new StringBuilder();
        if ( ( offsetPeriod.getMinutes() < 0 ) || ( offsetPeriod.getHours() < 0 ) )
        {
            s.append( "-" );
        }
        else
        {
            s.append( "+" );
        }

        final int hours = Math.abs( offsetPeriod.getHours() );

        if ( hours < 10 && hours > ( -10 ) )
        {
            s.append( "0" );
        }
        s.append( hours );
        s.append( ":" );

        final int minutes = Math.abs( offsetPeriod.getMinutes() );
        if ( minutes < 10 )
        {
            s.append( "0" );
        }
        s.append( minutes );
        return s.toString();
    }
}
