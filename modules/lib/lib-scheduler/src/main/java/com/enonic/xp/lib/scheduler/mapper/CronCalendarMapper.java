package com.enonic.xp.lib.scheduler.mapper;

import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class CronCalendarMapper
    implements MapSerializable
{
    private final CronCalendar calendar;

    public CronCalendarMapper( final CronCalendar calendar )
    {
        this.calendar = calendar;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "value", calendar.getCronValue() );
        gen.value( "timeZone", calendar.getTimeZone().getID() );
        gen.value( "type", calendar.getType() );
    }
}
