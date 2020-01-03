package com.enonic.xp.web.jetty.impl.session;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.SessionCacheFactory;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JettySessionStorageConfiguratorImplTest
{
    @Mock
    private ClusterConfig clusterConfig;

    @Mock
    private SessionDataStoreFactory sessionDataStoreFactory;

    @Mock
    private SessionCacheFactory sessionCacheFactory;

    @Mock
    private Server server;

    @Test
    void configure()
    {
        when( clusterConfig.name() ).thenReturn( ClusterNodeId.from( "localNodeName" ) );

        final JettySessionStorageConfiguratorImpl jettySessionStorageConfigurator =
            new JettySessionStorageConfiguratorImpl( clusterConfig, sessionDataStoreFactory, sessionCacheFactory );

        jettySessionStorageConfigurator.configure( server );

        verify( server ).addBean( same( sessionDataStoreFactory ) );
        verify( server ).addBean( same( sessionCacheFactory ) );

        final ArgumentCaptor<SessionIdManager> captor = ArgumentCaptor.forClass( SessionIdManager.class );
        verify( server ).setSessionIdManager( captor.capture() );

        assertEquals( "localNodeName", captor.getValue().getWorkerName() );
    }
}
