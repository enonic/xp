package com.enonic.xp.server.internal.deploy.health;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

import com.enonic.xp.status.health.HealthCheck;
import com.enonic.xp.status.health.HealthCheckResult;

@Component(immediate = true, service = HealthCheck.class)
public class OSGIHealthCheck
    implements HealthCheck
{
    private static final List<String> TRACKED_SERVICE_NAMES =
        List.of( "org.elasticsearch.client.Client", "org.elasticsearch.client.AdminClient", "org.elasticsearch.client.ClusterAdminClient" );

    private final Map<ServiceTracker<?, ?>, String> trackers;

    @Activate
    public OSGIHealthCheck( final BundleContext bundleContext )
    {
        this.trackers = TRACKED_SERVICE_NAMES.stream()
            .collect( Collectors.toMap( service -> new ServiceTracker<>( bundleContext, service, null ), service -> service ) );

        this.trackers.keySet().forEach( ServiceTracker::open );
    }

    @Deactivate
    public void deactivate()
    {
        trackers.keySet().forEach( ServiceTracker::close );
    }

    @Override
    public HealthCheckResult isHealthy()
    {
        final HealthCheckResult.Builder result = HealthCheckResult.create();

        trackers.entrySet()
            .stream()
            .filter( entry -> entry.getKey().isEmpty() )
            .forEach( entry -> result.addErrorMessage( String.format( "[%s] service in not available", entry.getValue() ) ) );

        return result.build();
    }

}
