package com.enonic.xp.app;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleEvent;

import com.google.common.annotations.Beta;

@Beta
public enum ApplicationEventType
{
    INSTALLED( BundleEvent.INSTALLED ),
    RESOLVED( BundleEvent.RESOLVED ),
    LAZY_ACTIVATION( BundleEvent.LAZY_ACTIVATION ),
    STARTING( BundleEvent.STARTING ),
    STARTED( BundleEvent.STARTED ),
    STOPPING( BundleEvent.STOPPING ),
    STOPPED( BundleEvent.STOPPED ),
    UPDATED( BundleEvent.UPDATED ),
    UNRESOLVED( BundleEvent.UNRESOLVED ),
    UNINSTALLED( BundleEvent.UNINSTALLED );

    private static final Map<Integer, ApplicationEventType> LOOKUP_TABLE = new HashMap<>();

    static
    {
        for ( final ApplicationEventType applicationEventType : ApplicationEventType.values() )
        {
            LOOKUP_TABLE.put( applicationEventType.bundleEventId, applicationEventType );
        }
    }

    private final int bundleEventId;

    private ApplicationEventType( final int bundleEventId )
    {
        this.bundleEventId = bundleEventId;
    }

    public static ApplicationEventType fromBundleEvent( final BundleEvent bundle )
    {
        return LOOKUP_TABLE.get( bundle.getType() );
    }
}
