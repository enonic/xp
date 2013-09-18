package com.enonic.wem.admin.rpc.util;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class TimeZoneJsonResult
    extends JsonResult
{
    private final List<DateTimeZone> list;

    public TimeZoneJsonResult( final List<DateTimeZone> list )
    {
        this.list = list;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", this.list.size() );

        final ArrayNode array = json.putArray( "timezones" );
        for ( final DateTimeZone model : this.list )
        {
            serialize( array.addObject(), model );
        }
    }

    private void serialize( final ObjectNode json, final DateTimeZone model )
    {
        final DateTime now = new DateTime();

        json.put( "id", model.getID() );
        json.put( "humanizedId", model.getID().replaceAll( "_", " " ) );
        json.put( "shortName", model.getShortName( now.getMillis() ) );
        json.put( "name", model.getName( now.getMillis() ) );

        final DateTime local = now.plus( model.getOffsetFromLocal( now.getMillis() ) );
        final Period offsetPeriod = new Period( now, local );
        json.put( "offset", getHoursAsHumanReadable( offsetPeriod ) );
    }

    private String getHoursAsHumanReadable( Period offsetPeriod )
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
