package com.enonic.xp.app;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleEvent;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.event.Event;

@Beta
public final class ApplicationEvent
    implements Event
{
    private final BundleEvent bundleEvent;

    private final String state;

    private final ApplicationKey applicationKey;

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

    public ApplicationEvent( final BundleEvent bundleEvent )
    {
        this.bundleEvent = bundleEvent;
        this.applicationKey = ApplicationKey.from( bundleEvent.getBundle() );
        this.state = STATE_LOOKUP_TABLE.get( bundleEvent.getType() );
    }

    public String getState()
    {
        return this.state;
    }

    public ApplicationKey getKey()
    {
        return this.applicationKey;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "state", state ).
            add( "applicationKey", applicationKey ).
            omitNullValues().
            toString();
    }
}
