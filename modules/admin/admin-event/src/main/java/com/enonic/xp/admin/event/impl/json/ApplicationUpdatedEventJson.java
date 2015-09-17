package com.enonic.xp.admin.event.impl.json;

import com.enonic.xp.app.ApplicationUpdatedEvent;

public final class ApplicationUpdatedEventJson
    implements EventJson
{
    private final String eventType;

    private final String applicationKey;

    public ApplicationUpdatedEventJson( final ApplicationUpdatedEvent event )
    {
        this.eventType = event.getEventType().name();
        this.applicationKey = event.getApplicationKey().toString();
    }

    public String getEventType()
    {
        return eventType;
    }

    public String getApplicationKey()
    {
        return applicationKey;
    }
}
