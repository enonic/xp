package com.enonic.wem.web.rest2.timezone;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import com.enonic.wem.web.rest2.common.JsonResult;

public final class TimeZoneResult
    extends JsonResult
{
    private final List<DateTimeZone> list;

    public TimeZoneResult(final List<DateTimeZone> list)
    {
        this.list = list;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "total", this.list.size() );

        final ArrayNode array = json.putArray( "timezones" );
        for ( final DateTimeZone model : this.list )
        {
            array.add( toJson( model ) );
        }

        return json;
    }

    private ObjectNode toJson( final DateTimeZone model )
    {
        final DateTime now = new DateTime();

        final ObjectNode json = objectNode();
        json.put( "id", model.getID() );
        json.put( "humanizedId", model.getID().replaceAll( "_", " " ) );
        json.put( "shortName", model.getShortName( now.getMillis() ) );
        json.put( "name", model.getName( now.getMillis() ) );

        final DateTime local = now.plus( model.getOffsetFromLocal( now.getMillis() ) );
        final Period offsetPeriod = new Period( now, local );
        json.put( "offset", getHoursAsHumanReadable( offsetPeriod ) );

        return json;
    }

    private String getHoursAsHumanReadable( Period offsetPeriod )
    {
        final StringBuilder s = new StringBuilder();
        if ( offsetPeriod.getMinutes() < 0 )
        {
            s.append( "-" );
        }
        else
        {
            s.append( "+" );
        }

        final int hours = offsetPeriod.getHours();

        if ( hours < 10 && hours > ( -10 ) )
        {
            s.append( "0" );
        }
        s.append( Math.abs( hours ) );
        s.append( ":" );

        final int minutes = offsetPeriod.getMinutes();
        if ( minutes < 10 )
        {
            s.append( "0" );
        }
        s.append( minutes );
        return s.toString();
    }
}
