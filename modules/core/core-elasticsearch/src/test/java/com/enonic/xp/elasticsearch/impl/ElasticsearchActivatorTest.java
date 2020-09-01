package com.enonic.xp.elasticsearch.impl;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.node.Node;
import org.elasticsearch.transport.TransportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;

import static org.mockito.Mockito.when;

@Tag("elasticsearch")
@ExtendWith(MockitoExtension.class)
class ElasticsearchActivatorTest
{
    @Mock
    private BundleContext context;

    @Mock(stubOnly = true, answer = Answers.RETURNS_DEEP_STUBS)
    private ClusterConfig clusterConfig;

    private ElasticsearchActivator activator;

    @TempDir
    public Path temporaryFolder;

    private ServiceRegistration<Node> nodeReg;

    private ServiceRegistration<AdminClient> adminClientReg;

    private ServiceRegistration<ClusterAdminClient> clusterAdminClientReg;

    private ServiceRegistration<ClusterService> clusterServiceReg;

    private ServiceRegistration<TransportService> transportServiceReg;

    @BeforeEach
    void setup()
        throws Exception
    {
        this.activator = new ElasticsearchActivator();

        when( clusterConfig.isEnabled() ).thenReturn( true );
        when( clusterConfig.discovery().get() ).
            thenReturn( List.of( InetAddress.getByName( "localhost" ), InetAddress.getByName( "127.1.0.1" ) ) );
        when( clusterConfig.name() ).thenReturn( ClusterNodeId.from( "local-node" ) );
        when( clusterConfig.networkHost() ).thenReturn( "127.0.0.1" );
        when( clusterConfig.networkPublishHost() ).thenReturn( "127.0.0.1" );

        this.activator.setClusterConfig( clusterConfig );

        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );

        this.nodeReg = mockRegisterService( Node.class );
        this.adminClientReg = mockRegisterService( AdminClient.class );
        this.clusterAdminClientReg = mockRegisterService( ClusterAdminClient.class );
        this.clusterServiceReg = mockRegisterService( ClusterService.class );
        this.transportServiceReg = mockRegisterService( TransportService.class );
    }

    @Test
    void testLifeCycle()
    {
        final Map<String, String> map = new HashMap<>();

        this.activator.activate( this.context, map );

        verifyRegisterService( Node.class );
        verifyRegisterService( AdminClient.class );
        verifyRegisterService( ClusterAdminClient.class );
        verifyRegisterService( ClusterService.class );
        verifyRegisterService( TransportService.class );

        this.activator.deactivate();
        verifyUnregisterService( this.nodeReg );
        verifyUnregisterService( this.adminClientReg );
        verifyUnregisterService( this.clusterAdminClientReg );
        verifyUnregisterService( this.clusterServiceReg );
        verifyUnregisterService( this.transportServiceReg );
    }

    private <T> void verifyRegisterService( final Class<T> type )
    {
        Mockito.verify( this.context, Mockito.times( 1 ) ).registerService( Mockito.eq( type ), Mockito.any( type ), Mockito.any() );
    }

    private <T> void verifyUnregisterService( final ServiceRegistration<T> reg )
    {
        Mockito.verify( reg, Mockito.times( 1 ) ).unregister();
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceRegistration<T> mockRegisterService( final Class<T> type )
    {
        final ServiceRegistration<T> reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( this.context.registerService( Mockito.eq( type ), Mockito.any( type ), Mockito.any() ) ).thenReturn( reg );
        return reg;
    }
}
