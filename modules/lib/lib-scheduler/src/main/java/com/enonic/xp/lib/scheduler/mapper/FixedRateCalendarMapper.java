package com.enonic.xp.lib.scheduler.mapper;

import com.enonic.xp.scheduler.FixedRateCalendar;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class FixedRateCalendarMapper
    implements MapSerializable
{
    private final FixedRateCalendar calendar;

    public FixedRateCalendarMapper( final FixedRateCalendar calendar )
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
