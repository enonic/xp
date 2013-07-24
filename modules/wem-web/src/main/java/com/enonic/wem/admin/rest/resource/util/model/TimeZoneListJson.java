package com.enonic.wem.admin.rest.resource.util.model;

import java.util.List;

import org.joda.time.DateTimeZone;

import com.google.common.collect.ImmutableList;

public class TimeZoneListJson
{
    private final ImmutableList<TimeZoneJson> list;

    public TimeZoneListJson( final List<DateTimeZone> timeZones )
    {
        final ImmutableList.Builder<TimeZoneJson> builder = ImmutableList.builder();
        for ( final DateTimeZone zone : timeZones )
        {
            builder.add( new TimeZoneJson( zone ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<TimeZoneJson> getTimezones()
    {
        return this.list;
    }
}
