package com.enonic.xp.web.session.impl.ignite;

import org.apache.ignite.Ignite;
import org.eclipse.jetty.server.session.NullSessionCacheFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.web.session.impl.AbstractSessionDataStoreFactoryActivator;
import com.enonic.xp.web.session.impl.WebSessionConfig;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.session")
public class IgniteSessionDataStoreFactoryActivator
    extends AbstractSessionDataStoreFactoryActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( IgniteSessionDataStoreFactoryActivator.class );

    private final ClusterConfig clusterConfig;

    private final Ignite ignite;

    @Activate
    public IgniteSessionDataStoreFactoryActivator( final BundleContext bundleContext, @Reference final ClusterConfig clusterConfig,
                                                   @Reference final Ignite ignite )
    {
        super( bundleContext );
        this.clusterConfig = clusterConfig;
        this.ignite = ignite;
    }

    @Activate
    public void activate( final WebSessionConfig config )
    {
        if ( clusterConfig.isEnabled() && clusterConfig.isSessionReplicationEnabled() )
        {
            final IgniteSessionDataStoreFactory sessionDataStoreFactory = new IgniteSessionDataStoreFactory( ignite, config );
            sessionDataStoreFactory.setSavePeriodSec( config.session_save_period() );

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
