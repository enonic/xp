package com.enonic.xp.server.impl.status.check;

import java.util.List;

import org.osgi.framework.BundleContext;

public final class HealthOSGIStateCheck
    extends OSGIStateCheck
{
    private static final List<String> TRACKED_SERVICE_NAMES =
        List.of( "org.elasticsearch.client.Client", "org.elasticsearch.client.AdminClient", "org.elasticsearch.client.ClusterAdminClient" );

    public HealthOSGIStateCheck( final BundleContext bundleContext )
    {
        super( bundleContext );
    }

    @Override
    List<String> getServicesToTrack()
    {
        return TRACKED_SERVICE_NAMES;
    }
}
