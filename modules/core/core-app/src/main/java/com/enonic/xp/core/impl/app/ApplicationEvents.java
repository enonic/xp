package com.enonic.xp.core.impl.app;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleEvent;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event2;

public class ApplicationEvents
{
    private static final Map<Integer, String> STATE_LOOKUP_TABLE = new HashMap<>();

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

    public static Event2 event( BundleEvent bundleEvent )
    {
        return Event2.create( "application" ).
            distributed( false ).
            value( "applicationKey", ApplicationKey.from( bundleEvent.getBundle() ) ).
            value( "eventType", STATE_LOOKUP_TABLE.get( bundleEvent.getType() ) ).
            build();
    }

}
