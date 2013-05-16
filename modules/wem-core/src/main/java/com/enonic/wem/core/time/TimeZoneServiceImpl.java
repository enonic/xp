package com.enonic.wem.core.time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeZone;


public class TimeZoneServiceImpl
    implements TimeZoneService
{
    private final List<DateTimeZone> timeZones = new ArrayList<DateTimeZone>();

    public TimeZoneServiceImpl()
    {
        Set<String> ids = DateTimeZone.getAvailableIDs();
        this.timeZones.add( DateTimeZone.UTC );
        for ( final String id : ids )
        {
            if ( !id.equals( "UTC" ) )
            {
                this.timeZones.add( DateTimeZone.forID( id ) );
            }
        }
    }

    public List<DateTimeZone> getTimeZones()
    {
        return Collections.unmodifiableList( this.timeZones );
    }
}
