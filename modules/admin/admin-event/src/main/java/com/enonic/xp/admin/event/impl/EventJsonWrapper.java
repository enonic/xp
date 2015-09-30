package com.enonic.xp.admin.event.impl;

import com.enonic.xp.admin.event.impl.json.EventJson;
import com.enonic.xp.event.Event;

final class EventJsonWrapper
{
    private final Event event;

    private final EventJson eventJson;

    public EventJsonWrapper( final Event event, final EventJson eventJson )
    {
        this.event = event;
        this.eventJson = eventJson;
    }

    public String getType()
    {
        return event.getClass().getSimpleName();
    }

    public EventJson getEvent()
    {
        return eventJson;
    }
}
