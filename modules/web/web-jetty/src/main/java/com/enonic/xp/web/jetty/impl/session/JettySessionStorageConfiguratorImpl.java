package com.enonic.xp.web.jetty.impl.session;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionCacheFactory;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.cluster.ClusterConfig;

@Component(immediate = true)
public class JettySessionStorageConfiguratorImpl
    implements JettySessionStorageConfigurator
{
    private final String workerName;

    private final SessionDataStoreFactory sessionDataStoreFactory;

    private final SessionCacheFactory sessionCacheFactory;

    @Activate
    public JettySessionStorageConfiguratorImpl( @Reference final ClusterConfig clusterConfig,
                                                @Reference final SessionDataStoreFactory sessionDataStoreFactory,
                                                @Reference final SessionCacheFactory sessionCacheFactory )
    {
        this.workerName = clusterConfig.name().toString();
        this.sessionDataStoreFactory = sessionDataStoreFactory;
        this.sessionCacheFactory = sessionCacheFactory;
    }

    @Override
    public void configure( final Server server )
    {
        final DefaultSessionIdManager sessionManager = new DefaultSessionIdManager( server );

        server.addBean( sessionDataStoreFactory );
        server.addBean( sessionCacheFactory );
        sessionManager.setWorkerName( workerName );

        server.setSessionIdManager( sessionManager );
    }
}