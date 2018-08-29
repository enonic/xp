package com.enonic.xp.elasticsearch.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.node.Node;
import org.elasticsearch.transport.TransportService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;

public class ElasticsearchActivatorTest
{
    private BundleContext context;

    private ElasticsearchActivator activator;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ServiceRegistration<Node> nodeReg;

    private ServiceRegistration<AdminClient> adminClientReg;

    private ServiceRegistration<ClusterAdminClient> clusterAdminClientReg;

    private ServiceRegistration<ClusterService> clusterServiceReg;

    private ServiceRegistration<TransportService> transportServiceReg;

    @Before
    public void setup()
        throws Exception
    {
        this.context = Mockito.mock( BundleContext.class );
        this.activator = new ElasticsearchActivator();
        this.activator.setClusterConfig( new ClusterConfig()
        {
            @Override
            public NodeDiscovery discovery()
            {
                return () -> {
                    final InetAddress local1;
                    final InetAddress local2;
                    try
                    {
                        local1 = InetAddress.getByName( "localhost" );
                        local2 = InetAddress.getByName( "192.168.0.1" );
                    }
                    catch ( UnknownHostException e )
                    {
                        throw new RuntimeException( e );
                    }
                    return Lists.newArrayList( local1, local2 );
                };
            }

            @Override
            public ClusterNodeId name()
            {
                return ClusterNodeId.from( "ClusterNodeId" );
            }

            @Override
            public boolean isEnabled()
            {
                return true;
            }

            @Override
            public String networkPublishHost()
            {
                return "127.0.0.1";
            }

            @Override
            public String networkHost()
            {
                return "127.0.0.1";
            }

            @Override
            public boolean isSessionReplicationEnabled()
            {
                return true;
            }
        } );

        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );

        this.nodeReg = mockRegisterService( Node.class );
        this.adminClientReg = mockRegisterService( AdminClient.class );
        this.clusterAdminClientReg = mockRegisterService( ClusterAdminClient.class );
        this.clusterServiceReg = mockRegisterService( ClusterService.class );
        this.transportServiceReg = mockRegisterService( TransportService.class );
    }

    @Test
    public void testLifeCycle()
        throws Exception
    {
        final Map<String, String> map = Maps.newHashMap();

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
