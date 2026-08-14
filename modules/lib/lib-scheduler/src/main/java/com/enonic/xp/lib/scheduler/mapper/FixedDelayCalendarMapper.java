package com.enonic.xp.lib.scheduler.mapper;

import com.enonic.xp.scheduler.FixedDelayCalendar;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class FixedDelayCalendarMapper
    implements MapSerializable
{
    private final FixedDelayCalendar calendar;

    public FixedDelayCalendarMapper( final FixedDelayCalendar calendar )
    {
        this.calendar = calendar;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "value", calendar.getDuration().toString() );
        gen.value( "type", calendar.getType() );
    }
}
