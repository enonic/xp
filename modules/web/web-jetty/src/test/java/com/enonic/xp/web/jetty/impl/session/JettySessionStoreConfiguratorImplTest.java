package com.enonic.xp.web.jetty.impl.session;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.session.DefaultSessionIdManager;
import org.eclipse.jetty.session.SessionCacheFactory;
import org.eclipse.jetty.session.SessionDataStoreFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JettySessionStoreConfiguratorImplTest
{
    @Mock
    private SessionDataStoreFactory sessionDataStoreFactory;

    @Mock
    private SessionCacheFactory sessionCacheFactory;

    @Mock
    private Server server;

    @Test
    void configure()
    {
        final JettySessionStoreConfiguratorImpl jettySessionStorageConfigurator =
            new JettySessionStoreConfiguratorImpl( sessionDataStoreFactory, sessionCacheFactory );

        jettySessionStorageConfigurator.configure( server, 42 );

        verify( server ).addBean( same( sessionDataStoreFactory ) );
        verify( server ).addBean( same( sessionCacheFactory ) );

        final ArgumentCaptor<DefaultSessionIdManager> captor = ArgumentCaptor.forClass( DefaultSessionIdManager.class );
        verify( server ).addBean( captor.capture(), eq( true ) );

        assertEquals( "", captor.getValue().getWorkerName() );
        assertEquals( 42, captor.getValue().getSessionHouseKeeper().getIntervalSec() );
    }
}
