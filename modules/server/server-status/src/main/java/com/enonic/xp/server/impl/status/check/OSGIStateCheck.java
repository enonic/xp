package com.enonic.xp.server.impl.status.check;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public final class OSGIStateCheck
{

    private final Map<String, ServiceTracker<?, ?>> trackers;

    public OSGIStateCheck( final BundleContext bundleContext, final Set<String> servicesToTrack )
    {
        this.trackers = servicesToTrack.stream()
            .collect( Collectors.toMap( Function.identity(), service -> new ServiceTracker<>( bundleContext, service, null ) ) );

        this.trackers.values().forEach( ServiceTracker::open );
    }

    public StateCheckResult check()
    {
        final StateCheckResult.Builder result = StateCheckResult.create();

        trackers.entrySet()
            .stream()
            .filter( entry -> entry.getValue().isEmpty() )
            .forEach( entry -> result.addErrorMessage( String.format( "[%s] service in not available", entry.getKey() ) ) );

        return result.build();
    }

    public void deactivate()
    {
        trackers.values().forEach( ServiceTracker::close );
    }
}
