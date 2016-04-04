package com.enonic.xp.core.impl.app.event;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleEvent;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;

public final class ApplicationEvents
{
    public static final String EVENT_TYPE = "application";

    public static final String APPLICATION_KEY_KEY = "applicationKey";

    public static final String APPLICATION_URL_KEY = "applicationUrl";

    public static final String INSTALLATION_PROGRESS_KEY = "progress";

    public static final String EVENT_TYPE_KEY = "eventType";

    public static final String INSTALLED = "INSTALLED";

    public static final String RESOLVED = "RESOLVED";

    public static final String LAZY_ACTIVATION = "LAZY_ACTIVATION";

    public static final String STARTING = "STARTING";

    public static final String STARTED = "STARTED";

    public static final String STOPPING = "STOPPING";

    public static final String STOPPED = "STOPPED";

    public static final String UPDATED = "UPDATED";

    public static final String UNRESOLVED = "UNRESOLVED";

    public static final String UNINSTALLED = "UNINSTALLED";

    public static final String INSTALLATION_PROGRESS = "PROGRESS";

    private static final Map<Integer, String> STATE_LOOKUP_TABLE = new HashMap<>();

    static
    {
        STATE_LOOKUP_TABLE.put( BundleEvent.INSTALLED, INSTALLED );
        STATE_LOOKUP_TABLE.put( BundleEvent.RESOLVED, RESOLVED );
        STATE_LOOKUP_TABLE.put( BundleEvent.LAZY_ACTIVATION, LAZY_ACTIVATION );
        STATE_LOOKUP_TABLE.put( BundleEvent.STARTING, STARTING );
        STATE_LOOKUP_TABLE.put( BundleEvent.STARTED, STARTED );
        STATE_LOOKUP_TABLE.put( BundleEvent.STOPPING, STOPPING );
        STATE_LOOKUP_TABLE.put( BundleEvent.STOPPED, STOPPED );
        STATE_LOOKUP_TABLE.put( BundleEvent.UPDATED, UPDATED );
        STATE_LOOKUP_TABLE.put( BundleEvent.UNRESOLVED, UNRESOLVED );
        STATE_LOOKUP_TABLE.put( BundleEvent.UNINSTALLED, UNINSTALLED );
    }

    public static Event event( BundleEvent bundleEvent )
    {
        return Event.create( EVENT_TYPE ).
            distributed( false ).
            value( APPLICATION_KEY_KEY, ApplicationKey.from( bundleEvent.getBundle() ) ).
            value( EVENT_TYPE_KEY, STATE_LOOKUP_TABLE.get( bundleEvent.getType() ) ).
            build();
    }


    public static Event progress( final String url, final int progress )
    {
        return Event.create( EVENT_TYPE ).
            distributed( false ).
            value( EVENT_TYPE_KEY, INSTALLATION_PROGRESS ).
            value( APPLICATION_URL_KEY, url ).
            value( INSTALLATION_PROGRESS_KEY, progress ).
            build();
    }

}
