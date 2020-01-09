package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.NullSessionDataStoreFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterConfig;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.session")
public class NullSessionDataStoreFactoryActivator
    extends AbstractSessionDataStoreFactoryActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( NullSessionDataStoreFactoryActivator.class );

    private final ClusterConfig clusterConfig;

    @Activate
    public NullSessionDataStoreFactoryActivator( final BundleContext bundleContext, @Reference final ClusterConfig clusterConfig )
    {
        super( bundleContext );
        this.clusterConfig = clusterConfig;
    }

    @Activate
    public void activate( final WebSessionConfig config )
    {
        if ( clusterConfig.isEnabled() && clusterConfig.isSessionReplicationEnabled() )
        {
            LOG.debug( "Don't activate NullSessionDataStore when session replication is enabled" );
        }
        else
        {
            final NullSessionDataStoreFactory sessionDataStoreFactory = new NullSessionDataStoreFactory();
            sessionDataStoreFactory.setSavePeriodSec( config.session_save_period() );

            registerServices( sessionDataStoreFactory, DefaultSessionCache::new );
        }
    }

    @Deactivate
    public void deactivate()
    {
        unregisterServices();
    }
}
