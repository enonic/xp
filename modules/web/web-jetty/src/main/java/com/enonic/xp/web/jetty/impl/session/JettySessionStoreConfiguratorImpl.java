package com.enonic.xp.web.jetty.impl.session;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.session.DefaultSessionIdManager;
import org.eclipse.jetty.session.HouseKeeper;
import org.eclipse.jetty.session.SessionCacheFactory;
import org.eclipse.jetty.session.SessionDataStoreFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class JettySessionStoreConfiguratorImpl
    implements JettySessionStoreConfigurator
{
    private final SessionDataStoreFactory sessionDataStoreFactory;

    private final SessionCacheFactory sessionCacheFactory;

    @Activate
    public JettySessionStoreConfiguratorImpl( @Reference final SessionDataStoreFactory sessionDataStoreFactory,
                                              @Reference final SessionCacheFactory sessionCacheFactory )
    {
        this.sessionDataStoreFactory = sessionDataStoreFactory;
        this.sessionCacheFactory = sessionCacheFactory;
    }

    @Override
    public void configure( final Server server, final int scavengeIntervalSeconds )
    {
        final DefaultSessionIdManager sessionManager = new DefaultSessionIdManager( server );

        server.addBean( sessionDataStoreFactory );
        server.addBean( sessionCacheFactory );
        sessionManager.setWorkerName( "" );

        // Idle-expired sessions are only invalidated when the HouseKeeper scavenges. Jetty's own default
        // (10 min) can delay session-end events long after the actual timeout, so set an interval derived
        // from the configured session timeout instead.
        final HouseKeeper houseKeeper = new HouseKeeper();
        try
        {
            houseKeeper.setIntervalSec( scavengeIntervalSeconds );
        }
        catch ( final Exception e )
        {
            throw new IllegalStateException( e );
        }
        sessionManager.setSessionHouseKeeper( houseKeeper );

        server.addBean( sessionManager, true );
    }
}
