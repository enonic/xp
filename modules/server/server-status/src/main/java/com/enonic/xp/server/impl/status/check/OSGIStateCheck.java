package com.enonic.xp.server.impl.status.check;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

abstract class OSGIStateCheck
    implements StateCheck
{
    private final Map<String, ServiceTracker<?, ?>> trackers;

    OSGIStateCheck( final BundleContext bundleContext )
    {
        this.trackers = getServicesToTrack().stream()
            .collect( Collectors.toMap( service -> service, service -> new ServiceTracker<>( bundleContext, service, null ) ) );

        this.trackers.values().forEach( ServiceTracker::open );
    }

    abstract List<String> getServicesToTrack();

    @Override
    public StateCheckResult check()
    {
        final StateCheckResult.Builder result = StateCheckResult.create();

        trackers.entrySet()
            .stream()
            .filter( entry -> entry.getValue().isEmpty() )
            .forEach( entry -> result.addErrorMessage( String.format( "[%s] service in not available", entry.getKey() ) ) );

        return result.build();
    }

    @Override
    public void deactivate()
    {
        trackers.values().forEach( ServiceTracker::close );
    }
}
