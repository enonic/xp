package com.enonic.xp.lib.scheduler.mapper;

import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class OneTimeCalendarMapper
    implements MapSerializable
{
    private final OneTimeCalendar calendar;

    public OneTimeCalendarMapper( final OneTimeCalendar calendar )
    {
        this.calendar = calendar;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "value", calendar.getValue().toString() );
        gen.value( "type", calendar.getType() );
    }
}
