package com.enonic.xp.core.impl.app.event;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;

public final class ApplicationEvents
{
    private ApplicationEvents()
    {
    }

    public static final String EVENT_TYPE = "application";

    public static final String APPLICATION_KEY_KEY = "applicationKey";

    public static final String SYSTEM_APPLICATION = "systemApplication";

    public static final String EVENT_TYPE_KEY = "eventType";

    public static final String INSTALLED = "INSTALLED";

    public static final String STARTED = "STARTED";

    public static final String STOPPED = "STOPPED";

    public static final String UNINSTALLED = "UNINSTALLED";

    public static Event installed( ApplicationKey applicationKey )
    {
        return event( applicationKey, INSTALLED );
    }

    public static Event uninstalled( ApplicationKey applicationKey )
    {
        return event( applicationKey, UNINSTALLED );
    }

    public static Event started( ApplicationKey applicationKey )
    {
        return event( applicationKey, STARTED );
    }

    public static Event stopped( ApplicationKey applicationKey )
    {
        return event( applicationKey, STOPPED );
    }


    private static Event event( final ApplicationKey applicationKey, final String type )
    {
        return Event.create( EVENT_TYPE )
            .distributed( false )
            .value( APPLICATION_KEY_KEY, applicationKey )
            .value( SYSTEM_APPLICATION, false )
            .value( EVENT_TYPE_KEY, type )
            .build();
    }
}
