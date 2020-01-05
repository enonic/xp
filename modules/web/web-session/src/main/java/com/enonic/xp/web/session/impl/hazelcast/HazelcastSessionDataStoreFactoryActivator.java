package com.enonic.xp.web.session.impl.hazelcast;

import org.eclipse.jetty.hazelcast.session.HazelcastSessionDataStoreFactory;
import org.eclipse.jetty.server.session.NullSessionCacheFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.web.session.impl.AbstractSessionDataStoreFactoryActivator;
import com.enonic.xp.web.session.impl.WebSessionConfig;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.session")
public class HazelcastSessionDataStoreFactoryActivator
    extends AbstractSessionDataStoreFactoryActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( HazelcastSessionDataStoreFactoryActivator.class );

    private final ClusterConfig clusterConfig;

    private final HazelcastInstance hazelcastInstance;

    @Activate
    public HazelcastSessionDataStoreFactoryActivator( final BundleContext bundleContext, @Reference final ClusterConfig clusterConfig,
                                                      @Reference final HazelcastInstance hazelcastInstance )
    {
        super( bundleContext );
        this.clusterConfig = clusterConfig;
        this.hazelcastInstance = hazelcastInstance;
    }

    @Activate
    public void activate( final WebSessionConfig webSessionConfig )
    {
        if ( clusterConfig.isEnabled() && clusterConfig.isSessionReplicationEnabled() )
        {
            final HazelcastSessionDataStoreFactory sessionDataStoreFactory = new HazelcastSessionDataStoreFactory();
            sessionDataStoreFactory.setSavePeriodSec( webSessionConfig.session_save_period() );
            sessionDataStoreFactory.setHazelcastInstance( hazelcastInstance );

            NullSessionCacheFactory sessionCacheFactory = new NullSessionCacheFactory();
            sessionCacheFactory.setSaveOnCreate( true );
            sessionCacheFactory.setRemoveUnloadableSessions( true );
            sessionCacheFactory.setFlushOnResponseCommit( true );
            registerServices( sessionDataStoreFactory, sessionCacheFactory );
        }
    }

    @Deactivate
    public void deactivate()
    {
        unregisterServices();
    }
}